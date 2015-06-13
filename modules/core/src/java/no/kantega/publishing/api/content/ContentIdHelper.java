package no.kantega.publishing.api.content;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.ContentNotFoundException;

import javax.servlet.http.HttpServletRequest;

public interface ContentIdHelper {

    /**
     * Find the ContentIdentifer for a Content, relative to the context-Content and expr.
     * @param context - The current Content-contect
     * @param expr    - Path, may be in various formats.
     *                  "../", "../../" or "/" finds the parent, grand parent, and root, respectively.
     *                  «group» get the identifier for the group of the current content.
     *                  «///» get the identifier three levels from root.
     *                 «next», «previous» get the siblings of the current Content.
     * @return        - ContentIdentifier
     * @throws SystemException
     * @throws ContentNotFoundException
     */
    ContentIdentifier findRelativeContentIdentifier(Content context, String expr) throws SystemException, ContentNotFoundException;

    /**
     * @param request - The current request
     * @return ContentIdentifier for the given request.
     * @throws ContentNotFoundException
     */
    ContentIdentifier fromRequest(HttpServletRequest request) throws ContentNotFoundException;

    /**
     * @param request - The current request
     * @param url - The url of ContentIdentifier is desired for.
     * @return ContentIdentifier for url.
     * @throws ContentNotFoundException
     * @throws SystemException
     */
    ContentIdentifier fromRequestAndUrl(HttpServletRequest request, String url) throws ContentNotFoundException, SystemException;

    /**
     *
     * @param siteId - id of the site we want ContentIdentifier for.
     * @param url we want ContentIdentifier for.
     * @return ContentIdentifier for url on site with siteId
     * @throws SystemException
     * @throws ContentNotFoundException
     */
    ContentIdentifier fromSiteIdAndUrl(int siteId, String url) throws SystemException, ContentNotFoundException;

    /**
     * @param url - e.g. "/"
     * @return ContentIdentifier for url.
     * @throws ContentNotFoundException
     * @throws SystemException
     */
    ContentIdentifier fromUrl(String url) throws ContentNotFoundException, SystemException;

    /**
     * Make sure the given ContentIdentifier has both contentId and associationId set.
     * @param contentIdentifier assure both are set on.
     */
    void assureContentIdAndAssociationIdSet(ContentIdentifier contentIdentifier);

    /**
     * Make sure the given ContentIdentifier has associationId set.
     * @param contentIdentifier assure associationid is set on.
     */
    void assureAssociationIdSet(ContentIdentifier contentIdentifier);
}
