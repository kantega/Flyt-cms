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

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.LocaleLabels;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.MultimediaAO;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.MediaAttribute;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.exception.InvalidImageFormatException;
import no.kantega.publishing.multimedia.MultimediaUploadHandler;
import no.kantega.publishing.spring.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 *
 */
public class PersistMediaAttributeBehaviour implements PersistAttributeBehaviour {
    private static final Logger log = LoggerFactory.getLogger(PersistMediaAttributeBehaviour.class);

    private static MultimediaAO multimediaAO;

    public void persistAttribute(Connection c, Content content, Attribute attribute) throws SQLException, SystemException {
        if (multimediaAO == null) {
            multimediaAO = RootContext.getInstance().getBean(MultimediaAO.class);
        }

        if (attribute instanceof MediaAttribute) {
            MediaAttribute mediaAttr = (MediaAttribute) attribute;
            MultipartFile importFile = mediaAttr.getImportFile();
            try {
                if (importFile != null) {
                    Multimedia multimedia = null;

                    try {
                        if (content.getStatus() == ContentStatus.PUBLISHED) {
                            // When a file is uploaded and published directly, the old image if overwritten
                            // This cannot be done for drafts or pages awaiting to be published
                            int oldId = Integer.parseInt(mediaAttr.getValue());
                            multimedia = multimediaAO.getMultimedia(oldId);
                        }
                    } catch (NumberFormatException e) {

                    }

                    String filename = importFile.getOriginalFilename();

                    if (multimedia == null) {
                        multimedia = new Multimedia();

                        int mediaFolderId = -1;
                        String mediaFolder = mediaAttr.getDefaultMediaFolder();
                        if (mediaFolder != null) {
                            try {
                                mediaFolderId = Integer.parseInt(mediaFolder);
                                if (multimediaAO.getMultimedia(mediaFolderId) == null) {
                                    mediaFolderId = -1;
                                }
                            } catch (Exception e) {
                                // Name of folder was specified
                            }
                        }

                        if (mediaFolderId == -1) {
                            mediaFolderId = createMediaFolder(mediaFolderId, mediaFolder);
                        }

                        multimedia.setParentId(mediaFolderId);
                    }

                    MultimediaUploadHandler multimediaUploadHandler = (MultimediaUploadHandler) RootContext.getInstance().getBean("aksessMultimediaUploadHandler");

                    multimediaUploadHandler.updateMultimediaWithData(multimedia, importFile.getBytes(), filename, true);

                    int id = multimediaAO.setMultimedia(multimedia);
                    mediaAttr.setValue("" + id);
                    mediaAttr.setImportFile(null);
                }
            } catch (IllegalStateException e) {
                log.info("Uploaded file was discarded, has been deleted");
            } catch (IOException | InvalidImageFormatException e) {
                throw new SystemException("Feil ved filvedlegg", e);
            }
        }

        PersistSimpleAttributeBehaviour saveSimple = new PersistSimpleAttributeBehaviour();
        saveSimple.persistAttribute(c, content, attribute);
    }

    private int createMediaFolder(int mediaFolderId, String mediaFolder) {
        String defaultFolderName = LocaleLabels.getLabel("aksess.multimedia.uploadfolder", Aksess.getDefaultAdminLocale());
        if (mediaFolder == null || mediaFolder.length() == 0) {
            mediaFolder = defaultFolderName;
        }
        // Find folder with this name
        List<Multimedia> folders = multimediaAO.getMultimediaList(0);
        for (Multimedia m : folders) {
            if (m.getType() == MultimediaType.FOLDER && m.getName().equalsIgnoreCase(mediaFolder)) {
                mediaFolderId = m.getId();
            }
        }

        if (mediaFolderId == -1) {
            // Folder does not exists create one
            Multimedia folder = new Multimedia();
            folder.setName(mediaFolder);
            folder.setType(MultimediaType.FOLDER);
            mediaFolderId = multimediaAO.setMultimedia(folder);
        }
        return mediaFolderId;
    }
}
