package no.kantega.publishing.admin.content.util;

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

import no.kantega.publishing.api.attachment.ao.AttachmentAO;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.AttachmentAOImpl;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.FileAttribute;
import no.kantega.publishing.spring.RootContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class AttachmentHelper {
    public static void saveFileAsContentAttachment(Content content, FileAttribute fileAttribute, MultipartFile importFile) throws IOException {
        AttachmentAO attachmentAO = RootContext.getInstance().getBean(AttachmentAOImpl.class);
        int oldId = -1;
        try {
            oldId = Integer.parseInt(fileAttribute.getValue());
        } catch (NumberFormatException e) {

        }

        // Brukeren har lastet opp en fil, legg inn basen
        Attachment attachment = new Attachment();
        attachment.setContentId(content.getId());
        attachment.setLanguage(content.getLanguage());
        if (!fileAttribute.isKeepOldVersions() && oldId != -1) {
            // Delete old version
            attachment.setId(oldId);
        } else {
            setOldVersionNotSearchable(attachmentAO, oldId);
        }

        byte[] data = importFile.getBytes();

        String filename = getFilename(importFile);

        attachment.setFilename(filename);
        attachment.setData(data);
        attachment.setSize(data.length);

        fileAttribute.setValue(String.valueOf(attachmentAO.setAttachment(attachment)));

        attachment.setData(null);

        ensureContentIdIsUpdatedWhenContentIsPublished(content, attachment);
    }

    private static void setOldVersionNotSearchable(AttachmentAO attachmentAO, int oldId) {
        if(Aksess.getConfiguration().getBoolean("attachments.setOldVersionNotSearchable", true)) {
            Attachment attachment = attachmentAO.getAttachment(oldId);
            attachment.setSearchable(false);
            attachmentAO.setAttachment(attachment);
        }
    }

    private static void ensureContentIdIsUpdatedWhenContentIsPublished(Content content, Attachment attachment) {
        content.addAttachment(attachment);
    }

    private static String getFilename(MultipartFile importFile) {
        String filename = importFile.getOriginalFilename();
        if (filename.length() > 255) {
            filename = filename.substring(filename.length() - 255, filename.length());
        }
        return filename;
    }
}
