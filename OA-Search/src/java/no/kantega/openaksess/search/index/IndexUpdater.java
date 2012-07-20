package no.kantega.openaksess.search.index;

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.openaksess.search.provider.transformer.ContentTransformer;
import no.kantega.publishing.common.ao.AttachmentAO;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.event.ContentEvent;
import no.kantega.publishing.event.ContentEventListenerAdapter;
import no.kantega.search.api.index.DocumentIndexer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IndexUpdater extends ContentEventListenerAdapter {
    @Autowired
    private DocumentIndexer documentIndexer;

    @Autowired
    private ContentTransformer transformer;

    @Override
    public void contentStatusChanged(ContentEvent contentEvent) {
        super.contentStatusChanged(contentEvent);
    }

    @Override
    public void contentSaved(ContentEvent event) {
        updateIndex(event.getContent());
    }

    @Override
    public void contentActivated(ContentEvent event) {
        updateIndex(event.getContent());
    }

    @Override
    public void contentDeleted(ContentEvent event) {
        // Slett innhold
        // indexManager.addIndexJob(new RemoveContentJob(Integer.toString(event.getContent().getId()), "aksessContent"));

        // Slett vedlegg
        List<Attachment> attachments = null;
        try {
            attachments = AttachmentAO.getAttachmentList(event.getContent().getContentIdentifier());
            for (Attachment attachment : attachments) {
                //indexManager.addIndexJob(new RemoveAttachmentJob(Integer.toString(attachment.getId()), "aksessAttachments"));
            }
        } catch (SystemException e) {
            Log.error("", e, null, null);
        }
    }

    public void attachmentUpdated(ContentEvent event) {
        if (event.getAttachment().getContentId() != -1) {
            // indexManager.addIndexJob(new UpdateAttachmentJob(""+event.getAttachment().getId(), "aksessAttachments"));
        }
    }

    private void updateIndex(Content content) {
        /*indexManager.addIndexJob(new UpdateContentJob(Integer.toString(content.getId()), "aksessContent"));
        List<Attachment> attachments = null;
        try {
            attachments = AttachmentAO.getAttachmentList(content.getContentIdentifier());
            for (Attachment attachment : attachments) {
                indexManager.addIndexJob(new UpdateAttachmentJob(Integer.toString(attachment.getId()), "aksessAttachments"));
            }
        } catch (SystemException e) {
            Log.error(SOURCE, e, null, null);
        }*/
    }
}
