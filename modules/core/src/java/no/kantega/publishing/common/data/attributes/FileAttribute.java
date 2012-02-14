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

package no.kantega.publishing.common.data.attributes;

import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.FormatHelper;
import no.kantega.publishing.admin.content.behaviours.attributes.PersistAttributeBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.PersistFileAttributeBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.UpdateAttributeFromRequestBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.UpdateFileAttributeFromRequestBehaviour;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.AttachmentAO;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.enums.AttributeProperty;

import java.util.HashMap;
import java.util.Map;

public class FileAttribute extends Attribute {

    private boolean deleteAttachment = false;
    private boolean keepOldVersions = true;

    public String getProperty(String property) {
        if (value == null || value.length() == 0) {
            return "";
        }
        if (AttributeProperty.HTML.equalsIgnoreCase(property) || AttributeProperty.URL.equalsIgnoreCase(property)) {
            return Aksess.getContextPath() + "/" + Aksess.ATTACHMENT_REQUEST_HANDLER +"?id=" + value;
        } else if (AttributeProperty.MIMETYPE.equalsIgnoreCase(property)
                || AttributeProperty.NAME.equalsIgnoreCase(property)
                || AttributeProperty.SIZE.equalsIgnoreCase(property)) {
            try {
                Attachment attachment = AttachmentAO.getAttachment(Integer.parseInt(value));

                if (attachment == null) {
                    return "";
                }

                if (AttributeProperty.MIMETYPE.equalsIgnoreCase(property)) {
                    return attachment.getMimeType().getType();
                } else if (AttributeProperty.NAME.equalsIgnoreCase(property)) {
                    return attachment.getFilename();
                } else if (AttributeProperty.SIZE.equalsIgnoreCase(property)) {
                    int size = attachment.getSize();
                    if (size > 0) {
                        return FormatHelper.formatSize(size);
                    } else {
                        return "";
                    }
                }
            } catch (SystemException e) {
                return "";
            }
        }
        return getValue();
    }

    public void setDeleteAttachment(boolean setDelete) {
        deleteAttachment = setDelete;
    }

    public boolean getDeleteAttachment() {
        return deleteAttachment;
    }

    public boolean isKeepOldVersions() {
        return keepOldVersions;
    }

    public void setKeepOldVersions(boolean keepOldVersions) {
        this.keepOldVersions = keepOldVersions;
    }

    public String getRenderer() {
        return "file";
    }

    public  void validate(ValidationErrors errors) throws no.kantega.commons.exception.RegExpSyntaxException {
        if (mandatory && (value == null || value.length() == 0)) {
            Map<String, Object> objects = new HashMap<String, Object>();
            objects.put("field", title);
            errors.add(name, "aksess.feil.mandatoryfield.file", objects);
        }
    }

    public PersistAttributeBehaviour getSaveBehaviour() {
        return new PersistFileAttributeBehaviour();
    }

    public UpdateAttributeFromRequestBehaviour getUpdateFromRequestBehaviour() {
        return new UpdateFileAttributeFromRequestBehaviour();
    }
}
