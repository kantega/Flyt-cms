package no.kantega.publishing.content.api;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.common.ao.ContentHandler;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.exception.ObjectInUseException;
import no.kantega.publishing.org.OrgUnit;
import no.kantega.publishing.security.data.User;

import java.util.Date;
import java.util.List;

public interface ContentAO {
    ContentIdentifier deleteContent(ContentIdentifier cid) throws SystemException, ObjectInUseException;

    void forAllContentObjects(ContentHandler contentHandler, ContentHandlerStopper stopper);

    void deleteContentVersion(ContentIdentifier cid, boolean deleteActiveVersion) throws SystemException;

    List<Content> getAllContentVersions(ContentIdentifier cid) throws SystemException;

    Content checkOutContent(ContentIdentifier cid) throws SystemException;

    Content getContent(ContentIdentifier cid, boolean isAdminMode) throws SystemException;

    Content getContent(OrgUnit orgUnit) throws SystemException;

    String getTitleByAssociationId(int associationId);

    List<Content> getContentList(ContentQuery contentQuery, int maxElements, SortOrder sort, boolean getAttributes) throws SystemException;

    List<Content> getContentList(ContentQuery contentQuery, int maxElements, SortOrder sort, boolean getAttributes, boolean getTopics) throws SystemException;

    List<Content> getContentListForApproval();

    List<WorkList<Content>> getMyContentList(User user);

    void doForEachInContentList(ContentQuery contentQuery, int maxElements, SortOrder sort, ContentHandler handler) throws SystemException;

    ContentIdentifier getParent(ContentIdentifier cid) throws SystemException;

    Content checkInContent(Content content, ContentStatus newStatus) throws SystemException;

    Content setContentStatus(ContentIdentifier cid, ContentStatus newStatus, Date newPublishDate, String userId) throws SystemException;

    int getNextExpiredContentId(int after) throws SystemException;

    int getNextWaitingContentId(int after) throws SystemException;

    int getNextActivationContentId(int after) throws SystemException;

    void setContentVisibilityStatus(int contentId, int newStatus) throws SystemException;

    void setNumberOfNotes(int contentId, int count) throws SystemException;

    List<UserContentChanges> getNoChangesPerUser(int months) throws SystemException;

    int getContentCount() throws SystemException;

    int getLinkCount() throws SystemException;

    int getContentProducerCount() throws SystemException;

    void updateContentFromTemplates(TemplateConfiguration templateConfiguration);

    boolean hasBeenPublished(int contentId);

    void updateDisplayPeriodForContent(ContentIdentifier cid, Date publishDate, Date expireDate, boolean updateChildren);

    public interface ContentHandlerStopper {
        public boolean isStopRequested();
    }
}
