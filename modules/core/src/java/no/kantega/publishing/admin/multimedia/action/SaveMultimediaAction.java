/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.multimedia.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.media.ImageInfo;
import no.kantega.commons.media.MimeType;
import no.kantega.commons.media.MimeTypes;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.exception.ExceptionHandler;
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.common.util.MultimediaHelper;
import no.kantega.publishing.multimedia.ImageEditor;
import org.springframework.web.multipart.MultipartFile;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipEntry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipException;
import java.nio.charset.Charset;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import com.glaforge.i18n.io.CharsetToolkit;

public class SaveMultimediaAction implements Controller {
    private static String SOURCE = "aksess.SaveMultimediaAction";

    private ImageEditor imageEditor;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request, "utf-8");

        int id = param.getInt("id");
        int parentId = param.getInt("parentId");
        MultimediaType type = MultimediaType.getMultimediaTypeAsEnum(param.getInt("type"));

        Multimedia parent = null;

        String name = param.getString("name", 255);
        String altname = param.getString("altname", 255);
        String author = param.getString("author", 255);
        String desc = param.getString("description", 4000);
        String usage = param.getString("usage", 4000);
        int width = param.getInt("width");
        int height = param.getInt("height");

        MultipartFile file = param.getFile("file");

        String filename = "";
        String fileext = "";
        if (file != null) {
            filename = file.getOriginalFilename();
            fileext = filename.substring(filename.length() - 3, filename.length());
        }

        boolean isZip = false;
        if ("zip".equalsIgnoreCase(fileext)) {
            isZip = true;
        }

        try {
            MultimediaService mediaService = new MultimediaService(request);

            if (parentId != 0) {
                parent = mediaService.getMultimedia(parentId);
            }

            if (!isZip) {
                Multimedia mm = null;

                if (id != -1) {
                    mm = mediaService.getMultimedia(id);
                    if (mm == null) {
                        throw new SystemException("mm == null", SOURCE, null);
                    }
                } else {
                    mm = new Multimedia();
                    mm.setParentId(parentId);
                    mm.setType(type);
                    if (parent != null) {
                        mm.setSecurityId(parent.getSecurityId());
                    } else {
                        mm.setSecurityId(0);
                    }
                }

                // Basisopplysninger
                if (name == null || name.length() == 0) {
                    if (filename.indexOf(".") != -1) {
                        name = filename.substring(0, filename.lastIndexOf('.'));
                    } else {
                        name = filename;
                    }
                }
                mm.setName(name);
                mm.setAltname(altname);
                mm.setAuthor(author);
                mm.setDescription(desc);
                mm.setUsage(usage);

                boolean needsSize = false;
                if (width != -1) {
                    mm.setWidth(width);
                }
                if (height != -1) {
                    mm.setHeight(height);
                }


                if (file != null) {
                    // Lastet opp en vanlig fil
                    byte[] data = file.getBytes();
                    mm.setData(data);

                    MimeType mimeType = MimeTypes.getMimeType(filename);
                    if (mimeType.shouldConvertImage() && Aksess.isImageConversionEnabled()) {
                        // Konverterer bildet til et format som nettlesere kan vise
                        mm.setData(MultimediaHelper.convertImageFormat(data));
                        filename = filename.substring(0, filename.lastIndexOf(".") + 1) + Aksess.getOutputImageFormat();
                        mimeType = MimeTypes.getMimeType(filename);
                    }

                    if (mimeType.getType().indexOf("image") != -1 || mimeType.getType().indexOf("flash") != -1) {
                        // Dette er et bilde eller Flash fil, finn størrelse
                        ImageInfo ii = new ImageInfo();
                        ii.setInput(new ByteArrayInputStream(mm.getData()));
                        if (ii.check()) {
                            mm.setWidth(ii.getWidth());
                            mm.setHeight(ii.getHeight());
                        }
                    } else if (mimeType.isDimensionRequired() && (mm.getWidth() <= 0 || mm.getHeight() <= 0)) {
                        mm.setWidth(Aksess.getDefaultMediaWidth());
                        mm.setHeight(Aksess.getDefaultMediaHeight());
                        needsSize = true;
                    }

                    if (filename.length() > 255) {
                        filename = filename.substring(filename.length() - 255, filename.length());
                    }

                    mm.setFilename(filename);

                }

                boolean preserveImageSize = param.getBoolean("preserveImageSize", false);
                if (!preserveImageSize) {
                    mm = resizeMultimedia(mm);
                }
                int newId = mediaService.setMultimedia(mm);

                if (mm.getType() == MultimediaType.FOLDER) {
                    response.sendRedirect("multimedia.jsp?activetab=viewfolder&id=" + newId + "&updatetree=true");
                } else {
                    if (needsSize) {
                        response.sendRedirect("multimedia.jsp?activetab=editmultimedia&id=" + newId + "&updatetree=true");
                    } else {
                        response.sendRedirect("multimedia.jsp?activetab=viewmultimedia&id=" + newId + "&updatetree=true");
                    }

                }
            } else {
                // Zip fil
                if (id == -1) {
                    File temp = File.createTempFile("multimedia", ".zip");
                    file.transferTo(temp);

                    try {
                        // Lastet opp zipfil, legges inn som separate bilder
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

                                mm.setParentId(parentId);
                                mm.setType(type);
                                if (parent != null) {
                                    mm.setSecurityId(parent.getSecurityId());
                                }

                                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                                byte[] buf = new byte[1024];
                                int len;
                                while ((len = zis.read(buf)) > 0) {
                                    bos.write(buf, 0, len);
                                }

                                byte[] data = bos.toByteArray();
                                mm.setData(data);

                                bos.close();

                                // Basisopplysninger
                                mm.setAuthor(author);
                                mm.setDescription(desc);
                                mm.setUsage(usage);

                                // Navn + filnavn
                                String entryfilename = entry.getName();
                                entryfilename = normalize(entryfilename);
                                if(entryfilename.contains("/")) {
                                    entryfilename = entryfilename.substring(entryfilename.lastIndexOf("/")+1);
                                }
                                if (entryfilename.indexOf(".") != -1) {
                                    name = entryfilename.substring(0, entryfilename.lastIndexOf('.'));
                                } else {
                                    name = entryfilename;
                                }
                                mm.setName(name);

                                MimeType mt = MimeTypes.getMimeType(entryfilename);

                                if (mt.shouldConvertImage()) {
                                    // Konverterer bildet til et format som nettlesere kan vise
                                    mm.setData(MultimediaHelper.convertImageFormat(data));
                                    entryfilename = entryfilename.substring(0, entryfilename.lastIndexOf(".") + 1) + Aksess.getOutputImageFormat();
                                    mt = MimeTypes.getMimeType(entryfilename);
                                }

                                if (mt.getType().indexOf("image") != -1 || mt.getType().indexOf("flash") != -1) {
                                    // Dette er et bilde eller Flash fil, finn størrelse
                                    ImageInfo ii = new ImageInfo();
                                    ii.setInput(new ByteArrayInputStream(mm.getData()));
                                    if (ii.check()) {
                                        mm.setWidth(ii.getWidth());
                                        mm.setHeight(ii.getHeight());
                                    }
                                }

                                if (entryfilename.length() > 255) {
                                    entryfilename = entryfilename.substring(entryfilename.length() - 255, entryfilename.length());
                                }
                                mm.setFilename(entryfilename);

                                boolean preserveImageSize = param.getBoolean("preserveImageSize", false);
                                if (!preserveImageSize) {
                                    mm = resizeMultimedia(mm);
                                }
                                mediaService.setMultimedia(mm);
                            }

                        }
                        zipFile.close();
                    } finally {
                        temp.delete();
                    }
                }
                response.sendRedirect("multimedia.jsp?activetab=viewfolder&id=" + parentId + "&updatetree=true");
            }

        } catch (Exception e) {
            ExceptionHandler handler = new ExceptionHandler();
            handler.setThrowable(e, SOURCE);
            request.getSession(true).setAttribute("handler", handler);
            request.getRequestDispatcher(Aksess.ERROR_URL).forward(request, response);
        }

        return null;
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
                            throw new SystemException(SOURCE, "IOException", e);
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

