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

package no.kantega.publishing.jobs.contentstate;

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.ao.AttachmentAO;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.event.ContentEvent;
import no.kantega.publishing.event.ContentEventListenerAdapter;
import no.kantega.publishing.search.index.jobs.RemoveAttachmentJob;
import no.kantega.publishing.search.index.jobs.RemoveContentJob;
import no.kantega.publishing.search.index.jobs.UpdateAttachmentJob;
import no.kantega.publishing.search.index.jobs.UpdateContentJob;
import no.kantega.search.index.IndexManager;

import java.util.List;

/**
 *
 */
public class IndexUpdaterListener extends ContentEventListenerAdapter {
    private static final String SOURCE = "aksess.IndexUpdaterListener";

    private IndexManager indexManager;

    @Override
    public void contentSaved(ContentEvent event) {
        updateIndex(event.getContent());
    }

    @Override
    public void contentExpired(ContentEvent event) {
        updateIndex(event.getContent());
    }

    @Override
    public void contentActivated(ContentEvent event) {
        updateIndex(event.getContent());
    }

    @Override
    public void contentDeleted(ContentEvent event) {
        // Slett innhold
        indexManager.addIndexJob(new RemoveContentJob(Integer.toString(event.getContent().getId()), "aksessContent"));

        // Slett vedlegg
        List<Attachment> attachments = null;
        try {
            attachments = AttachmentAO.getAttachmentList(event.getContent().getContentIdentifier());
            for (Attachment attachment : attachments) {
                indexManager.addIndexJob(new RemoveAttachmentJob(Integer.toString(attachment.getId()), "aksessAttachments"));
            }
        } catch (SystemException e) {
            Log.error(SOURCE, e, null, null);
        }
    }

    public void attachmentUpdated(ContentEvent event) {
        if (event.getAttachment().getContentId() != -1) {
            indexManager.addIndexJob(new UpdateAttachmentJob(""+event.getAttachment().getId(), "aksessAttachments"));
        }
    }

    private void updateIndex(Content content) {
        indexManager.addIndexJob(new UpdateContentJob(Integer.toString(content.getId()), "aksessContent"));
        List<Attachment> attachments = null;
        try {
            attachments = AttachmentAO.getAttachmentList(content.getContentIdentifier());
            for (Attachment attachment : attachments) {
                indexManager.addIndexJob(new UpdateAttachmentJob(Integer.toString(attachment.getId()), "aksessAttachments"));
            }
        } catch (SystemException e) {
            Log.error(SOURCE, e, null, null);
        }
    }

    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }
}
