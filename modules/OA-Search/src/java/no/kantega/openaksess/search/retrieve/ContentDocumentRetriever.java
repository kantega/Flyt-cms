package no.kantega.openaksess.search.retrieve;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.openaksess.search.provider.transformer.ContentTransformer;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.search.api.retrieve.DocumentRetriever;
import org.springframework.stereotype.Component;

@Component
public class ContentDocumentRetriever implements DocumentRetriever<Content> {

    private ContentManagementService contentManagementService;

    public String getSupportedContentType() {
        return ContentTransformer.HANDLED_DOCUMENT_TYPE;
    }

    public Content getObjectById(int id) {
        if(contentManagementService == null){
             contentManagementService = new ContentManagementService(SecuritySession.createNewAdminInstance());
        }

        ContentIdentifier cid =  ContentIdentifier.fromContentId(id);
        try {
            return contentManagementService.getContent(cid);
        } catch (NotAuthorizedException e) {
            return null;
        }
    }
}
