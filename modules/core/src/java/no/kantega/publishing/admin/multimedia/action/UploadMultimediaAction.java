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
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.util.MultimediaHelper;
import no.kantega.publishing.multimedia.ImageEditor;
import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.media.MimeType;
import no.kantega.commons.media.MimeTypes;
import no.kantega.commons.media.ImageInfo;
import no.kantega.commons.exception.SystemException;
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
/**
 *
 */
public class UploadMultimediaAction extends AdminController {
    private ImageEditor imageEditor;

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request, "utf-8");

        MultimediaService mediaService = new MultimediaService(request);

        int parentId = param.getInt("parentId");

        // Save image or other file
        Multimedia parent = null;
        if (parentId != 0) {
            parent = mediaService.getMultimedia(parentId);
        }

        String name = param.getString("name");
        String altName = param.getString("altname");
        String author = param.getString("author");

        List<Multimedia> multimedia = getUploadedFiles(param);

        for (Multimedia m : multimedia) {

            if (m.getId() == -1) {
                // New file
                if (parent != null) {
                    m.setSecurityId(parent.getSecurityId());
                    m.setParentId(parentId);
                } else {
                    m.setSecurityId(0);
                }
                if (multimedia.size() == 1) {
                    m.setName(name);
                    m.setAltname(altName);
                    m.setAuthor(author);
                }
            }

            String filename = m.getFilename();
            MimeType mimeType = MimeTypes.getMimeType(filename);

            if (mimeType.getType().indexOf("image") != -1 || mimeType.getType().indexOf("flash") != -1) {
                // For images and Flash we can find the dimensions
                ImageInfo ii = new ImageInfo();
                ii.setInput(new ByteArrayInputStream(m.getData()));
                if (ii.check()) {
                    m.setWidth(ii.getWidth());
                    m.setHeight(ii.getHeight());
                }
                boolean preserveImageSize = param.getBoolean("preserveImageSize", false);
                if (!preserveImageSize) {
                    m = resizeMultimedia(m);
                }
            } else if (mimeType.isDimensionRequired() && (m.getWidth() <= 0 || m.getHeight() <= 0)) {
                m.setWidth(Aksess.getDefaultMediaWidth());
                m.setHeight(Aksess.getDefaultMediaHeight());
            }

            if (filename.length() > 255) {
                filename = filename.substring(filename.length() - 255, filename.length());
            }

            m.setFilename(filename);            

            // Save object
            int newId = mediaService.setMultimedia(m);
            m.setId(newId);
        }


        if (multimedia.size() == 1) {
            Map model = new HashMap();
            model.put("id", multimedia.get(0).getId());
            return new ModelAndView(new RedirectView("EditMultimedia.action"), model);
        } else {
            return new ModelAndView(new RedirectView("Navigate.action"));
        }
    }

    private List<Multimedia> getUploadedFiles(RequestParameters param) throws IOException {
        List<Multimedia> multimedia = new ArrayList<Multimedia>();
        String filename = "";
        String fileExtension = "";

        MultipartFile file = param.getFile("file");
        if (file != null) {
            filename = file.getOriginalFilename();
            fileExtension = filename.substring(filename.length() - 3, filename.length());
            if ("zip".equalsIgnoreCase(fileExtension)) {
                // Upload of multiple files
                return getZipFiles(file);
            } else {
                // Upload of single file - new or replace existing
                if(filename.contains("/")) {
                    filename = filename.substring(filename.lastIndexOf("/")+1);
                }

                Multimedia mm = null;
                int id = param.getInt("id");
                if (id != -1) {
                    MultimediaService mediaService = new MultimediaService(param.getRequest());
                    mm = mediaService.getMultimedia(id);
                }
                if (mm == null) {
                    mm = new Multimedia();
                    String name;
                    if (filename.indexOf(".") != -1) {
                        name = filename.substring(0, filename.lastIndexOf('.'));
                    } else {
                        name = filename;
                    }
                    mm.setName(name);
                }
                mm.setData(file.getBytes());
                mm.setFilename(filename);
                multimedia.add(mm);
            }
        }

        return multimedia;
    }

    /**
     * Unzip zip file and return a list of multimedia objects
     * @param file
     * @return
     * @throws IOException
     */
    private List getZipFiles(MultipartFile file) throws IOException {
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
                            // Fix norwegian ø and Ø
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

                    InputStream zis = zipFile.getInputStream(entry);
                    Multimedia mm = new Multimedia();


                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = zis.read(buf)) > 0) {
                        bos.write(buf, 0, len);
                    }

                    byte[] data = bos.toByteArray();
                    mm.setData(data);

                    bos.close();


                    // Create name from filename
                    String name = "";
                    String entryFilename = entry.getName();
                    entryFilename = normalize(entryFilename);
                    if(entryFilename.contains("/")) {
                        entryFilename = entryFilename.substring(entryFilename.lastIndexOf("/")+1);
                    }
                    if (entryFilename.indexOf(".") != -1) {
                        name = entryFilename.substring(0, entryFilename.lastIndexOf('.'));
                    } else {
                        name = entryFilename;
                    }
                    mm.setName(name);
                    mm.setFilename(entryFilename);

                    files.add(mm);
                }

            }
            zipFile.close();
        } finally {
            temp.delete();
        }

        return files;
    }

    private String normalize(String entryfilename) {
        // Replace composed unicode norwegian aring with the single byte aring
        return entryfilename.replaceAll("\u0061\u030a", "\u00e5");
    }

    private boolean isValidEntry(ZipEntry entry) {
        return !entry.isDirectory() && !entry.getName().startsWith("__MACOSX");
    }

    public Multimedia resizeMultimedia(Multimedia multimedia) {
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
}