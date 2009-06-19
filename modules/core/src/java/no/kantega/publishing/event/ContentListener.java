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

package no.kantega.publishing.event;

import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.Attachment;

import java.util.Map;

/**
 *
 */
public interface ContentListener {

    public void beforeSelectTemplate(Map model);
    public void beforeAssociationUpdate(Association association);
    public void beforeConfirmCopyPasteContent(Content content, Map model);
    public void associationUpdated(Association association);
    public void contentCreated(Content content);
    public void beforeContentSave(Content content);
    public void beforeContentDelete(Content content, Boolean canDelete);
    public void contentSaved(Content content);
    public void contentExpired(Content content);
    public void contentActivated(Content content);
    public void contentDeleted(Content content);
    public void attachmentUpdated(Attachment attachment);
}
