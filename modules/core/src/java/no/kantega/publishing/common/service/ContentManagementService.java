/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.common.service;

import com.google.common.base.Predicate;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import no.kantega.commons.exception.InvalidFileException;
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.HttpHelper;
import no.kantega.publishing.admin.content.util.EditContentHelper;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.api.path.PathEntry;
import no.kantega.publishing.api.xmlcache.XMLCacheEntry;
import no.kantega.publishing.api.xmlcache.XmlCache;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.*;
import no.kantega.publishing.common.cache.AssociationCategoryCache;
import no.kantega.publishing.common.cache.ContentTemplateCache;
import no.kantega.publishing.common.cache.DisplayTemplateCache;
import no.kantega.publishing.common.cache.DocumentTypeCache;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.enums.AssociationType;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.publishing.common.data.enums.ExpireAction;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.common.exception.InvalidTemplateReferenceException;
import no.kantega.publishing.common.exception.ObjectInUseException;
import no.kantega.publishing.common.exception.ObjectLockedException;
import no.kantega.publishing.common.service.impl.PathWorker;
import no.kantega.publishing.common.service.impl.SiteMapWorker;
import no.kantega.publishing.common.service.lock.ContentLock;
import no.kantega.publishing.common.service.lock.LockManager;
import no.kantega.publishing.common.traffic.TrafficLogger;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.publishing.common.util.templates.TemplateHelper;
import no.kantega.publishing.content.api.ContentAO;
import no.kantega.publishing.content.api.ContentIdHelper;
import no.kantega.publishing.event.ContentEvent;
import no.kantega.publishing.event.ContentEventListener;
import no.kantega.publishing.eventlog.Event;
import no.kantega.publishing.eventlog.EventLog;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.security.data.enums.Privilege;
import no.kantega.publishing.spring.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.*;

import static com.google.common.collect.Collections2.filter;

/**
 *  A monolithic service for fetching content objects, saving them, deleting them,
 *  as well as some other snacksy operations.
 */
public class ContentManagementService {
    private static final Logger log = LoggerFactory.getLogger(ContentManagementService.class);
    HttpServletRequest request = null;

    SecuritySession securitySession = null;
    private final Ehcache contentCache;
    private final Ehcache contentListCache;
    private final Ehcache siteMapCache;
    private final XmlCache xmlCache;
    private EventLog eventLog;
    private boolean cachingEnabled;
    private TrafficLogger trafficLogger;
    private SiteCache siteCache;
    private ContentAO contentAO;
    private ContentIdHelper contentIdHelper;
    private ContentEventListener contentNotifier;

    private ContentManagementService() {
        ApplicationContext context = RootContext.getInstance();

        CacheManager cacheManager = context.getBean(CacheManager.class);
        contentCache = cacheManager.getEhcache("ContentCache");
        contentListCache = cacheManager.getEhcache("ContentListCache");
        siteMapCache = cacheManager.getEhcache("SiteMapCache");

        eventLog = context.getBean(EventLog.class);
        trafficLogger = context.getBean(TrafficLogger.class);
        xmlCache = context.getBean(XmlCache.class);

        contentAO = context.getBean(ContentAO.class);
        contentIdHelper = context.getBean(ContentIdHelper.class);
        cachingEnabled = Aksess.getConfiguration().getBoolean("caching.enabled", false);
        contentNotifier = context.getBean("contentListenerNotifier", ContentEventListener.class);
    }

    public ContentManagementService(HttpServletRequest request) throws SystemException {
        this();
        this.request = request;
        this.securitySession = SecuritySession.getInstance(request);
    }

    public ContentManagementService(SecuritySession securitySession) throws SystemException {
        this();
        this.securitySession = securitySession;
    }

    public ContentManagementService(SecuritySession securitySession, HttpServletRequest request) {
        this();
        this.request = request;
        this.securitySession = securitySession;
    }


    /**
     * @return - the user's SecuritySession
     */
    public SecuritySession getSecuritySession() {
        return securitySession;
    }


    /**
     * Check out an content object. The difference from getContent is that checking out
     * indicates that we want to change the object, so a Lock is created.
     * @param id - ContentIdentifier for the Content object
     * @return Content object associated with the ContentIdentifier or null if the content does not exist.
     * @throws SystemException - System error
     * @throws NotAuthorizedException - if the user is not authorized to update the content.
     * @throws InvalidFileException - The template
     * @throws InvalidTemplateException - invalid template
     * @throws ObjectLockedException - if the Content object is already checked out.
     */
    public Content checkOutContent(ContentIdentifier id) throws SystemException, NotAuthorizedException, InvalidFileException, InvalidTemplateException, ObjectLockedException {
        contentIdHelper.assureContentIdAndAssociationIdSet(id);
        ContentLock lock = LockManager.peekAtLock(id.getContentId());
        if(lock != null && !lock.getOwner().equals(securitySession.getUser().getId())) {
            throw new ObjectLockedException(securitySession.getUser().getId());
        }

        Content c = contentAO.checkOutContent(id);
        if (!securitySession.isAuthorized(c, Privilege.UPDATE_CONTENT)) {
            throw new NotAuthorizedException(securitySession.getUser().getId() + " not authorized to checkOutContent: " + c.getId());
        }

        if (request != null) {
            log.info( "Locking contentid: " + c.getId() + " for user: " + securitySession.getUser().getId() + " with IP: " + request.getRemoteAddr());
        }

        // Reset minor change field
        c.setMinorChange(Aksess.isDefaultMinorChange());

        LockManager.lockContent(securitySession.getUser().getId(), c.getId());

        EditContentHelper.updateAttributesFromTemplate(c);

        return c;
    }


    public Content createNewContent(ContentCreateParameters parameters) throws SystemException, InvalidFileException, InvalidTemplateException, NotAuthorizedException {
        Content content = EditContentHelper.createContent(securitySession, parameters);

        Map<String, String> defaultValues = parameters.getDefaultValues();
        User user = securitySession.getUser();
        defaultValues.put(AttributeDefaultValues.USER_ID, user.getId());
        defaultValues.put(AttributeDefaultValues.USER_NAME, user.getName());
        defaultValues.put(AttributeDefaultValues.USER_DEPARTMENT, user.getDepartment());
        defaultValues.put(AttributeDefaultValues.USER_EMAIL, user.getEmail());

        Calendar cal = new GregorianCalendar(Aksess.getDefaultLocale());
        defaultValues.put(AttributeDefaultValues.YEAR, "" + cal.get(Calendar.YEAR));
        int month = cal.get(Calendar.MONTH)+1;
        String m = String.valueOf((month<10) ? "0"+month : month);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String d = (day<10) ? "0"+day : ""+day;
        defaultValues.put(AttributeDefaultValues.MONTH, m);
        defaultValues.put(AttributeDefaultValues.DAY, d);
        defaultValues.put(AttributeDefaultValues.WEEK, String.valueOf(cal.get(Calendar.WEEK_OF_YEAR)));

        // Last attributter fra XML fil
        EditContentHelper.updateAttributesFromTemplate(content, parameters.getDefaultValues());

        // Kjør plugins
        contentNotifier.contentCreated(new ContentEvent()
                .setContent(content)
                .setUser(securitySession.getUser()));

        return content;
    }


    /**
     * Henter ut siste versjon av innholdsobjekt fra basen
     * @param id  - ContentIdentifier til innholdsobjekt
     * @return Innholdsobjekt
     * @throws SystemException
     * @throws NotAuthorizedException - dersom bruker ikke har tilgang
     */
    public Content getLastVersionOfContent(ContentIdentifier id) throws SystemException, NotAuthorizedException {
        Content c = contentAO.getContent(id, true);
        if (c != null) {
            assertCanView(c, true, securitySession);
        }
        return c;
    }

    /**
     * Henter ut innholdsobjekt fra basen og logger hvis spesifisert
     * @param id  - ContentIdentifier til innholdsobjekt
     * @param logView - Angi om visning av side skal logges
     * @return Innholdsobjekt
     * @throws SystemException
     * @throws NotAuthorizedException - dersom bruker ikke har tilgang
     */
    public Content getContent(ContentIdentifier id, boolean logView) throws SystemException, NotAuthorizedException {
        boolean adminMode = HttpHelper.isAdminMode(request);

        Content c = getContentFromCache(id, adminMode);
        if (c != null) {
            assertCanView(c, adminMode, securitySession);
        }
        if (c != null && logView && request != null) {
            trafficLogger.log(c, request);
        }
        return c;
    }

    private Content getContentFromCache(ContentIdentifier id, boolean adminMode) {
        if(shouldUseCache(id, adminMode)) {
            contentIdHelper.assureAssociationIdSet(id);
            final Object key = id.getAssociationId();
            final Element element = contentCache.get(key);

            if(element == null) {
                Content content = contentAO.getContent(id, adminMode);
                contentCache.put(new Element(key, content));
                return content;
            } else {
                return (Content) element.getObjectValue();
            }
        } else {
            return contentAO.getContent(id, adminMode);
        }
    }

    private boolean shouldUseCache(ContentIdentifier id, boolean adminMode) {
        return cachingEnabled && !adminMode && id.getVersion() == -1 && id.getStatus() == null;
    }

    /**
     * Henter ut innholdsobjekt fra basen
     * @param id - ContentIdentifier til innholdsobjekt
     * @return Innholdsobjekt
     * @throws SystemException
     * @throws NotAuthorizedException - dersom bruker ikke har tilgang
     */
    public Content getContent(ContentIdentifier id) throws SystemException, NotAuthorizedException {
        return getContent(id, false);
    }

    private void assertCanView(Content c, boolean adminMode, SecuritySession securitySession) throws NotAuthorizedException, SystemException {
        String userId = null;
        if (securitySession.isLoggedIn()) {
            userId = securitySession.getUser().getId();
        }

        if (!securitySession.isAuthorized(c, Privilege.VIEW_CONTENT)) {
            if (securitySession.getUser() != null){
                throw new NotAuthorizedException(securitySession.getUser().getId() + " not authorized to view: " + c.getId());
            }  else{
                throw new NotAuthorizedException("Not authorized to view Content " + c.getId() + ", No user is logged in");
            }
        }

        if(c.getStatus() == ContentStatus.HEARING && !securitySession.isUserInRole(Aksess.getQualityAdminRole()) && !HearingAO.isHearingInstance(c.getVersionId(), securitySession.getUser().getId()) && !adminMode && !c.getModifiedBy().equals(userId)) {
            throw new NotAuthorizedException("User is neither in admin mode or hearing instance");
        }

        if (c.getStatus() == ContentStatus.DRAFT && !adminMode && !c.getModifiedBy().equals(userId) && !c.getOwnerPerson().equals(userId)) {
            throw new NotAuthorizedException("Object is draft, must view in admin mode: " + c.getId());
        }
    }


    /**
     * Henter ut alle innholdsversjoner av en side
     * @param id - ContentIdentifier til innholdsobjekt
     * @return - liste med innholdsobjekter
     * @throws SystemException
     */
    public List<Content> getAllContentVersions(ContentIdentifier id) throws SystemException {
        return contentAO.getAllContentVersions(id);
    }


    /**
     * Lagrer et innholdsobjekt med en gitt status. Oppretter i gitte tilfeller en ny versjon.
     * Legger til objektet i søkeindeks dersom status = Publish
     * @param content - Endret objekt
     * @param newStatus - Status som skal settes på nytt objekt
     * @return the new, saved, content object.
     * @throws SystemException
     * @throws NotAuthorizedException
     */
    public Content checkInContent(Content content, ContentStatus newStatus) throws SystemException, NotAuthorizedException {
        LockManager.releaseLock(content.getId());
        boolean hasBeenPublished = contentAO.hasBeenPublished(content.getId());
        boolean isNewContent = content.isNew();

        if (!securitySession.isAuthorized(content, Privilege.UPDATE_CONTENT)) {
            throw new NotAuthorizedException(securitySession.getUser().getId() + " not authorized to checkInContent: " + content.getId());
        }

        String userId = securitySession.getUser().getId();
        content.setModifiedBy(userId);
        Date currentTime = new Date();
        content.setLastModified(currentTime);

        if (isNewContent || !content.isMinorChange()) {
            content.setLastMajorChangeBy(userId);
            content.setLastMajorChange(currentTime);
        }

        // Check if user is authorized to publish directly
        if (newStatus == ContentStatus.WAITING_FOR_APPROVAL ||
                newStatus == ContentStatus.PUBLISHED && !securitySession.isAuthorized(content, Privilege.APPROVE_CONTENT)) {
            // User is not authorized, set waiting status
            newStatus = ContentStatus.WAITING_FOR_APPROVAL;
            content.setApprovedBy("");
        } else {
            content.setApprovedBy(userId);
        }

        // Check if change should be active now
        if (content.getChangeFromDate() != null) {
            if (content.getChangeFromDate().after(currentTime)) {
                // Change should not be active yet
                if (newStatus == ContentStatus.PUBLISHED) {
                    newStatus = ContentStatus.PUBLISHED_WAITING;
                }
            } else {
                // Reset change from date, since it has passed
                content.setChangeFromDate(null);
            }
        }

        if (newStatus == ContentStatus.PUBLISHED) {
            if (content.getPublishDate() == null) {
                // If page is published, publish date must be set
                content.setPublishDate(currentTime);
            }
            if ((!isNewContent) && (!hasBeenPublished)) {
                // If the content has not been published before (e.g only saved as draft) and publish date has been set to be some time earlier than the publishing
                // is performed, set the publish date to the exact time when the content is published.
                // This is necessary because MailSubscriptionAgent checks for content with publish date after last job execution.
                if (content.getPublishDate().before(currentTime)) {
                    content.setPublishDate(currentTime);
                }
            }
        } else {
            if ((!isNewContent) && hasBeenPublished && content.getPublishDate() == null) {
                // If page has been published before, publish date must be set
                content.setPublishDate(currentTime);
            }
        }

        if (content.getPublishDate() != null && content.getPublishDate().after(currentTime) || newStatus == ContentStatus.WAITING_FOR_APPROVAL) {
            // Content is waiting to become active
            content.setVisibilityStatus(ContentVisibilityStatus.WAITING);
        } else if (content.getExpireDate() != null && content.getExpireDate().before(currentTime)) {
            // Content is expired
            if (content.getExpireAction () == ExpireAction.ARCHIVE) {
                content.setVisibilityStatus(ContentVisibilityStatus.ARCHIVED);
            } else {
                content.setVisibilityStatus(ContentVisibilityStatus.EXPIRED);
            }
        } else {
            content.setVisibilityStatus(ContentVisibilityStatus.ACTIVE);
        }

        if (!isNewContent && content.getDisplayTemplateId() > 0) {
            DisplayTemplate displayTemplate = getDisplayTemplate(content.getDisplayTemplateId());
            if (displayTemplate.isNewGroup()) {
                content.setGroupId(content.getId());
            }
        }

        if (content.getStatus() == ContentStatus.HEARING) {
            expireHearing(content);
        }

        contentNotifier.beforeContentSave(new ContentEvent()
                .setContent(content)
                .setUser(securitySession.getUser()));

        Content c = contentAO.checkInContent(content, newStatus);

        if (c.getStatus() == ContentStatus.HEARING) {
            saveHearing(content);
        }

        ContentEvent event = new ContentEvent()
                .setContent(c)
                .setUser(securitySession.getUser());
        contentNotifier.contentSaved(event);

        if (isNewContent) {
            contentNotifier.newContentSaved(event);
        }

        if (newStatus == ContentStatus.PUBLISHED && content.getVisibilityStatus() == ContentVisibilityStatus.ACTIVE && ! hasBeenPublished) {
            contentNotifier.newContentPublished(event);
        }

        String eventName;
        switch (c.getStatus()) {
            case DRAFT:
                eventName = Event.SAVE_DRAFT;
                break;
            case WAITING_FOR_APPROVAL:
                eventName = Event.SEND_FOR_APPROVAL;
                break;
            default:
                eventName = Event.PUBLISH_CONTENT;
        }
        eventLog.log(securitySession, request, eventName, c.getTitle(), c);

        return c;
    }

    private void saveHearing(Content content) {
        Hearing hearing = content.getHearing();
        hearing.setContentVersionId(content.getVersionId());
        int hearingId = HearingAO.saveOrUpdate(hearing);
        for (HearingInvitee invitee : hearing.getInvitees()) {
            invitee.setHearingId(hearingId);
            HearingAO.saveOrUpdate(invitee);
        }
    }

    private void expireHearing(Content content) {
        Hearing hearing = HearingAO.getHearingByContentVersion(content.getVersionId());
        if (hearing != null) {
            Date now = new Date();
            if (hearing.getDeadLine().getTime() > now.getTime()) {
                hearing.setDeadLine(now);
                HearingAO.saveOrUpdate(hearing);
            }
        }
    }


    /**
     * Create a copy of the source Content and put it under the given target.
     * @param sourceContent - The Content that is to be copied.
     * @param target - The association under which the copy is to be placed.
     * @param category - The AssociationCategory to put the copy into.
     * @param copyChildren - indicate whether the operation should be recursive, and copy the source's children as well.
     * @return The new copy of sourceContent.
     * @throws SystemException
     * @throws NotAuthorizedException
     */
    public Content copyContent(Content sourceContent, Association target, AssociationCategory category, boolean copyChildren) throws SystemException, NotAuthorizedException {
        ContentIdentifier parentCid =  ContentIdentifier.fromAssociationId(target.getAssociationId());
        log.info( "Copying Content {} to target {} in category {}.", sourceContent.getAssociation().getId(), target.getAssociationId(), category.getId());

        Content destParent = contentAO.getContent(parentCid, true);
        ContentIdentifier origialContentIdentifier = sourceContent.getContentIdentifier();

        Content newContent = copyContent(sourceContent);

        // Ta egenskaper fra ny parent
        newContent.setSecurityId(destParent.getSecurityId());
        newContent.setOwner(destParent.getOwner());
        newContent.setOwnerPerson(destParent.getOwnerPerson());
        newContent.setLanguage(destParent.getLanguage());

        if (sourceContent.getDisplayTemplateId() > 0) {
            DisplayTemplate displayTemplate = DisplayTemplateCache.getTemplateById(sourceContent.getDisplayTemplateId());
            if (displayTemplate.isNewGroup()) {
                // Arver egenskaper fra sider over.  GroupId brukes til å lage ting som skal være spesielt for en struktur, f.eks meny
                newContent.setGroupId(destParent.getGroupId());
            }
        }

        // Legg til kopling til parent
        Association association = new Association();

        association.setParentAssociationId(target.getId());
        association.setCategory(category);
        association.setSiteId(target.getSiteId());

        newContent.setAssociations(Collections.singletonList(association));

        contentNotifier.contentCreated(new ContentEvent()
                .setContent(newContent)
                .setUser(securitySession.getUser()));

        Content content = checkInContent(newContent, sourceContent.getStatus());
        AttachmentAO.copyAttachment(origialContentIdentifier.getContentId(), content.getId());

        if(copyChildren){
            log.info( "Copying children of Content " + sourceContent.getAssociation().getId());
            ContentQuery query = new ContentQuery();
            query.setAssociatedId(origialContentIdentifier);

            for (Content child : getContentList(query, -1, null)) {
                Association childAssociation = content.getAssociation();
                copyContent(child, childAssociation, childAssociation.getCategory(), true);
            }
        }
        return content;
    }

    private Content copyContent(Content sourceContent) {
        Content newContent = new Content();
        newContent.setTitle(sourceContent.getTitle());
        newContent.setAltTitle(sourceContent.getAltTitle());
        newContent.setApprovedBy(sourceContent.getApprovedBy());
        newContent.setAttributes(sourceContent.getAttributes(AttributeDataType.CONTENT_DATA), AttributeDataType.CONTENT_DATA);
        newContent.setAttributes(sourceContent.getAttributes(AttributeDataType.META_DATA), AttributeDataType.META_DATA);
        newContent.setChangeDescription(sourceContent.getChangeDescription());
        newContent.setChangeFromDate(sourceContent.getChangeFromDate());
        newContent.setContentTemplateId(sourceContent.getContentTemplateId());
        newContent.setDisplayTemplateId(sourceContent.getDisplayTemplateId());
        newContent.setDocumentTypeId(sourceContent.getDocumentTypeId());
        newContent.setDocumentTypeIdForChildren(sourceContent.getDocumentTypeIdForChildren());
        newContent.setCreateDate(sourceContent.getCreateDate());
        newContent.setExpireDate(sourceContent.getExpireDate());
        newContent.setExpireAction(sourceContent.getExpireAction());
        newContent.setDoOpenInNewWindow(sourceContent.isOpenInNewWindow());
        newContent.setForumId(sourceContent.getForumId());
        newContent.setHearing(sourceContent.getHearing());
        newContent.setImage(sourceContent.getImage());
        newContent.setKeywords(sourceContent.getKeywords());
        newContent.setLastMajorChange(sourceContent.getLastMajorChange());
        newContent.setLastMajorChangeBy(sourceContent.getLastMajorChangeBy());
        newContent.setLastModified(sourceContent.getLastModified());
        newContent.setLocation(sourceContent.getLocation());
        newContent.setMetaDataTemplateId(sourceContent.getMetaDataTemplateId());
        newContent.setModifiedBy(sourceContent.getModifiedBy());
        newContent.setApprovedBy(sourceContent.getApprovedBy());
        newContent.setNumberOfComments(sourceContent.getNumberOfComments());
        newContent.setNumberOfNotes(sourceContent.getNumberOfNotes());
        newContent.setNumberOfRatings(sourceContent.getNumberOfRatings());
        newContent.setPublishDate(sourceContent.getPublishDate());
        newContent.setPublisher(sourceContent.getPublisher());
        newContent.setRatingScore(sourceContent.getRatingScore());
        newContent.setRevisionDate(sourceContent.getRevisionDate());
        newContent.setSearchable(sourceContent.isSearchable());
        newContent.setStatus(sourceContent.getStatus());
        newContent.setTopics(sourceContent.getTopics());
        newContent.setType(sourceContent.getType());
        newContent.setVisibilityStatus(sourceContent.getVisibilityStatus());
        return newContent;
    }

    /**
     * Updates the visibility status of a content object
     * @param content to set visibility status for
     * @param newVisibilityStatus the new status
     */
    public void setContentVisibilityStatus(Content content, ContentVisibilityStatus newVisibilityStatus) {
        contentAO.setContentVisibilityStatus(content.getId(), newVisibilityStatus);

        ContentEvent event = new ContentEvent()
                .setContent(content)
                .setUser(securitySession.getUser());

        if (newVisibilityStatus == ContentVisibilityStatus.ARCHIVED || newVisibilityStatus == ContentVisibilityStatus.EXPIRED) {
            contentNotifier.contentExpired(event);
        } else if (newVisibilityStatus == ContentVisibilityStatus.ACTIVE) {
            contentNotifier.contentActivated(event);
            if (content.getStatus() == ContentStatus.PUBLISHED) {
                contentNotifier.newContentPublished(event);
            }
        }
        eventLog.log(securitySession, request, "CV-STATUS-" + newVisibilityStatus, content.getTitle(), content);
    }

    /**
     * Setter ny status på et objekt, f.eks ved godkjenning av en side.
     * @param cid - ContentIdenfier for nytt objekt
     * @param newStatus - Ny status
     * @param note - melding
     * @return the content object identified by the given ContentIdenfier with the new status.
     * @throws NotAuthorizedException
     * @throws SystemException
     */
    public Content setContentStatus(ContentIdentifier cid, ContentStatus newStatus, String note) throws NotAuthorizedException, SystemException {
        Content c = getContent(cid);
        boolean hasBeenPublished = contentAO.hasBeenPublished(c.getId());
        if (!securitySession.isAuthorized(c, Privilege.APPROVE_CONTENT)) {
            throw new NotAuthorizedException(securitySession.getUser().getId() + " not authorized to setContentStatus: " + c.getId());
        }

        if (note != null && note.length() > 0) {
            Note n = new Note();
            n.setAuthor(securitySession.getUser().getName());
            n.setDate(new Date());
            n.setText(note);
            n.setContentId(cid.getContentId());

            contentIdHelper.assureContentIdAndAssociationIdSet(cid);
            int contentId = cid.getContentId();
            n.setContentId(contentId);

            NotesDao notesDao = (NotesDao)RootContext.getInstance().getBean("aksessNotesDao");
            notesDao.addNote(n);
            int count = notesDao.getNotesByContentId(contentId).size();
            contentAO.setNumberOfNotes(contentId, count);
        }

        Date newPublishDate = null;

        String event = Event.APPROVED;
        if (newStatus == ContentStatus.REJECTED) {
            event = Event.REJECTED;
        } else if (newStatus == ContentStatus.PUBLISHED) {
            Date currentTime = new Date();

            if (c.getPublishDate() == null) {
                newPublishDate = currentTime;
            } else if (! hasBeenPublished) {
                // If the content has not been published before and publish date has been set to be some time earlier than the publishing
                // is performed, set the publish date to the exact time when the content is published.
                // This is necessary because MailSubscriptionAgent checks for content with publish date after last job execution.
                if (c.getPublishDate().before(currentTime)) {
                    newPublishDate = currentTime;
                }
            }
        }

        eventLog.log(securitySession, request, event, c.getTitle(), c);
        Content content = contentAO.setContentStatus(cid, newStatus, newPublishDate, securitySession.getUser().getId());

        ContentEvent contentEvent = new ContentEvent()
                .setContent(content)
                .setUser(securitySession.getUser());

        if (newStatus == ContentStatus.PUBLISHED && content.getVisibilityStatus() == ContentVisibilityStatus.ACTIVE && ! hasBeenPublished) {
            contentNotifier.newContentPublished(contentEvent);
        }

        contentNotifier.contentStatusChanged(contentEvent);

        return content;
    }

    /**
     * Delete all versions of the Content object.
     * @param id - ContentIdenfier of one of the ContentVersions
     * @throws SystemException
     * @throws ObjectInUseException
     * @throws NotAuthorizedException
     */
    public void deleteContent(ContentIdentifier id) throws SystemException, ObjectInUseException, NotAuthorizedException {
        String title = null;

        if (id != null) {
            Content c = getContent(id);
            if (c != null) {
                int priv = Privilege.UPDATE_CONTENT;
                if (c.getVersion() > 1 || c.getStatus() == ContentStatus.PUBLISHED) {
                    // Hvis siden er publisert eller versjon > 1 før ikke slettet uten godkjenningsrett
                    priv = Privilege.APPROVE_CONTENT;
                }
                if (!securitySession.isAuthorized(c, priv)) {
                    throw new NotAuthorizedException(securitySession.getUser().getId() + " not authorized to deleteContent: " + c.getId());
                }
                if (Aksess.isEventLogEnabled()) {
                    title = c.getTitle();
                }

                contentNotifier.beforeContentDelete(new ContentEvent()
                        .setContent(c)
                        .setCanDelete(true)
                        .setUser(securitySession.getUser()));

                contentAO.deleteContent(id);
                if (title != null) {
                    eventLog.log(securitySession, request, Event.DELETE_CONTENT, title);
                }

                contentNotifier.contentDeleted(new ContentEvent()
                        .setContent(c)
                        .setUser(securitySession.getUser()));


            }
        }
    }


    /**
     * Sletter en bestemt versjon av et innholdsobjekt.  Dersom objektversjonen er aktiv blir den ikke slettet.
     * @param id - Innholdsid
     * @throws SystemException
     * @throws NotAuthorizedException
     */
    public void deleteContentVersion(ContentIdentifier id) throws SystemException, NotAuthorizedException {
        String title = null;

        if (id != null) {
            Content c = getContent(id);
            if (!securitySession.isAuthorized(c, Privilege.APPROVE_CONTENT)) {
                throw new NotAuthorizedException(securitySession.getUser().getId() + " not authorized to deleteContentVersion: " + c.getId());
            }
            if (c != null) {
                title = c.getTitle();
            }
        }
        contentAO.deleteContentVersion(id, false);
        if (title != null) {
            eventLog.log(securitySession, request, Event.DELETE_CONTENT_VERSION, title);
        }
    }


    /**
     * Henter en liste med innholdsobjekter fra basen
     * @param query - Søk som angir hva som skal hentes
     * @param maxElements - Max antall elementer som skal hentes, -1 for alle
     * @param sort - Sorteringsrekkefølge
     * @param getAttributes - Hent attributter (true) for en side eller bare basisdata (false)
     * @param getTopics - Hent topics (true) for en side eller ikke (false) 
     * @return Liste med innholdsobjekter
     * @throws SystemException
     */
    public List<Content> getContentList(ContentQuery query, int maxElements, SortOrder sort, boolean getAttributes, boolean getTopics) throws SystemException {
        List<Content> list = getContentListFromCache(query, getMaxElementsToGetBeforeAuthorizationCheck(maxElements), sort, getAttributes, getTopics);

        List<Content> approved = new ArrayList<>();

        // Add only elements which user is authorized for, and only get maxElements items
        for (Content content : list) {
            if (securitySession.isAuthorized(content, Privilege.VIEW_CONTENT)) {
                approved.add(content);
            }
            if (maxElements != -1 && maxElements == approved.size()) {
                break;
            }
        }

        return approved;
    }

    private List<Content> getContentListFromCache(ContentQuery query, int maxElements, SortOrder sort, boolean getAttributes, boolean getTopics) {
        if(cachingEnabled) {
            if(maxElements != -1) {
                query.setMaxRecords(maxElements);
            }

            if (sort != null){
                query.setSortOrder(sort);
            }

            ContentQuery.QueryWithParameters qp = query.getQueryWithParameters();

            String key = buildContentListKey(qp, maxElements, sort, getAttributes, getTopics);

            Element element = contentListCache.get(key);

            if(element == null) {
                element = new Element(key, contentAO.getContentList(query, maxElements, sort, getAttributes, getTopics));
                contentListCache.put(element);
            }
            return (List<Content>) element.getObjectValue();
        } else {
            return contentAO.getContentList(query, maxElements, sort, getAttributes, getTopics);
        }
    }

    private String buildContentListKey(ContentQuery.QueryWithParameters qp, int maxElements, SortOrder sort, boolean getAttributes, boolean getTopics) {
        StringBuilder keyBuilder = new StringBuilder(qp.getQuery());
        for (Object o : qp.getParams().entrySet()) {
            keyBuilder.append(o);
        }
        keyBuilder.append(maxElements);
        if (sort != null) {
            keyBuilder.append(sort.getSort1());
            keyBuilder.append(sort.getSort2());
            keyBuilder.append(sort.sortDescending());
        }
        keyBuilder.append(getAttributes);
        keyBuilder.append(getTopics);
        return keyBuilder.toString();
    }

    private int getMaxElementsToGetBeforeAuthorizationCheck(int maxElements) {
        int maxElementsToGetBeforeAuthorizationCheck = -1;
        if (maxElements > 10) {
            maxElementsToGetBeforeAuthorizationCheck = maxElements+10;
        } else if (maxElements != -1) {
            maxElementsToGetBeforeAuthorizationCheck = maxElements*2;
        }
        return maxElementsToGetBeforeAuthorizationCheck;
    }

    /**
     * Henter en liste med innholdsobjekter fra basen med innholdsattributter
     * @param query - Søk som angir hva som skal hentes
     * @param maxElements - Max antall elementer som skal hentes, -1 for alle
     * @param sort - Sorteringsrekkefølge
     * @return the Content object matching contentQuery, that the user have access privilegies for.
     * @throws SystemException
     */
    public List<Content> getContentList(ContentQuery query, int maxElements, SortOrder sort) throws SystemException {
        return getContentList(query, maxElements, sort, true, false);
    }


    /**
     * Henter en liste med innholdsobjekter fra basen uten attributter
     * @param query - Søk som angir hva som skal hentes
     * @param maxElements - Max antall elementer som skal hentes, -1 for alle
     * @param sort - Sorteringsrekkefølge
     * @return Liste med innholdsobjekter
     * @throws SystemException
     */
    public List<Content> getContentSummaryList(ContentQuery query, int maxElements, SortOrder sort) throws SystemException {
        return getContentList(query, maxElements, sort, false, false);
    }


    /**
     * Hent innhold som er mitt (dvs min arbeidsliste)
     * @return Liste med innholdsobjekter
     * @throws SystemException
     */
    public List<WorkList<Content>> getMyContentList() throws SystemException {
        if (securitySession != null && securitySession.getUser() != null) {
            return contentAO.getMyContentList(securitySession.getUser());
        }

        return null;
    }


    /**
     * Henter alle innholdsobjekter som kan godkjennes av deg
     * @return Liste med innholdsobjekter
     * @throws SystemException
     */
    public List<Content> getContentListForApproval() throws SystemException {
        if (securitySession == null) {
            return null;
        }

        List<Content> list = contentAO.getContentListForApproval();
        return new ArrayList<>(filter(list, new Predicate<Content>() {
            @Override
            public boolean apply(Content c) {
                return securitySession.isApprover(c);
            }
        }));
    }


    /**
     * Get parent content identifier
     * @param cid of the child we want to get parent identifier for
     * @return ContentIdenfier for the parent of the content identified by cid
     * @throws SystemException
     */
    public ContentIdentifier getParent(ContentIdentifier cid) throws SystemException {
        if(cid == null) {
            return null;
        }
        return contentAO.getParent(cid);
    }


    /**
     * Updates publish date and expire date on a content object and all child objects
     * @param cid - ContentIdentifier to content object
     * @param publishDate - new publish date
     * @param expireDate - new expire date
     * @param updateChildren - true = update children / false = dont update children
     */
    public void updateDisplayPeriodForContent(ContentIdentifier cid, Date publishDate, Date expireDate, boolean updateChildren) throws NotAuthorizedException {
        Content content = contentAO.getContent(cid, true);
        if (content != null) {
            if (securitySession.isAuthorized(content, Privilege.APPROVE_CONTENT)) {
                contentAO.updateDisplayPeriodForContent(cid, publishDate, expireDate, updateChildren);
                eventLog.log(securitySession, request, Event.UPDATE_DISPLAY_PERIOD, content.getTitle());
            } else {
                throw new NotAuthorizedException(securitySession.getUser().getId() + " not authorized to updateDisplayPeriod: " + content.getId());
            }
        }
    }

    /**
     * Henter en liste over antall endringer gjort av brukere
     * @return Liste med innholdsobjekter
     * @throws SystemException
     */
    public List<UserContentChanges> getNoChangesPerUser(int months) throws SystemException {
        return contentAO.getNoChangesPerUser(months);
    }


    /**
     * Hent sitemap
     * @param siteId - Site det skal hentes for
     * @param depth - Antall nivåer som skal hentes
     * @param language - Språk det skal hentes for
     * @param associationCategoryName - Spalte / knytning det skal hentes for.  (F.eks alt som er publisert i "venstremeny"
     * @param rootId - Startpunkt for sitemap
     * @param currentId - Id for side man står på
     * @return
     * @throws SystemException
     */
    public SiteMapEntry getSiteMap(int siteId, int depth, int language, String associationCategoryName, int rootId, int currentId) throws SystemException {
        AssociationCategory category = null;
        if (associationCategoryName != null) {
            category = AssociationCategoryCache.getAssociationCategoryByPublicId(associationCategoryName);
        }
        int path[] = {currentId};

        return getSiteMapFromCache(siteId, depth, language, rootId, path, category);
    }

    /**
     * Hent sitemap
     * @param siteId - Site det skal hentes for
     * @param depth - Antall nivåer som skal hentes
     * @param language - Språk det skal hentes for
     * @param associationCategoryName - Spalte / knytning det skal hentes for.  (F.eks alt som er publisert i "venstremeny"
     * @param rootId - Startpunkt for sitemap
     * @return
     * @throws SystemException
     */
    public SiteMapEntry getSiteMap(int siteId, int depth, int language, String associationCategoryName, int rootId, int[] path) throws SystemException {
        AssociationCategory category = null;
        if (associationCategoryName != null) {
            category = AssociationCategoryCache.getAssociationCategoryByPublicId(associationCategoryName);
        }
        return getSiteMapFromCache(siteId, depth, language, rootId, path, category);
    }


    private SiteMapEntry getSiteMapFromCache(int siteId, int depth, int language, int rootId, int[] path, AssociationCategory category) {
        if (cachingEnabled) {
            int categoryId = category != null ? category.getId() : -1;

            String key = createSiteMapKey(siteId, depth, language, rootId, path, categoryId);

            Element element = siteMapCache.get(key);
            if(element == null) {
                element = new Element(key, SiteMapWorker.getSiteMap(siteId, depth, language, category, rootId, path));
                siteMapCache.put(element);
            }

            return (SiteMapEntry) element.getObjectValue();
        } else {
            return SiteMapWorker.getSiteMap(siteId, depth, language, category, rootId, path);
        }
    }

    private String createSiteMapKey(int siteId, int depth, int language, int rootId, int[] path, int categoryId) {
        return Integer.toString(siteId) + depth + language + rootId + Arrays.toString(path) + categoryId;
    }

    /**
     * Hent meny
     * @param siteId - Site det skal hentes for
     * @param idList - Liste med åpne element i menyen, henter alle med parent som ligger i lista
     * @param sort
     * @param showExpired
     * @return
     * @throws SystemException
     */
    public SiteMapEntry getNavigatorMenu(int siteId, int[] idList, String sort, boolean showExpired) throws SystemException {
        return SiteMapWorker.getPartialSiteMap(siteId, idList, sort, showExpired);
    }


    /**
     * Hent meny
     * @param siteId - Site det skal hentes for
     * @param idList - Liste med åpne element i menyen, henter alle med parent som ligger i lista
     * @param sort
     * @param showExpired
     * @param associationCategories - List of association category ids. Retrieve only content in these categories.
     * @return
     * @throws SystemException
     */
    public SiteMapEntry getNavigatorMenu(int siteId, int[] idList, String sort, boolean showExpired, int[] associationCategories) throws SystemException {
        return SiteMapWorker.getPartialSiteMap(siteId, idList, sort, showExpired, associationCategories);
    }

    /**
     * Hent liste over alle dokumenttyper
     * @return Liste av DocumentType
     * @throws SystemException
     */
    public List<DocumentType> getDocumentTypes() throws SystemException {
        return DocumentTypeCache.getDocumentTypes();
    }

    public DocumentType getDocumentTypeByName(String name) throws SystemException {
        return DocumentTypeCache.getDocumentTypeByPublicId(name);
    }


    /**
     * Hent sti basert på kopling
     * @param association - Kopling til innholdsobjekt
     * @return Liste med PathEntry objekter
     * @throws SystemException
     */
    public List<PathEntry> getPathByAssociation(Association association) throws SystemException {
        List<PathEntry> paths = PathWorker.getPathByAssociation(association);
        if (paths != null && paths.size() > 0) {
            setSiteCacheIfNull();
            Site site = siteCache.getSiteById(paths.get(0).getId());
            if (site != null) {
                paths.get(0).setTitle(site.getName());
            }
        }
        return paths;
    }

    private void setSiteCacheIfNull() {
        if(siteCache == null){
            siteCache = RootContext.getInstance().getBean(SiteCache.class);
        }
    }
    /**
     * Hent sti basert på ContentIdentifier
     * @param cid - Innholdsid
     * @return Liste med PathEntry objekter
     * @throws SystemException
     */
    public List getPathByContentId(ContentIdentifier cid) throws SystemException {
        return PathWorker.getPathByContentId(cid);
    }

    /**
     * Hent en liste med visnings og innholdsmaler som er tillatt for et nettsted og en gitt innholdsmal
     * @param siteId - Nettsted
     * @param parentContentTemplateId - Innholdsmal
     * @return Liste med ContentTemplate og DisplayTemplate objekter
     * @throws SystemException
     */
    public List getAllowedChildTemplates(int siteId, int parentContentTemplateId) throws SystemException, InvalidTemplateReferenceException {
        return TemplateHelper.getAllowedChildTemplates(siteId, parentContentTemplateId);
    }


    /**
     * Hent innholdsmal
     * @param id - id til innholdsmal
     * @return
     * @throws SystemException
     */
    public ContentTemplate getContentTemplate(int id) throws SystemException {
        return ContentTemplateCache.getTemplateById(id);
    }



    /**
     * Hent visningsmal basert på id
     * @param id - Id til visningsmal
     * @return liste med DisplayTemplate objekter
     * @throws SystemException
     */
    public DisplayTemplate getDisplayTemplate(int id) throws SystemException {
        return DisplayTemplateCache.getTemplateById(id);
    }


    /**
     * Hent liste med tillatte visningsmaler for en innholdsmal
     * @param content - side
     * @return
     * @throws SystemException
     */
    public List getAllowedDisplayTemplates(Content content) throws SystemException {
        boolean isAdmin = false;
        if (securitySession.isUserInRole(Aksess.getAdminRole())) {
            isAdmin = true;
        }
        return TemplateHelper.getAllowedDisplayTemplatesForChange(content, isAdmin);
    }


    /**
     * Henter en spalte basert på id
     * @param id - Id til spalten som skal hentes
     * @return
     * @throws SystemException
     */
    public AssociationCategory getAssociationCategory(int id) throws SystemException {
        return AssociationCategoryCache.getAssociationCategoryById(id);
    }


    /**
     * Henter en spalte basert på public id
     * @param id - Id på spalten som skal hentes
     * @return
     * @throws SystemException
     */
    public AssociationCategory getAssociationCategoryByPublicId(String id) throws SystemException {
        return AssociationCategoryCache.getAssociationCategoryByPublicId(id);
    }

    /**
     * Henter en spalte basert på navn
     * @param name - Navnet på spalten som skal hentes
     * @return
     * @throws SystemException
     * @deprecated - Use getAssociationCategoryByPublicId 
     */
    @Deprecated
    public AssociationCategory getAssociationCategoryByName(String name) throws SystemException {
        return AssociationCategoryCache.getAssociationCategoryByPublicId(name);
    }


    /**
     * Setter rekkefølge pø koplinger for sortering i menyer
     * @param associations
     * @throws SystemException
     */
    public void setAssociationsPriority(List<Association> associations) throws SystemException {
        AssociationAO.setAssociationsPriority(associations);
        for (Association association : associations) {
            contentNotifier.setAssociationsPriority(new ContentEvent()
                    .setAssociation(association)
                    .setUser(securitySession.getUser()));
        }
    }

    /**
     * Henter en kopling fra basen
     * @param id - Koplingsid (Unique-id)
     * @return
     * @throws SystemException
     */
    public Association getAssociationById(int id) throws SystemException {
        return AssociationAO.getAssociationById(id);
    }


    /**
     * Kopierer en struktur fra et sted til et annet, dvs krysspubliserer.
     *
     * @param source - Punktet som skal publiseres
     * @param target - Punktes det skal publiseres under
     * @param category - Spalte det skal publiseres til
     * @param copyChildren - Skal barn også kopieres
     * @throws SystemException
     */
    public void copyAssociations(Association source, Association target, AssociationCategory category, boolean copyChildren) throws SystemException {
        AssociationAO.copyAssociations(source, target, category, copyChildren);
        contentNotifier.associationCopied(new ContentEvent()
                .setAssociation(source)
                .setUser(securitySession.getUser()));
    }


    /**
     * Legger til en kopling i basen
     *
     * @param association  - Kopling som skal legges til
     * @throws SystemException
     */
    public void addAssociation(Association association) throws SystemException {
        AssociationAO.addAssociation(association);
        contentNotifier.associationAdded(new ContentEvent()
                .setAssociation(association)
                .setUser(securitySession.getUser()));
    }


    /**
     * Sletter de angitte koplinger fra basen, dvs markerer dem som slettet. Legger innslag i deleteditems
     * slik at brukeren kan gjenopprette dem senere.
     *
     * Dersom deleteMultiple = false og det finnes underobjekter vil ikke sletting bli utført, men
     * man før en liste med hva som blir slettet, som kan vises for brukeren
     *
     * @param associationIds - Koplinger som skal slettes
     * @param deleteMultiple - Må være satt til true for å utføre sletting hvis det finnes underobjekter
     * @return The content objects to which the associations deleted pointed to.
     * @throws SystemException
     */
    public List<Content> deleteAssociationsById(int[] associationIds, boolean deleteMultiple) throws SystemException {
        List<Association> associations = new ArrayList<>();

        for (int associationId : associationIds) {
            Association a = AssociationAO.getAssociationById(associationId);
            if (a != null) {
                if (a.getAssociationtype() == AssociationType.SHORTCUT) {
                    // Sjekk tilgangen til snarvei
                    if (securitySession.isAuthorized(a, Privilege.APPROVE_CONTENT)) {
                        associations.add(a);
                    }
                } else {
                    // Sjekk tilgangen til innholdsobjektet den peker på
                    ContentIdentifier cid =  ContentIdentifier.fromAssociationId(a.getId());
                    Content c = contentAO.getContent(cid, false);
                    if (c == null) {
                        log.error( "Content == null, associationId =" + a.getId());
                        throw new SystemException("Content == null, associationId =" + a.getId(), null);
                    }
                    int priv = Privilege.UPDATE_CONTENT;
                    if (c.getVersion() > 1 || c.getStatus() == ContentStatus.PUBLISHED) {
                        // Hvis siden er publisert eller versjon > 1 før ikke slettet uten godkjenningsrett
                        priv = Privilege.APPROVE_CONTENT;
                    }
                    if (securitySession.isAuthorized(c, priv)) {
                        associations.add(a);
                    }
                }
            }
        }

        List<Content> pagesToBeDeleted = AssociationAO.deleteAssociationsById(associations, deleteMultiple, securitySession.getUser().getId());

        // Hvis ikke brukeren har angitt at flere skal kunne slettes så blir de ikke slettet
        if (aDeleteHasHappened(deleteMultiple, pagesToBeDeleted)) {
            // Dette er innholdsobjekter som er slettet i sin helhet
            for (Content c : pagesToBeDeleted) {
                eventLog.log(securitySession, request, Event.DELETE_CONTENT, c.getTitle());
                contentNotifier.contentDeleted(new ContentEvent()
                        .setContent(c)
                        .setUser(securitySession.getUser()));
            }

            for (Association a : associations) {
                contentNotifier.associationDeleted(new ContentEvent()
                        .setAssociation(a)
                        .setUser(securitySession.getUser()));
            }

        }

        return pagesToBeDeleted;
    }

    private boolean aDeleteHasHappened(boolean deleteMultiple, List<Content> pagesToBeDeleted) {
        return pagesToBeDeleted.size() == 1 || deleteMultiple;
    }


    /**
     * Endrer en kopling i systemet.  F.eks når en bruker flytter et punkt i strukturen. Oppdaterer
     * alle underliggende koplinger.
     *
     * @param association - Kopling som skal oppdateres
     * @throws SystemException
     */
    public void modifyAssociation(Association association) throws SystemException {
        int aid = association.getAssociationId();
        if (aid != -1) {
            ContentIdentifier cid =  ContentIdentifier.fromAssociationId(aid);
            Content c = contentAO.getContent(cid, true);
            Association old = AssociationAO.getAssociationById(association.getId());
            if (c != null && old != null) {
                ContentIdentifier cidOldParent =  ContentIdentifier.fromAssociationId(old.getParentAssociationId());
                Content oldParent = contentAO.getContent(cidOldParent, true);

                ContentIdentifier cidNewParent =  ContentIdentifier.fromAssociationId(association.getParentAssociationId());
                Content newParent = contentAO.getContent(cidNewParent, true);

                String event = Event.MOVE_CONTENT;
                if (oldParent != null && newParent != null) {
                    event += ": " + oldParent.getName() + " -&gt; " + newParent.getName();
                }
                eventLog.log(securitySession, request, event, c.getName(), c);
            }
        }

        ContentEvent event = new ContentEvent()
                .setAssociation(association)
                .setUser(securitySession.getUser());
        contentNotifier.beforeAssociationUpdate(event);

        AssociationAO.modifyAssociation(association, true, true);

        contentNotifier.associationUpdated(event);
    }

    /**
     * Finner eventuelle duplikate alias innenfor område av strukturen
     *
     * @return - Liste med alias (String)
     * @throws SystemException
     */
    public List<String> findDuplicateAliases(Association parent) throws SystemException {
        return AssociationAO.findDuplicateAliases(parent);
    }


    /**
     * Henter en liste med innhold som er slettet av brukeren, slik at han kan angre på dette senere
     *
     * @return - Liste med DeletedItem
     * @throws SystemException
     */
    public List<DeletedItem> getDeletedItems() throws SystemException {
        return DeletedItemsAO.getDeletedItems(securitySession.getUser().getId());
    }

    /**
     * Restore a deleted item from deleted items
     * @param id from trashcan
     * @return id of restored item (not trashcan id)
     * @throws SystemException
     */
    public int restoreDeletedItem(int id) throws SystemException {
        int parentId = AssociationAO.restoreAssociations(id);
        return parentId;
    }

    /**
     * Henter et vedlegg fra databasen med angitt id. NB! Henter ikke data i objektet, må streames
     *
     * @param id - id til vedlegg som skal hentes
     * @return - Attachment objekt
     * @throws SystemException
     * @throws NotAuthorizedException - Brukeren har ikke rettighet til å lese vedlegg
     */
    public Attachment getAttachment(int id, int siteId) throws SystemException, NotAuthorizedException {
        Attachment attachment = AttachmentAO.getAttachment(id);
        if (attachment != null) {
            int contentId = attachment.getContentId();
            // Må hente ut tilhørende contentobject for å vite om bruker er autorisert og at ikke vedlegget er slettet
            if (contentId != -1) {
                ContentIdentifier cid =  ContentIdentifier.fromContentId(contentId);
                if (siteId != -1) {
                    cid.setSiteId(siteId);
                }
                Content c = getContent(cid);
                if (c == null) {
                    return null;
                }
            }
        }

        return attachment;
    }

    /**
     * Streamer et vedlegg fra databasen til en stream ved hjelp av en callback
     * @param id - Id til vedlegg som skal streames
     * @param ish - Callback for å streame data
     * @throws SystemException
     */
    public void streamAttachmentData(int id, InputStreamHandler ish) throws SystemException {
        AttachmentAO.streamAttachmentData(id, ish);
    }


    /**
     * Lagrer et vedlegg i basen
     * @param attachment - Vedlegg som skal lagres
     * @return - id of saved attachment
     * @throws SystemException
     * @throws SQLException
     * @throws NotAuthorizedException
     */
    public int setAttachment(Attachment attachment) throws SystemException, SQLException, NotAuthorizedException {
        if (!securitySession.isLoggedIn()) {
            throw new NotAuthorizedException("Not logged in");
        }

        if (attachment.getContentId() != -1) {
            ContentIdentifier cid =  ContentIdentifier.fromContentId(attachment.getContentId());
            Content content = getContent(cid);
            if (!securitySession.isAuthorized(content, Privilege.UPDATE_CONTENT)) {
                throw new NotAuthorizedException(securitySession.getUser().getId() + " not authorized to add attachment for content: " + content.getId());
            }
        }

        eventLog.log(securitySession, request, Event.SAVE_ATTACHMENT, attachment.getFilename());

        int id = AttachmentAO.setAttachment(attachment);
        attachment.setId(id);

        contentNotifier.attachmentUpdated(new ContentEvent()
                .setAttachment(attachment)
                .setUser(securitySession.getUser()));

        return attachment.getId();
    }


    /**
     * Sletter et vedlegg fra basen
     * @param id - id til vedlegg som skal slettes
     * @throws SystemException
     * @throws NotAuthorizedException
     */
    public void deleteAttachment(int id) throws SystemException, NotAuthorizedException {
        if (id == -1) {
            return;
        }
        String title = null;

        Attachment attachment = AttachmentAO.getAttachment(id);
        if (attachment == null) {
            return;
        }

        title = attachment.getFilename();

        if (attachment.getContentId() != -1) {
            ContentIdentifier cid =  ContentIdentifier.fromContentId(attachment.getContentId());
            Content content = getContent(cid);
            if (!securitySession.isAuthorized(content, Privilege.UPDATE_CONTENT)) {
                throw new NotAuthorizedException(securitySession.getUser().getId() + " authorized to delete attachment for content " + content.getId());
            }
        }

        AttachmentAO.deleteAttachment(id);
        eventLog.log(securitySession, request, Event.DELETE_ATTACHMENT, title);
    }


    /**
     * Henter en liste med alle vedlegg til et innholdsobjekt
     * @param id - Id til innholdsobjekt
     * @return - liste med Attachment objekt
     * @throws SystemException
     */
    public List<Attachment> getAttachmentList(ContentIdentifier id) throws SystemException {
        return AttachmentAO.getAttachmentList(id);
    }


    /**
     * Henter et objekt fra XML cachen i systemet.  XML cachen brukes for å lagre XML dokumenter lokalt
     * istedet for å hente dem med HTTP for hver visning.  Kan brukes for f.eks nyheter.
     *
     * @param id - unik identifikator i basen
     * @return
     * @throws SystemException
     */
    public XMLCacheEntry getXMLFromCache(String id) throws SystemException {
        return xmlCache.getXMLFromCache(id);
    }

    /**
     * Henter en liste med innslag fra XML-cachen.  Brukes for å se hvilke objekter som ligger der og nør
     * de er oppdatert.
     *
     * @return
     * @throws SystemException
     */
    public List getXMLCacheSummary() throws SystemException {
        return xmlCache.getSummary();
    }
}
