/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.multimedia.action;

import no.kantega.publishing.admin.viewcontroller.AdminController;
import no.kantega.publishing.common.exception.InvalidImageFormatException;
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.util.MultimediaHelper;
import no.kantega.publishing.multimedia.ImageEditor;
import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.multimedia.metadata.MultimediaMetadataExtractor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.multipart.MultipartFile;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipEntry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.zip.ZipException;
import java.io.*;
import java.nio.charset.Charset;

import com.glaforge.i18n.io.CharsetToolkit;
import no.kantega.publishing.admin.content.util.AttachmentBlacklistHelper;

public class UploadMultimediaAction extends AdminController {

    private ImageEditor imageEditor;
    private List<MultimediaMetadataExtractor> multimediaMetadataExtractors;

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters parameters = new RequestParameters(request, "utf-8");

        MultimediaService mediaService = new MultimediaService(request);

        int parentId = parameters.getInt("parentId");

        // Save image or other file
        Multimedia parent = null;
        if (parentId != 0) {
            parent = mediaService.getMultimedia(parentId);
        }

        String name = parameters.getString("name");
        String altName = parameters.getString("altname");
        String author = parameters.getString("author");

        List<Multimedia> uploadedFiles = getUploadedFiles(parameters);

        for (Multimedia file : uploadedFiles) {

            if (file.isNew()) {
                // New file
                if (parent != null) {
                    file.setSecurityId(parent.getSecurityId());
                    file.setParentId(parentId);
                } else {
                    file.setSecurityId(0);
                }
                if (uploadedFiles.size() == 1) {
                    if (name != null && name.length() > 0) {
                        file.setName(name);
                    }
                    file.setAltname(altName);
                    file.setAuthor(author);
                }
            }

            for (MultimediaMetadataExtractor extractor : multimediaMetadataExtractors) {
                if (extractor.supportsMimeType(file.getMimeType().getType())) {
                    file = extractor.extractMetadata(file);
                }
            }

            boolean preserveImageSize = parameters.getBoolean("preserveImageSize", false);
            if (!preserveImageSize) {
                file = resizeMultimedia(file);
            }

            // Save object
            int newId = mediaService.setMultimedia(file);
            file.setId(newId);
        }


        if (uploadedFiles.size() == 1) {
            Map model = new HashMap();
            model.put("id", uploadedFiles.get(0).getId());
            return new ModelAndView(new RedirectView("EditMultimedia.action"), model);
        } else {
            return new ModelAndView(new RedirectView("Navigate.action"));
        }
    }

    private List<Multimedia> getUploadedFiles(RequestParameters parameters) throws IOException {
        List<Multimedia> multimediaList = new ArrayList<Multimedia>();

        List<MultipartFile> multipartFiles = parameters.getFiles("file");

        for (MultipartFile multipartFile : multipartFiles) {
            if (multipartFile != null && !AttachmentBlacklistHelper.isFileTypeInBlacklist(multipartFile)) {
                if (isZipFile(multipartFile)) {
                    multimediaList.addAll(unpackFilesFromZipArchive(multipartFile));
                } else {
                    multimediaList.add(getNormalFile(parameters, multipartFile));
                }
            }
        }

        return multimediaList;
    }

    private boolean isZipFile(MultipartFile multipartFile) {
        String filename = multipartFile.getOriginalFilename();
        return filename.toLowerCase().endsWith(".zip");
    }

    private Multimedia getNormalFile(RequestParameters parameters, MultipartFile multipartFile) throws IOException {
        String filename = multipartFile.getOriginalFilename();

        // Upload of single file - new or replace existing
        filename = removeDirectoryFromFilename(filename);

        Multimedia mm = null;
        int id = parameters.getInt("id");
        if (id != -1) {
            MultimediaService mediaService = new MultimediaService(parameters.getRequest());
            mm = mediaService.getMultimedia(id);
        }
        if (mm == null) {
            mm = new Multimedia();
            String name = removeFileExtension(filename);
            mm.setName(name);
        }
        mm.setData(multipartFile.getBytes());
        mm.setFilename(filename);
        return mm;
    }

    private String removeFileExtension(String filename) {
        String name;
        if (filename.indexOf(".") != -1) {
            name = filename.substring(0, filename.lastIndexOf('.'));
        } else {
            name = filename;
        }
        return name;
    }

    /**
     * Unzip zip file and return a list of multimedia objects
     * @param file
     * @return
     * @throws IOException
     */
    private List<Multimedia> unpackFilesFromZipArchive(MultipartFile file) throws IOException {
        List<Multimedia> files = new ArrayList<Multimedia>();
        File temp = File.createTempFile("multimedia", ".zip");
        file.transferTo(temp);

        try {
            ZipFile zipFile = new ZipFile(temp) {
                /**
                 * Override getString to try and guess encoding
                 * @param bytes
                 * @return
                 * @throws ZipException
                 */
                @Override
                protected String getString(byte[] bytes) throws ZipException {
                    if(bytes.length == 0) {
                        return "";
                    }
                    try {
                        // Default charset on windows xp compress (and winzip?)
                        final Charset defaultCharset = Charset.forName("ibm437");
                        final CharsetToolkit toolkit = new CharsetToolkit(bytes, defaultCharset);
                        final Charset charset = toolkit.guessEncoding();


                        // XP compress messes up the norwegian o with dash charachters
                        // See http://en.wikipedia.org/wiki/Code_page_437
                        String ibm = new String(bytes, defaultCharset.name());
                        if(ibm.contains("\u00a2") ||ibm.contains("\u00d5")) {
                            // Fix norwegian � and �
                            return ibm.replaceAll("\u00a2", "\u00f8").replaceAll("\u00d5", "\u00d8");
                        }

                        return new String(bytes, charset.name());
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }
            };

            List<ZipEntry> entries = Collections.list(zipFile.getEntries());
            for(ZipEntry entry : entries) {
                if (isValidEntry(entry)) {
                    Multimedia multimedia = new Multimedia();

                    byte[] data = getDataFromZipFileEntry(zipFile, entry);

                    multimedia.setData(data);

                    // Create name from filename
                    String entryFilename = entry.getName();
                    entryFilename = normalizeFilename(entryFilename);
                    entryFilename = removeDirectoryFromFilename(entryFilename);
                    multimedia.setFilename(entryFilename);

                    String name = removeFileExtension(entryFilename);
                    multimedia.setName(name);

                    files.add(multimedia);
                }

            }
            zipFile.close();
        } finally {
            temp.delete();
        }

        return files;
    }

    private byte[] getDataFromZipFileEntry(ZipFile zipFile, ZipEntry entry) throws IOException {
        InputStream zis = zipFile.getInputStream(entry);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        byte[] buf = new byte[1024];
        int len;
        while ((len = zis.read(buf)) > 0) {
            bos.write(buf, 0, len);
        }

        byte[] data = bos.toByteArray();
        bos.close();
        return data;
    }

    private String removeDirectoryFromFilename(String entryFilename) {
        if(entryFilename.contains("/")) {
            entryFilename = entryFilename.substring(entryFilename.lastIndexOf("/")+1);
        }
        return entryFilename;
    }

    private String normalizeFilename(String entryfilename) {
        // Replace composed unicode norwegian aring with the single byte aring
        return entryfilename.replaceAll("\u0061\u030a", "\u00e5");
    }

    private boolean isValidEntry(ZipEntry entry) {
        return !entry.isDirectory() && !entry.getName().startsWith("__MACOSX");
    }

    public Multimedia resizeMultimedia(Multimedia multimedia) throws InvalidImageFormatException {
        if (multimedia.getType() == MultimediaType.MEDIA && multimedia.getData() != null ) {
            if (multimedia.getMimeType().getType().indexOf("image") != -1 && (Aksess.getMaxMediaWidth() > 0 || Aksess.getMaxMediaHeight() > 0)) {
                if (multimedia.getWidth() > Aksess.getMaxMediaWidth() ||  multimedia.getHeight() > Aksess.getMaxMediaHeight()) {
                    try {
                        multimedia = imageEditor.resizeMultimedia(multimedia, Aksess.getMaxMediaWidth(), Aksess.getMaxMediaHeight());
                    } catch (IOException e) {
                        throw new SystemException(this.getClass().getName(), "IOException", e);
                    }
                }
            }
        }
        return multimedia;
    }

    public void setImageEditor(ImageEditor imageEditor) {
        this.imageEditor = imageEditor;
    }

    public void setMultimediaMetadataExtractors(List<MultimediaMetadataExtractor> multimediaMetadataExtractors) {
        this.multimediaMetadataExtractors = multimediaMetadataExtractors;
    }
}
