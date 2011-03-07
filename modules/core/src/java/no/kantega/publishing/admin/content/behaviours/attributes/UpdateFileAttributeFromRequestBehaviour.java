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

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.admin.content.util.AttributeHelper;
import no.kantega.publishing.common.ao.AttachmentAO;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.FileAttribute;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class UpdateFileAttributeFromRequestBehaviour implements UpdateAttributeFromRequestBehaviour {
    public void updateAttribute(RequestParameters param, Content content, Attribute attribute) {
        try {
            String inputField = AttributeHelper.getInputFieldName(attribute.getName());

            FileAttribute fileAttribute = (FileAttribute)attribute;
            MultipartFile importFile = param.getFile(inputField);
            if (importFile != null) {
                saveFileAsAttachment(content, fileAttribute, importFile);
            } else {
                int delete = param.getInt("delete_" + inputField);
                if (delete == 1) {
                    fileAttribute.setDeleteAttachment(true);
                }
            }
        } catch (IOException e) {
            throw new SystemException("Feil ved filvedlegg", this.getClass().getName(), e);
        }
    }

    private void saveFileAsAttachment(Content content, FileAttribute fileAttribute, MultipartFile importFile) throws IOException {


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
        }

        byte[] data = importFile.getBytes();

        String filename = getFilename(importFile);

        attachment.setFilename(filename);
        attachment.setData(data);
        attachment.setSize(data.length);

        fileAttribute.setValue("" + AttachmentAO.setAttachment(attachment));

        attachment.setData(null);

        ensureContentIdIsUpdatedWhenContentIsPublished(content, attachment);
    }

    private void ensureContentIdIsUpdatedWhenContentIsPublished(Content content, Attachment attachment) {
        content.addAttachment(attachment);
    }

    private String getFilename(MultipartFile importFile) {
        String filename = importFile.getOriginalFilename();
        if (filename.length() > 255) {
            filename = filename.substring(filename.length() - 255, filename.length());
        }
        return filename;
    }
}
