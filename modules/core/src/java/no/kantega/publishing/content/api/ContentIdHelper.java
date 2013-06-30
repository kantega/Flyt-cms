package no.kantega.publishing.content.api;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.ContentNotFoundException;

import javax.servlet.http.HttpServletRequest;

public interface ContentIdHelper {
    ContentIdentifier findRelativeContentIdentifier(Content context, String expr) throws SystemException, ContentNotFoundException;

    ContentIdentifier fromRequest(HttpServletRequest request) throws ContentNotFoundException;

    ContentIdentifier fromRequestAndUrl(HttpServletRequest request, String url) throws ContentNotFoundException, SystemException;

    ContentIdentifier fromSiteIdAndUrl(int siteId, String url) throws SystemException, ContentNotFoundException;

    ContentIdentifier fromUrl(String url) throws ContentNotFoundException, SystemException;

    void assureContentIdAndAssociationIdSet(ContentIdentifier contentIdentifier);
}
