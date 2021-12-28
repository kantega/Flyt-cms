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

import com.glaforge.i18n.io.CharsetToolkit;
import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.admin.content.util.AttachmentBlacklistHelper;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.exception.InvalidImageFormatException;
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.multimedia.MultimediaUploadHandler;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipException;

public class UploadMultimediaAction extends AbstractController {

    private MultimediaUploadHandler multimediaUploadHandler;
    private String insertMultimediaView;

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters parameters = new RequestParameters(request, "utf-8");

        MultimediaService mediaService = new MultimediaService(request);

        int parentId = parameters.getInt("parentId");
        int contentId = -1;

        Content currentContent = null;
        boolean fileUploadedFromEditor = parameters.getBoolean("fileUploadedFromEditor", false);
        if (fileUploadedFromEditor) {
            currentContent = (Content)request.getSession().getAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT);
            if (currentContent != null) {
                contentId = currentContent.getId();
            }
        }

        // Save image or other file
        Multimedia parent = null;
        if (parentId > 0) {
            parent = mediaService.getMultimedia(parentId);
        }

        boolean preserveImageSize = parameters.getBoolean("preserveImageSize", Aksess.isPreserveImageSize());

        List<Multimedia> uploadedFiles = getUploadedFiles(parameters, preserveImageSize);

        for (Multimedia file : uploadedFiles) {
            if (file.isNew()) {
                // New file
                if (parent != null) {
                    file.setSecurityId(parent.getSecurityId());
                    file.setParentId(parentId);
                } else {
                    file.setSecurityId(0);
                    file.setContentId(contentId);
                    if (fileUploadedFromEditor) {
                        file.setParentId(-1);
                    } else {
                        file.setParentId(0);
                    }
                }
                if (uploadedFiles.size() == 1) {
                    String name = parameters.getString("name");
                    if (name != null && name.length() > 0) {
                        file.setName(name);
                    }
                    file.setAltname(parameters.getString("altname"));
                }
                file.setAuthor(parameters.getString("author"));
            }
            // Save object
            int newId = mediaService.setMultimedia(file);
            file.setId(newId);
        }


        if (fileUploadedFromEditor) {
            if (currentContent != null && currentContent.isNew()) {
                for (Multimedia uploadedFile : uploadedFiles) {
                    currentContent.addMultimedia(uploadedFile);
                }
            }
            boolean doInsertTag = parameters.getBoolean("doInsertTag", false);

            Map<String, Object> model = new HashMap<>();
            model.put("media", uploadedFiles.get(0));
            model.put("doInsertTag", doInsertTag);
            return new ModelAndView(insertMultimediaView, model);
        } else if (uploadedFiles.size() > 0) {
            List<Integer> ids = new ArrayList<>();
            for (Multimedia uploadedFile : uploadedFiles) {
                ids.add(uploadedFile.getId());
            }
            int id = ids.remove(0);

            return new ModelAndView(new RedirectView("EditMultimedia.action?id="+id), Collections.singletonMap("ids", ids));
        } else {
            return new ModelAndView(new RedirectView("Navigate.action"));
        }
    }

    private List<Multimedia> getUploadedFiles(RequestParameters parameters, boolean preserveImageSize) throws IOException, InvalidImageFormatException {
        List<Multimedia> multimediaList = new ArrayList<>();

        List<MultipartFile> multipartFiles = parameters.getFiles("file");

        for (MultipartFile multipartFile : multipartFiles) {
            if (multipartFile != null && !AttachmentBlacklistHelper.isFileTypeInBlacklist(multipartFile)) {
                if (isZipFile(multipartFile)) {
                    multimediaList.addAll(createMultimediaFromZipArchive(multipartFile, preserveImageSize));
                } else {
                    multimediaList.add(createMultimediaFromNormalFile(parameters, multipartFile, preserveImageSize));
                }
            }
        }
        return multimediaList;
    }

    private boolean isZipFile(MultipartFile multipartFile) {
        String filename = multipartFile.getOriginalFilename();
        return filename.toLowerCase().endsWith(".zip");
    }

    private Multimedia createMultimediaFromNormalFile(RequestParameters parameters, MultipartFile multipartFile, boolean preserveImageSize) throws IOException, InvalidImageFormatException {
        String filename = multipartFile.getOriginalFilename();

        // Upload of single file - new or replace existing
        filename = removeDirectoryFromFilename(filename);

        Multimedia multimedia = null;
        int id = parameters.getInt("id");
        if (id != -1) {
            MultimediaService mediaService = new MultimediaService(parameters.getRequest());
            multimedia = mediaService.getMultimedia(id);
        } else {
            multimedia = new Multimedia();
        }

        multimediaUploadHandler.updateMultimediaWithData(multimedia, multipartFile.getBytes(), filename, preserveImageSize);

        return multimedia;
    }

    /**
     * Unzip zip file and return a list of multimedia objects
     * @param file
     * @return
     * @throws IOException
     */
    private List<Multimedia> createMultimediaFromZipArchive(MultipartFile file, boolean preserveImageSize) throws IOException, InvalidImageFormatException {
        List<Multimedia> files = new ArrayList<>();
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
                            // Fix norwegian Ø and ø
                            return ibm.replaceAll("\u00a2", "\u00f8").replaceAll("\u00d5", "\u00d8");
                        }

                        return new String(bytes, charset.name());
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }
            };

            List<ZipEntry> entries = Collections.<ZipEntry>list(zipFile.getEntries());
            for(ZipEntry entry : entries) {
                if (isValidEntry(entry)) {
                    Multimedia multimedia = new Multimedia();
                    // Create name from filename
                    String entryFilename = getFileNameFromZipEntry(entry);

                    multimediaUploadHandler.updateMultimediaWithData(multimedia, getDataFromZipFileEntry(zipFile, entry), entryFilename, preserveImageSize);
                    files.add(multimedia);
                }

            }
            zipFile.close();
        } finally {
            temp.delete();
        }

        return files;
    }

    private String getFileNameFromZipEntry(ZipEntry entry) {
        String entryFilename = entry.getName();
        entryFilename = normalizeFilename(entryFilename);
        entryFilename = removeDirectoryFromFilename(entryFilename);
        return entryFilename;
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

    public void setMultimediaUploadHandler(MultimediaUploadHandler multimediaUploadHandler) {
        this.multimediaUploadHandler = multimediaUploadHandler;
    }

    public void setInsertMultimediaView(String insertMultimediaView) {
        this.insertMultimediaView = insertMultimediaView;
    }
}
