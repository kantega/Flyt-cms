package no.kantega.publishing.common.service.impl;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.api.services.ContentManagementService;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.exception.ObjectLockedException;
import no.kantega.publishing.security.SecuritySession;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ContentServiceLegacyImpl implements ContentManagementService {

    private final SecuritySession securitySession;

    @Autowired
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

    @Override
    public Content checkOutContent(ContentIdentifier id) throws NotAuthorizedException, ObjectLockedException, ContentNotFoundException {
        return null;
    }

    @Override
    public Content getLastVersionOfContent(ContentIdentifier id) throws ContentNotFoundException, NotAuthorizedException {
        return null;
    }

    @Override
    public List<Content> getAllContentVersions(ContentIdentifier id) throws ContentNotFoundException, NotAuthorizedException {
        return null;
    }

    @Override
    public Content checkInContent(Content content, ContentStatus newStatus) throws NotAuthorizedException {
        return null;
    }
}
