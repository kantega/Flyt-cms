package no.kantega.publishing.api.content;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.data.TemplateConfiguration;
import no.kantega.publishing.common.data.UserContentChanges;
import no.kantega.publishing.common.data.WorkList;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.org.OrgUnit;
import no.kantega.publishing.security.data.User;

import java.util.Date;
import java.util.List;

public interface ContentAO {

    /**
     * Delete the content identified by cid
     * @param cid - Identifying the content to be deleted
     * @return the ContentIdentifier for the parent of gived cid
     */
    ContentIdentifier deleteContent(ContentIdentifier cid);

    /**
     * Executes the given contenthandler for each Content that exists.
     * @param contentHandler to execute for each Content object
     * @param stopper that can be used to signal stopping execution.
     */
    void forAllContentObjects(ContentHandler contentHandler, ContentHandlerStopper stopper);

    /**
     *
     * @param cid identifying the content to be deleted
     * @param deleteActiveVersion - if the version identified by cid is the active version deleteActiveVersion have to be
     *                            the in order to delete the Content version.
     *                            If cid identifies the active version, and deleteActiveVersion is false, nothing is done.
     * @throws SystemException
     */
    void deleteContentVersion(ContentIdentifier cid, boolean deleteActiveVersion) throws SystemException;

    /**
     * @param cid identifying the Content to get.
     * @return All versions of the Content identified by cid.
     * @throws SystemException
     */
    List<Content> getAllContentVersions(ContentIdentifier cid) throws SystemException;

    /**
     * @param cid identifying the content.
     * @return Check out content and mark as checked out.
     * @throws SystemException
     */
    Content checkOutContent(ContentIdentifier cid) throws SystemException;

    /**
     * @param cid identifying the content.
     * @param isAdminMode if true the latest version may be returned, otherwise the active version is returned.
     * @return Content satisfying cid and isAdminMode, or null.
     * @throws SystemException
     */
    Content getContent(ContentIdentifier cid, boolean isAdminMode) throws SystemException;

    /**
     * @param orgUnit owning content
     * @return Content representing the orgunit.
     * @throws SystemException
     */
    Content getContent(OrgUnit orgUnit) throws SystemException;

    /**
     * @param associationId identifying Content
     * @return title of the page with gived associationId.
     * @throws ContentNotFoundException if content with associationId was not found.
     */
    String getTitleByAssociationId(int associationId) throws ContentNotFoundException;

    /**
     * @param contentQuery specifying the content wanted.
     * @param getAttributes if true, the Content objects are populated with their attributes.
     * @return Content matched by contentQuery.
     * @throws SystemException
     */
    List<Content> getContentList(ContentQuery contentQuery, boolean getAttributes) throws SystemException;

    /**
     * @param contentQuery specifying the content wanted.
     * @param getAttributes if true, the Content objects are populated with their attributes.
     * @param getTopics if true, the Content objects are populated with the topics associated to them.
     * @return Content matched by contentQuery.
     * @throws SystemException
     */
    List<Content> getContentList(ContentQuery contentQuery, boolean getAttributes, boolean getTopics) throws SystemException;

    /**
     * @return Content with status ContentStatus.WAITING_FOR_APPROVAL
     */
    List<Content> getContentListForApproval();

    /**
     * @param user associated with content
     * @return Worklist for draft, waiting, rejected, lastpublished, and remind.
     */
    List<WorkList<Content>> getMyContentList(User user);

    /**
     * Execute handler for each Content mateched by contentQuery, maxElements and sort.
     * @param contentQuery for finding Content.
     * @param handler to execute for each content.
     * @throws SystemException
     */
    void doForEachInContentList(ContentQuery contentQuery, ContentHandler handler) throws SystemException;

    /**
     * @param cid to find parent for.
     * @return ContentIdentifier representing the parent of given cid.
     * @throws SystemException
     */
    ContentIdentifier getParent(ContentIdentifier cid) throws SystemException;

    /**
     * Save Content.
     * @param content to check in
     * @param newStatus to set on Content
     * @return the updated Content.
     * @throws SystemException
     */
    Content checkInContent(Content content, ContentStatus newStatus) throws SystemException;

    /**
     * Set ContentStatus for Content represented by cid.
     * @param cid identifying the Content
     * @param newStatus - the new status
     * @param newPublishDate - the time when the content should be published. May be null.
     * @param userId to set as approvedBy if new status is published.
     * @return the updated Content.
     * @throws SystemException
     */
    Content setContentStatus(ContentIdentifier cid, ContentStatus newStatus, Date newPublishDate, String userId) throws SystemException;

    /**
     * @param after current contentId
     * @return the contentId for the next content with status Expired and has contentId larger than after.
     * @throws SystemException
     */
    int getNextExpiredContentId(int after) throws SystemException;

    /**
     * @param after current contentId
     * @return the contentId for the next content with status Published_waiting and has contentId larger than after.
     * @throws SystemException
     */

    int getNextWaitingContentId(int after) throws SystemException;

    /**
     * @param after - which content id to start at
     * @return the id of the next content id which should be activated
     * - because publish date was reached (on a new page or existing page)
     * - because changefrom date was reached (on a existing page)
     * @throws SystemException
     */
    int getNextActivationContentId(int after) throws SystemException;

    /**
     * Set ContentVisibilityStatus for Content
     * @param contentId for the Content.
     * @param newStatus to set. One of the constants in ContentVisibilityStatus: WAITING, ACTIVE, ARCHIVED, EXPIRED.
     * @throws SystemException
     */
    void setContentVisibilityStatus(int contentId, ContentVisibilityStatus newStatus) throws SystemException;

    /**
     * @param contentId identifying content
     * @param count - the number of notes content has.
     * @throws SystemException
     */
    void setNumberOfNotes(int contentId, int count) throws SystemException;

    /**
     * @param months - number of months in the past to count.
     * @return the changes made by users in the last n months.
     * @throws SystemException
     */
    List<UserContentChanges> getNoChangesPerUser(int months) throws SystemException;

    /**
     * @return the number of Content with ContentVisibilityStatus.ACTIVE and ContentType.PAGE.
     * @throws SystemException
     */
    int getContentCount() throws SystemException;

    /**
     * @return the number of Content with ContentVisibilityStatus.ACTIVE and ContentType.LINK.
     * @throws SystemException
     */
    int getLinkCount() throws SystemException;

    /**
     * @return the number of distinct users that have modified content.
     * @throws SystemException
     */
    int getContentProducerCount() throws SystemException;

    /**
     * Update Content based on the given TemplateConfiguration.
     * Goes through the defined DisplayTemplates,MetaDataTemplates,ContentTypes and:
     *   - set the defined ContentTemplate on all Content that does not have the defined ContentTemplate.
     *   - set the defined MetaDataTemplate on all Content that does not have the defined MetaDataTemplate.
     *   - set the defined ContentType on all Content that does not have the defined ContentType.
     *
     * @param templateConfiguration to update from.
     */
    void updateContentFromTemplates(TemplateConfiguration templateConfiguration);

    /**
     * @param contentId - ContentId
     * @return whether Content with contentId is published.
     */
    boolean hasBeenPublished(int contentId);

    /**
     * Updates publish date and expire date on a content object and all child objects
     * @param cid - ContentIdentifier to content object
     * @param publishDate - new publish date
     * @param expireDate - new expire date
     * @param updateChildren - true = update children / false = dont update children
     */
    void updateDisplayPeriodForContent(ContentIdentifier cid, Date publishDate, Date expireDate, boolean updateChildren) throws SystemException;

    interface ContentHandlerStopper {
        boolean isStopRequested();
    }
}
