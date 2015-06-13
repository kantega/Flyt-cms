package no.kantega.openaksess.search.index.update;

import no.kantega.openaksess.search.provider.transformer.AttachmentTransformer;
import no.kantega.openaksess.search.provider.transformer.ContentTransformer;
import no.kantega.publishing.api.attachment.ao.AttachmentAO;
import no.kantega.publishing.api.content.ContentAO;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.ao.AssociationAO;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.AssociationType;
import no.kantega.publishing.event.ContentEvent;
import no.kantega.publishing.event.ContentEventListenerAdapter;
import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.index.DocumentIndexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

@Component
public class IndexUpdater extends ContentEventListenerAdapter {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ContentAO contentAO;

    @Autowired
    private AttachmentAO attachmentAO;

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
        try {
            Content content = event.getContent();
            if (content.isSearchable()) {
                updateIndex(content);
            } else {
                contentDeleted(event);
            }
        } catch (Throwable e) {
            log.error("Error indexing saved content", e);
        }
    }

    @Override
    public void contentDeleted(ContentEvent event) {

        try {
            Content content = event.getContent();
            List<String> uids = new ArrayList<>();
            List<Association> associations = AssociationAO.getAssociationsByContentId(event.getContent().getId());
            if (associations != null) {
                for (Association association : associations) {
                    uids.add(contentTransformer.generateUniqueID(content, association.getSiteId()));
                }
            }

            for (Attachment attachment : attachmentAO.getAttachmentList(event.getContent().getContentIdentifier())) {
                uids.add(attachmentTransformer.generateUniqueID(attachment));
            }

            documentIndexer.deleteByUid(uids);
            documentIndexer.commit();
        } catch (Throwable e) {
            log.error("Error deleting content from index", e);
        }
    }

    @Override
    public void contentExpired(ContentEvent event) {
        contentDeleted(event);
    }

    @Override
    public void contentActivated(ContentEvent event) {
        updateIndex(event.getContent());
    }

    @Override
    public void associationUpdated(ContentEvent event) {
        // TODO: Should reindex children
        updateIndex(event.getAssociation());
    }

    @Override
    public void associationCopied(ContentEvent event) {
        // TODO: Should reindex children
        updateIndex(event.getAssociation());
    }

    @Override
    public void associationDeleted(ContentEvent event) {
        // TODO: Should remove children
        try {
            List<String> uids = new ArrayList<>();

            int siteId = event.getAssociation().getSiteId();

            ContentIdentifier cid = ContentIdentifier.fromAssociationId(event.getAssociation().getAssociationId());
            Content content = contentAO.getContent(cid, true);
            if (content != null) {
                boolean lastInstanceOnSite = true;
                for (Association association : content.getAssociations()) {
                    if (association.getSiteId() == siteId) {
                        // Don't remove from index if there still instances of this page is this site
                        lastInstanceOnSite = false;
                    }
                }
                if (lastInstanceOnSite) {
                    uids.add(contentTransformer.generateUniqueID(content, siteId));
                }
            } else {
                // Content was deleted (all associations deleted), handled by contentDeleted method
            }

            documentIndexer.deleteByUid(uids);
            documentIndexer.commit();
        } catch (Throwable e) {
            log.error("Error indexing deleted association", e);
        }
    }

    @Override
    public void associationAdded(ContentEvent event) {
        updateIndex(event.getAssociation());
    }

    public void attachmentUpdated(ContentEvent event) {
        try {
            Attachment attachment = event.getAttachment();
            if (attachment.getContentId() != -1) {
                IndexableDocument indexableDocument = attachmentTransformer.transform(attachment);
                if (indexableDocument.shouldIndex()) {
                    documentIndexer.indexDocumentAndCommit(indexableDocument);
                }
            }
        } catch (Throwable e) {
            log.error("Error indexing updated attachment", e);
        }
    }

    @Override
    public void attachmentDeleted(ContentEvent event) {
        try {
            documentIndexer.deleteByUid(asList(attachmentTransformer.generateUniqueID(event.getAttachment())));
            documentIndexer.commit();
        } catch (Throwable e) {
            log.error("Error removing attachment from index", e);
        }
    }

    private void updateIndex(Content content) {
        if (content.isSearchable()) {
            List<Association> associations = content.getAssociations();
            if (associations != null) {
                for (Association association : associations) {
                    if (association.getAssociationtype() != AssociationType.SHORTCUT) {
                        updateIndex(association);
                    }
                }
            }
        }
    }

    private void updateIndex(Association association) {
        ContentIdentifier cid = ContentIdentifier.fromAssociationId(association.getAssociationId());
        Content content = contentAO.getContent(cid, true);
        if (content != null) {
            IndexableDocument indexableDocument = contentTransformer.transform(content);
            if (indexableDocument.shouldIndex()) {
                documentIndexer.indexDocumentAndCommit(indexableDocument);
            }
        }
    }
}
