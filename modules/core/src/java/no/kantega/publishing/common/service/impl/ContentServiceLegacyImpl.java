package no.kantega.publishing.common.service.impl;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.services.ContentManagmentService;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.security.SecuritySession;

public class ContentServiceLegacyImpl implements ContentManagmentService {

    private final SecuritySession securitySession;

    public ContentServiceLegacyImpl(SecuritySession securitySession) {
        this.securitySession = securitySession;
    }

    @Override
    public Content getContentDoNotLog(ContentIdentifier id) throws NotAuthorizedException {
        return null;
    }

    @Override
    public Content getContent(ContentIdentifier id) throws NotAuthorizedException {
        return null;
    }
}
