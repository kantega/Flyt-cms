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

package no.kantega.publishing.admin.content.behaviours.attributes;

import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.MediaAttribute;
import no.kantega.publishing.common.ao.MultimediaAO;
import no.kantega.publishing.common.Aksess;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.media.ImageInfo;
import no.kantega.commons.media.MimeType;
import no.kantega.commons.media.MimeTypes;
import no.kantega.commons.log.Log;
import no.kantega.commons.util.LocaleLabels;

import java.sql.Connection;
import java.sql.SQLException;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.ByteArrayInputStream;
import java.util.List;

import no.kantega.publishing.multimedia.ImageEditor;
import no.kantega.publishing.multimedia.metadata.MultimediaMetadataExtractor;
import no.kantega.publishing.spring.RootContext;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 */
public class PersistMediaAttributeBehaviour implements PersistAttributeBehaviour {
    public void persistAttribute(Connection c, Content content, Attribute attribute) throws SQLException, SystemException {
        if (attribute instanceof MediaAttribute) {
            MediaAttribute mediaAttr = (MediaAttribute)attribute;
            MultipartFile importFile = mediaAttr.getImportFile();
            try {
                if (importFile != null) {
                    Multimedia multimedia = null;

                    try {
                        if (content.getStatus() == ContentStatus.PUBLISHED) {
                            // When a file is uploaded and published directly, the old image if overwritten
                            // This cannot be done for drafts or pages awaiting to be published
                            int oldId = Integer.parseInt(mediaAttr.getValue());
                            multimedia = MultimediaAO.getMultimedia(oldId);
                        }
                    } catch (NumberFormatException e) {

                    }

                    String filename = importFile.getOriginalFilename();

                    if (multimedia == null) {
                        multimedia = new Multimedia();

                        if (filename.length() > 255) {
                            filename = filename.substring(filename.length() - 255, filename.length());
                        }
                        multimedia.setName(filename);

                        int mediaFolderId = -1;
                        String mediaFolder = mediaAttr.getDefaultMediaFolder();
                        if (mediaFolder != null) {
                            try {
                                mediaFolderId = Integer.parseInt(mediaFolder);
                                if (MultimediaAO.getMultimedia(mediaFolderId) == null) {
                                    mediaFolderId = -1;
                                }
                            } catch (Exception e) {
                                // Name of folder was specified
                            }
                        }

                        if (mediaFolderId == -1) {
                            String defaultFolderName = LocaleLabels.getLabel("aksess.multimedia.uploadfolder", Aksess.getDefaultAdminLocale());
                            if (mediaFolder == null || mediaFolder.length() == 0) {
                                mediaFolder = defaultFolderName;
                            }
                            // Find folder with this name
                            List<Multimedia> folders = MultimediaAO.getMultimediaList(0);
                            for (Multimedia m : folders) {
                                if (m.getType() == MultimediaType.FOLDER && m.getName().equalsIgnoreCase(mediaFolder)) {
                                    mediaFolderId = m.getId();
                                }
                            }

                            if (mediaFolderId == -1) {
                                // Folder does not exists create one
                                Multimedia folder = new Multimedia();
                                folder.setName(defaultFolderName);
                                folder.setType(MultimediaType.FOLDER);
                                mediaFolderId = MultimediaAO.setMultimedia(folder);
                            }
                        }

                        multimedia.setParentId(mediaFolderId);
                    }
                    byte[] data = importFile.getBytes();
                    multimedia.setData(data);

                    MimeType mimeType = MimeTypes.getMimeType(filename);

                    List<MultimediaMetadataExtractor> multimediaMetadataExtractors = (List<MultimediaMetadataExtractor>)RootContext.getInstance().getBean("aksessMultimediaMetadataExtractors");
                    for (MultimediaMetadataExtractor extractor : multimediaMetadataExtractors) {
                        if (extractor.supportsMimeType(multimedia.getMimeType().getType())) {
                            multimedia = extractor.extractMetadata(multimedia);
                        }
                    }

                    multimedia = resizeImage(multimedia, mimeType);

                    multimedia.setFilename(filename);

                    int id = MultimediaAO.setMultimedia(multimedia);
                    mediaAttr.setValue("" + id);
                    mediaAttr.setImportFile(null);
                }
            } catch (IllegalStateException e) {
                Log.info(this.getClass().getName(), "Uploaded file was discarded, has been deleted", null, null);
            } catch (FileNotFoundException e) {
                throw new SystemException("Feil ved filvedlegg", this.getClass().getName(), e);
            } catch (IOException e) {
                throw new SystemException("Feil ved filvedlegg", this.getClass().getName(), e);
            }
        }

        PersistSimpleAttributeBehaviour saveSimple = new PersistSimpleAttributeBehaviour();
        saveSimple.persistAttribute(c, content, attribute);
    }

    private Multimedia resizeImage(Multimedia multimedia, MimeType mimeType) {
        if (mimeType.getType().indexOf("image") != -1 || mimeType.getType().indexOf("flash") != -1) {
            // For images and Flash we can find the dimensions
            if (multimedia.getMimeType().getType().indexOf("image") != -1 && (Aksess.getMaxMediaWidth() > 0 || Aksess.getMaxMediaHeight() > 0)) {
                if (multimedia.getWidth() > Aksess.getMaxMediaWidth() ||  multimedia.getHeight() > Aksess.getMaxMediaHeight()) {
                    try {
                        ImageEditor editor = (ImageEditor) RootContext.getInstance().getBean("aksessImageEditor");
                        multimedia = editor.resizeMultimedia(multimedia, Aksess.getMaxMediaWidth(), Aksess.getMaxMediaHeight());
                    } catch (Exception e) {
                        Log.error(this.getClass().getName(), e, null, null);
                    }
                }
            }
        }
        return multimedia;
    }
}
