package no.kantega.openaksess.search.retrieve;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.openaksess.search.provider.transformer.ContentTransformer;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.search.api.retrieve.DocumentRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContentDocumentRetriever implements DocumentRetriever<Content> {

    @Autowired
    private ContentManagementService contentManagementService;

    public String getSupportedContentType() {
        return ContentTransformer.HANDLED_DOCUMENT_TYPE;
    }

    public Content getObjectById(int id) {
        ContentIdentifier cid = new ContentIdentifier();
        cid.setContentId(id);
        try {
            return contentManagementService.getContent(cid);
        } catch (NotAuthorizedException e) {
            return null;
        }
    }
}
