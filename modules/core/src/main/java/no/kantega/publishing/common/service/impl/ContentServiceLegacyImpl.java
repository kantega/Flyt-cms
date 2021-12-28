package no.kantega.publishing.common.service.impl;

import no.kantega.commons.exception.InvalidFileException;
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.api.path.PathEntry;
import no.kantega.publishing.api.services.ContentManagementService;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.common.exception.ObjectInUseException;
import no.kantega.publishing.common.exception.ObjectLockedException;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.publishing.security.SecuritySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public abstract class ContentServiceLegacyImpl implements ContentManagementService {
    private static final Logger log = LoggerFactory.getLogger(ContentServiceLegacyImpl.class);

    protected abstract SecuritySession getSecuritySession();


    private no.kantega.publishing.common.service.ContentManagementService getCMS() {
        return new no.kantega.publishing.common.service.ContentManagementService(getSecuritySession());
    }

    @Override
    public Content getContentDoNotLog(ContentIdentifier id) throws NotAuthorizedException {
        return getCMS().getContent(id, false);
    }


    @Override
    public Content getContent(ContentIdentifier id) throws NotAuthorizedException {
        return getCMS().getContent(id);
    }

    @Override
    public Content checkOutContent(ContentIdentifier id) throws NotAuthorizedException, ObjectLockedException, ContentNotFoundException {
        try {
            return getCMS().checkOutContent(id);
        } catch (InvalidFileException | InvalidTemplateException e) {
            log.error("Error checking out Content " + id, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Content getLastVersionOfContent(ContentIdentifier id) throws ContentNotFoundException, NotAuthorizedException {
        return getCMS().getLastVersionOfContent(id);
    }

    @Override
    public List<Content> getAllContentVersions(ContentIdentifier id) throws ContentNotFoundException, NotAuthorizedException {
        return getCMS().getAllContentVersions(id);
    }

    @Override
    public Content checkInContent(Content content, ContentStatus newStatus) throws NotAuthorizedException {
        return getCMS().checkInContent(content, newStatus);
    }

    @Override
    public Content createNewContent(ContentCreateParameters parameters) throws NotAuthorizedException {
        try {
            return getCMS().createNewContent(parameters);
        } catch (InvalidFileException | InvalidTemplateException e) {
            log.error("Error creating new Content ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Content copyContent(Content sourceContent, Association target, AssociationCategory category, boolean copyChildren) throws NotAuthorizedException {
        return getCMS().copyContent(sourceContent, target, category, copyChildren);
    }

    @Override
    public void setContentVisibilityStatus(Content content, ContentVisibilityStatus newVisibilityStatus) {
        getCMS().setContentVisibilityStatus(content, newVisibilityStatus);
    }

    @Override
    public Content setContentStatus(ContentIdentifier cid, ContentStatus newStatus, String note) throws NotAuthorizedException {
        return getCMS().setContentStatus(cid, newStatus, note);
    }

    @Override
    public void deleteContent(ContentIdentifier id) throws ObjectInUseException, NotAuthorizedException {
        getCMS().deleteContent(id);
    }

    @Override
    public void deleteContentVersion(ContentIdentifier id) throws NotAuthorizedException {
        getCMS().deleteContentVersion(id);
    }

    @Override
    public List<Content> getContentList(ContentQuery query, boolean getAttributes, boolean getTopics) {
        return getCMS().getContentList(query, getAttributes, getTopics);
    }

    @Override
    public List<Content> getContentList(ContentQuery query) {
        return getCMS().getContentList(query);
    }

    @Override
    public List<Content> getContentSummaryList(ContentQuery query) {
        return getCMS().getContentSummaryList(query);
    }

    @Override
    public List<WorkList<Content>> getMyContentList() {
        return getCMS().getMyContentList();
    }

    @Override
    public List<Content> getContentListForApproval() {
        return getCMS().getContentListForApproval();
    }

    @Override
    public ContentIdentifier getParent(ContentIdentifier cid){
        return getCMS().getParent(cid);
    }

    @Override
    public void updateDisplayPeriodForContent(ContentIdentifier cid, Date publishDate, Date expireDate, boolean updateChildren) throws NotAuthorizedException {
        getCMS().updateDisplayPeriodForContent(cid, publishDate, expireDate, updateChildren);
    }

    @Override
    public List<UserContentChanges> getNoChangesPerUser(int months) {
        return getCMS().getNoChangesPerUser(months);
    }

    @Override
    public List<PathEntry> getPathByAssociation(Association association) {
        return getCMS().getPathByAssociation(association);
    }

    @Override
    public void copyAssociations(Association source, Association target, AssociationCategory category, boolean copyChildren) {
        getCMS().copyAssociations(source, target, category, copyChildren);
    }

    @Override
    public void addAssociation(Association association) {
        getCMS().addAssociation(association);
    }

    @Override
    public List<Content> deleteAssociationsById(List<Integer> associationIds, boolean deleteMultiple) {
        int[] ids = new int[associationIds.size()];
        for (int i = 0; i < associationIds.size(); i++) {
            ids[i] = associationIds.get(i);
        }
        return getCMS().deleteAssociationsById(ids, deleteMultiple);
    }

    @Override
    public void modifyAssociation(Association association) {
        getCMS().modifyAssociation(association);
    }

    @Override
    public List<DeletedItem> getDeletedItems() {
        return getCMS().getDeletedItems();
    }

    @Override
    public int restoreDeletedItem(int id) {
        return getCMS().restoreDeletedItem(id);
    }

    @Override
    public Attachment getAttachment(int id, int siteId) throws NotAuthorizedException {
        return getCMS().getAttachment(id, siteId);
    }

    @Override
    public void streamAttachmentData(int id, InputStreamHandler ish) {
        getCMS().streamAttachmentData(id, ish);
    }

    @Override
    public int setAttachment(Attachment attachment) throws NotAuthorizedException {
        try {
            return getCMS().setAttachment(attachment);
        } catch (SQLException e) {
            log.error("Error setting attachment", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAttachment(int id) throws NotAuthorizedException {
        getCMS().deleteAttachment(id);
    }

    @Override
    public List<Attachment> getAttachmentList(ContentIdentifier id) {
        return getCMS().getAttachmentList(id);
    }
}
