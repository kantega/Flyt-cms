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

import no.kantega.publishing.api.content.ContentIdentifier;

/**
 * @
 */
public interface ContentEventListener {

    /**
     * Called when user adds new page (content), before selecting a template.
     * @
     * @param event - Replace objects in event.getModel() to override default parameters for the select template view
     */
    public void beforeSelectTemplate(ContentEvent event);

    /**
     * Called before a page (content) is moved
     * @param event - event.getAssociation contains association which will be updated
     */
    public void beforeAssociationUpdate(ContentEvent event);
    /**
     * Called when an user tries to copy and paste page (content)
     * @param event - Replace objects in event.getModel() to override default parameters for the copy dialoguebox
     */
    public void beforeConfirmCopyPasteContent(ContentEvent event);

    /**
     * Called after a page (content) is moved
     * @param event - event.getAssociation contains association which was updated
     */
    public void associationUpdated(ContentEvent event);

    /**
     * Called after a association is copied - e.g content is crosspublished
     * @param event - event.getAssociation contains association which was crosspublished
     */
    void associationCopied(ContentEvent event);

    /**
     * Called after a association is added to a page - eg shortcut created
     * @param event - event.getAssociation contains association which was added
     */
    void associationAdded(ContentEvent event);

    /**
     * Called after a association is deleted
     * @param event - event.getAssociation contains association which was deleted
     */
    void associationDeleted(ContentEvent event);

    /**
     * Called when priority for a list of associations is updated
     * @param contentEvent - contains nothing
     */
    void setAssociationsPriority(ContentEvent contentEvent);


    /**
     * Called when content is created before it is edited
     * @param event - event.getContent contains newly created page (content)
     */
    public void contentCreated(ContentEvent event);

    /**
     * Called before content is saved in database
     * @param event - event.getContent contains page (content)
     */
    public void beforeContentSave(ContentEvent event);
    /**
     * Called before content is deleted
     * @param event - Use event.setCanDelete(false) to prevent user from deleting page (content)
     */
    public void beforeContentDelete(ContentEvent event);

    /**
     * Called after content is saved in database
     * @param event - event.getContent contains page (content)
     */
    public void contentSaved(ContentEvent event);

    /**
     * Called after new content is saved the first time in database. Called regardless of the status of the page (draft or published)
     * @param event - event.getContent contains page (content)
     */
    public void newContentSaved(ContentEvent event);
    /**
     *
     * @param event - event.getContent contains page (content)
     */
    public void newContentPublished(ContentEvent event);

    /**
     * Called when contents expire date is reached
     * @param event - event.getContent contains page (content)
     */
    public void contentExpired(ContentEvent event);

    /**
     * Called when content is activited, eg. when publish date is reached
     * @param event - event.getContent contains page (content)
     */
    public void contentActivated(ContentEvent event);

    /**
     * Called when content is deleted.
     * @param event - event.getContent contains page (content)
     */
    public void contentDeleted(ContentEvent event);

    /**
     * Called when attachment is updated
     * @param event - event.getAttachment contains attachment
     */
    public void attachmentUpdated(ContentEvent event);

    /**
     * Called when a content status has changed
     * @param event - event.getContent contains page (content)
     */
    public void contentStatusChanged(ContentEvent event);

    /**
     * Called when attachment is deleted
     * @param event - event.getAttachment contains attachment
     */
    public void attachmentDeleted(ContentEvent event);

    /**
     * Called when a content is permanently deleted
     * @param contentIdentifier
     */
    public void contentPermanentlyDeleted(ContentIdentifier contentIdentifier);

}
