package no.kantega.openaksess.search.retrieve;

import no.kantega.openaksess.search.provider.transformer.AttachmentTransformer;
import no.kantega.publishing.common.ao.AttachmentAO;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.search.api.retrieve.DocumentRetriever;
import org.springframework.stereotype.Component;

@Component
public class AttachmentDocumentRetriever implements DocumentRetriever<Attachment>{
    public String getSupportedContentType() {
        return AttachmentTransformer.HANDLED_DOCUMENT_TYPE;
    }

    public Attachment getObjectById(int id) {
        return AttachmentAO.getAttachment(id);
    }
}
