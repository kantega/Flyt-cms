package no.kantega.openaksess.search.index.update;

import no.kantega.openaksess.search.provider.transformer.AttachmentTransformer;
import no.kantega.openaksess.search.provider.transformer.ContentTransformer;
import no.kantega.publishing.common.ao.AttachmentAO;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.event.ContentEvent;
import no.kantega.publishing.event.ContentEventListenerAdapter;
import no.kantega.search.api.index.DocumentIndexer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IndexUpdater extends ContentEventListenerAdapter {
    @Autowired
    private DocumentIndexer documentIndexer;

    @Autowired
    private ContentTransformer contentTransformer;

    @Autowired
    private AttachmentTransformer attachmentTransformer;

    @Override
    public void contentStatusChanged(ContentEvent event) {
        Content content = event.getContent();
        updateIndex(content);
    }

    @Override
    public void contentSaved(ContentEvent event) {
        Content content = event.getContent();
        if (content.isSearchable()) {
            updateIndex(content);
        } else {
            contentDeleted(event);
        }
    }

    @Override
    public void contentDeleted(ContentEvent event) {
        Content content = event.getContent();
        List<String> uids = new ArrayList<>();
        uids.add(contentTransformer.generateUniqueID(content));

        for (Attachment attachment : AttachmentAO.getAttachmentList(event.getContent().getContentIdentifier())) {
            uids.add(attachmentTransformer.generateUniqueID(attachment));
        }
        documentIndexer.deleteById(uids);
    }

    @Override
    public void contentExpired(ContentEvent event) {
        contentDeleted(event);
    }

    @Override
    public void contentActivated(ContentEvent event) {
        updateIndex(event.getContent());
    }

    public void attachmentUpdated(ContentEvent event) {
        Attachment attachment = event.getAttachment();
        if (attachment.getContentId() != -1) {
            documentIndexer.indexDocumentAndCommit(attachmentTransformer.transform(attachment));
        }
    }

    private void updateIndex(Content content) {
        if (content.isSearchable()) {
            documentIndexer.indexDocumentAndCommit(contentTransformer.transform(content));
        }
    }
}
