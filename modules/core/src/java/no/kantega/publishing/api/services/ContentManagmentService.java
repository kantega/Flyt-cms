package no.kantega.publishing.api.services;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.exception.ObjectLockedException;

import java.util.List;

public interface ContentManagmentService {

    public Content getContentDoNotLog(ContentIdentifier id) throws NotAuthorizedException, ContentNotFoundException;

    public Content getContent(ContentIdentifier id) throws NotAuthorizedException, ContentNotFoundException;

    /**
     * Check out an content object. The difference from getContent is that checking out
     * indicates that we want to change the object, so a Lock is created.
     * @param id - ContentIdentifier for the Content object
     * @return Content object associated with the ContentIdentifier or null if the content does not exist.
     * @throws no.kantega.commons.exception.SystemException - System error
     * @throws NotAuthorizedException - if the user is not authorized to update the content.
     * @throws no.kantega.commons.exception.InvalidFileException - The template
     * @throws no.kantega.publishing.common.exception.InvalidTemplateException - invalid template
     * @throws no.kantega.publishing.common.exception.ObjectLockedException - if the Content object is already checked out.
     */
    public Content checkOutContent(ContentIdentifier id) throws NotAuthorizedException, ObjectLockedException, ContentNotFoundException;

    public Content getLastVersionOfContent(ContentIdentifier id) throws ContentNotFoundException, NotAuthorizedException;

    public List<Content> getAllContentVersions(ContentIdentifier id) throws ContentNotFoundException, NotAuthorizedException;

    public Content checkInContent(Content content, ContentStatus newStatus) throws NotAuthorizedException;
}
