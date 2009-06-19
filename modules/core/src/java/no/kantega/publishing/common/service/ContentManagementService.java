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

import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.data.enums.*;
import no.kantega.publishing.common.ao.*;
import no.kantega.publishing.common.exception.ObjectInUseException;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.common.exception.InvalidTemplateReferenceException;
import no.kantega.publishing.common.exception.ObjectLockedException;
import no.kantega.publishing.common.service.impl.*;
import no.kantega.publishing.common.service.lock.LockManager;
import no.kantega.publishing.common.service.lock.ContentLock;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.cache.*;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.publishing.common.util.templates.TemplateHelper;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.InvalidFileException;
import no.kantega.commons.util.HttpHelper;
import no.kantega.commons.log.Log;

import no.kantega.publishing.security.data.enums.Privilege;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.event.ContentListenerUtil;
import no.kantega.publishing.admin.content.util.EditContentHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Date;
import java.util.ArrayList;
import java.sql.SQLException;

/**
 *
 */
public class ContentManagementService {
    private static final String SOURCE = "aksess.ContentManagementService";

    HttpServletRequest request = null;
    SecuritySession securitySession = null;

    public ContentManagementService(HttpServletRequest request) throws SystemException {
        this.request = request;
        this.securitySession = SecuritySession.getInstance(request);
    }

    public ContentManagementService(SecuritySession securitySession) throws SystemException {
        this.securitySession = securitySession;
    }

    /**
     * Hent ut brukerens sikkerhetssesjon
     * @return - sikkerhetssesjon
     */
    public SecuritySession getSecuritySession() {
        return securitySession;
    }


    /**
     * Sjekk ut innholdsobjekt fra basen
     * @param id - ContentIdentifier til innholdsobjekt
     * @return Innholdsobjekt
     * @throws SystemException - System error
     * @throws NotAuthorizedException - dersom bruker ikke har tilgang
     * @throws InvalidFileException - mal ikke finnes
     * @throws InvalidTemplateException - invalid template
     * @throws ObjectLockedException - Someone else is editing object
     */
    public Content checkOutContent(ContentIdentifier id) throws SystemException, NotAuthorizedException, InvalidFileException, InvalidTemplateException, ObjectLockedException {
        ContentLock lock = LockManager.peekAtLock(id.getContentId());
        if(lock != null && !lock.getOwner().equals(securitySession.getUser().getId())) {
            throw new ObjectLockedException(securitySession.getUser().getId(), SOURCE);
        }

        Content c = ContentAO.checkOutContent(id);
        if (!securitySession.isAuthorized(c, Privilege.UPDATE_CONTENT)) {
            throw new NotAuthorizedException("checkOutContent", SOURCE);
        }

        if (request != null) {
            Log.debug(SOURCE, "Locking contentid: " + c.getId() + " for user: " + securitySession.getUser().getId() + " with IP: " + request.getRemoteAddr(), null, null);
        }
        LockManager.lockContent(securitySession.getUser().getId(), c.getId());

        EditContentHelper.updateAttributesFromTemplate(c, securitySession);

        return c;
    }


    public Content createNewContent(ContentCreateParameters parameters) throws SystemException, InvalidFileException, InvalidTemplateException, NotAuthorizedException {
        Content content = EditContentHelper.createContent(securitySession, parameters);

        // Last attributter fra XML fil
        EditContentHelper.updateAttributesFromTemplate(content, securitySession);

        // Kjør plugins        
        ContentListenerUtil.getContentNotifier().contentCreated(content);

        return content;
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

        Content c = ContentAO.getContent(id, adminMode);
        if (c != null) {
            assertCanView(c, adminMode, securitySession);
        }
        if (c != null && logView && !adminMode && Aksess.isTrafficLogEnabled() && request != null) {
            // Log event
            TrafficLogger.log(c, request);
        }
        return c;
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
        if (!securitySession.isAuthorized(c, Privilege.VIEW_CONTENT)) {
            throw new NotAuthorizedException("User not authorized to view: " + c.getId(), SOURCE);
        }

        if(c.getStatus() == ContentStatus.HEARING && !HearingAO.isHearingInstance(c.getVersionId(), securitySession.getUser().getId()) && !adminMode) {
            throw new NotAuthorizedException("User is neigther in admin mode or hearing instance", SOURCE);
        }

        if (c.getStatus() == ContentStatus.DRAFT && !adminMode) {
            throw new NotAuthorizedException("Object is draft, must view in admin mode: " + c.getId(), SOURCE);
        }
    }


    /**
     * Henter ut alle innholdsversjoner av en side
     * @param id - ContentIdentifier til innholdsobjekt
     * @return - liste med innholdsobjekter
     * @throws SystemException
     */
    public List getAllContentVersions(ContentIdentifier id) throws SystemException {
        return ContentAO.getAllContentVersions(id);
    }


    /**
     * Lagrer et innholdsobjekt med en gitt status. Oppretter i gitte tilfeller en ny versjon.
     * Legger til objektet i søkeindeks dersom status = Publish
     * @param content - Endret objekt
     * @param status - Status som skal settes på nytt objekt
     * @return
     * @throws SystemException
     * @throws NotAuthorizedException
     */
    public Content checkInContent(Content content, int status) throws SystemException, NotAuthorizedException {
        LockManager.releaseLock(content.getId());

        if (!securitySession.isAuthorized(content, Privilege.UPDATE_CONTENT)) {
            throw new NotAuthorizedException("checkInContent", SOURCE);
        }

        content.setModifiedBy(securitySession.getUser().getId());

        // Sjekk om brukeren har rett til å publisere, hvis ikke sett ventestatus
        if (status == ContentStatus.PUBLISHED && !securitySession.isAuthorized(content, Privilege.APPROVE_CONTENT)) {
            status = ContentStatus.WAITING;
            content.setApprovedBy("");
        } else {
            content.setApprovedBy(securitySession.getUser().getId());
        }

        if (content.getPublishDate() != null && content.getPublishDate().getTime() > new Date().getTime()) {
            content.setVisibilityStatus(ContentVisibilityStatus.WAITING);
        } else if (content.getExpireDate() != null && content.getExpireDate().getTime() < new Date().getTime()) {
            if (content.getExpireAction () == ExpireAction.ARCHIVE) {
                content.setVisibilityStatus(ContentVisibilityStatus.ARCHIVED);
            } else {
                content.setVisibilityStatus(ContentVisibilityStatus.EXPIRED);
            }
        } else {
            content.setVisibilityStatus(ContentVisibilityStatus.ACTIVE);
        }

        ContentListenerUtil.getContentNotifier().beforeContentSave(content);

        Content c = ContentAO.checkInContent(content, status);

        ContentListenerUtil.getContentNotifier().contentSaved(c);

        if (Aksess.isEventLogEnabled()) {
            String event;
            switch (c.getStatus()) {
                case ContentStatus.DRAFT:
                    event = Event.SAVE_DRAFT;
                    break;
                case ContentStatus.WAITING:
                    event = Event.SEND_FOR_APPROVAL;
                    break;
                default:
                    event = Event.PUBLISH_CONTENT;
            }
            EventLog.log(securitySession, request, event, c.getTitle(), c);
        }

        // Reload cache
        ContentIdentifierCache.reloadCache();

        return c;
    }


    /**
     * Tar et innholdsobjektet og en plassering og lagrer en kopi av objektet på den nye plasseringen
     * @param sourceContent - Endret objekt
     * @param target -
     * @param category -
     * @return
     * @throws SystemException
     * @throws NotAuthorizedException
     */
    public Content copyContent(Content sourceContent, Association target, AssociationCategory category) throws SystemException, NotAuthorizedException {


        ContentIdentifier parentCid = new ContentIdentifier();
        parentCid.setAssociationId(target.getAssociationId());

        Content destParent = ContentAO.getContent(parentCid, true);

        // Modifiserer sourcecontent, nullstill id'er
        sourceContent.setId(-1);
        sourceContent.setVersionId(-1);
        sourceContent.setVersion(1);

        sourceContent.setAlias("");

        // Ta egenskaper fra ny parent
        sourceContent.setSecurityId(destParent.getSecurityId());
        sourceContent.setOwner(destParent.getOwner());
        sourceContent.setOwnerPerson(destParent.getOwnerPerson());
        sourceContent.setLanguage(destParent.getLanguage());

        DisplayTemplate displayTemplate = DisplayTemplateCache.getTemplateById(sourceContent.getDisplayTemplateId());
        if (displayTemplate.isNewGroup()) {
            // Arver egenskaper fra sider over.  GroupId brukes til å lage ting som skal være spesielt for en struktur, f.eks meny
            sourceContent.setGroupId(destParent.getGroupId());
        }

        // Kjør plugins
        ContentListenerUtil.getContentNotifier().contentCreated(sourceContent);

        // Legg til kopling til parent
        List associations = new ArrayList();

        Association association = new Association();

        association.setParentAssociationId(target.getId());
        association.setCategory(category);
        association.setSiteId(target.getSiteId());

        associations.add(association);
        sourceContent.setAssociations(associations);

        return checkInContent(sourceContent, ContentStatus.PUBLISHED);
    }

    /**
     * Setter ny status på et objekt, f.eks ved godkjenning av en side.
     * Legger til / fjerner objektet til/fra søkeindeks
     * @param cid - ContentIdenfier for nytt objekt
     * @param newStatus - Ny status
     * @param note - melding
     * @return
     * @throws NotAuthorizedException
     * @throws SystemException
     */
    public Content setContentStatus(ContentIdentifier cid, int newStatus, String note) throws NotAuthorizedException, SystemException {
        Content c = getContent(cid);
        if (!securitySession.isAuthorized(c, Privilege.APPROVE_CONTENT)) {
            throw new NotAuthorizedException("setContentStatus", SOURCE);
        }

        if (note != null && note.length() > 0) {
            Note n = new Note();
            n.setAuthor(securitySession.getUser().getName());
            n.setDate(new Date());
            n.setText(note);
            n.setContentId(cid.getContentId());
            NotesAO.addNote(n);
            int count = NotesAO.getNotesByContentId(cid.getContentId()).length;
            ContentAO.setNumberOfNotes(cid.getContentId(), count);
        }

        String event = Event.APPROVED;
        if (newStatus == ContentStatus.REJECTED) {
            event = Event.REJECTED;
        }

        EventLog.log(securitySession, request, event, c.getTitle(), c);

        return ContentAO.setContentStatus(cid, newStatus, securitySession.getUser().getId());
    }


    /**
     * TODO: Slett denne senere
     * Sletter et innholdsobjekt fra basen. NB! Alle versjoner slettes
     * @param id - Innholdsid
     * @return
     * @throws SystemException
     * @throws ObjectInUseException
     * @throws NotAuthorizedException
     */
    public ContentIdentifier deleteContent(ContentIdentifier id) throws SystemException, ObjectInUseException, NotAuthorizedException {
        ContentIdentifier cid = null;
        String title = null;

        if (id != null) {
            Content c = getContent(id);
            if (c != null) {
                int priv = Privilege.UPDATE_CONTENT;
                if (c.getVersion() > 1 || c.getStatus() == ContentStatus.PUBLISHED) {
                    // Hvis siden er publisert eller versjon > 1 får ikke slettet uten godkjenningsrett
                    priv = Privilege.APPROVE_CONTENT;
                }
                if (!securitySession.isAuthorized(c, priv)) {
                    throw new NotAuthorizedException("deleteContent", SOURCE);
                }
                if (Aksess.isEventLogEnabled()) {
                    title = c.getTitle();
                }
                Boolean canDelete = new Boolean(true);
                ContentListenerUtil.getContentNotifier().beforeContentDelete(c, canDelete);
                if (!canDelete.booleanValue()) {
                    throw new ObjectInUseException(SOURCE, "I bruk");
                }
                
                cid = ContentAO.deleteContent(id);
                if (title != null) {
                    EventLog.log(securitySession, request, Event.DELETE_CONTENT, title);
                }

                ContentListenerUtil.getContentNotifier().contentDeleted(c);


            }
        }
        return cid;
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
                throw new NotAuthorizedException("deleteContentVersion", SOURCE);
            }
            if (Aksess.isEventLogEnabled()) {
                if (c != null) {
                    title = c.getTitle();
                }
            }
            ContentAO.deleteContentVersion(id, false);
            if (title != null) {
                EventLog.log(securitySession, request, Event.DELETE_CONTENT_VERSION, title);
            }
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
    public List getContentList(ContentQuery query, int maxElements, SortOrder sort, boolean getAttributes, boolean getTopics) throws SystemException {
        List list = ContentAO.getContentList(query, maxElements, sort, getAttributes, getTopics);

        List approved = new ArrayList();
        // Legg kun til elementer som brukeren har tilgang til
        for (int i = 0; i < list.size(); i++) {
            Content c = (Content)list.get(i);
            if (securitySession.isAuthorized(c, Privilege.VIEW_CONTENT)) {
                approved.add(c);
            }
        }

        return approved;
    }


    /**
     * Henter en liste med innholdsobjekter fra basen med innholdsattributter
     * @param query - Søk som angir hva som skal hentes
     * @param maxElements - Max antall elementer som skal hentes, -1 for alle
     * @param sort - Sorteringsrekkefølge
     * @return
     * @throws SystemException
     */
    public List getContentList(ContentQuery query, int maxElements, SortOrder sort) throws SystemException {
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
    public List getContentSummaryList(ContentQuery query, int maxElements, SortOrder sort) throws SystemException {
        return getContentList(query, maxElements, sort, false, false);
    }


    /**
     * Hent innhold som er mitt (dvs min arbeidsliste)
     * @return Liste med innholdsobjekter
     * @throws SystemException
     */
    public List getMyContentList() throws SystemException {
        if (securitySession != null && securitySession.getUser() != null) {
            return ContentAO.getMyContentList(securitySession.getUser());
        }

        return null;
    }


    /**
     * Henter alle innholdsobjekter som kan godkjennes av deg
     * @return Liste med innholdsobjekter
     * @throws SystemException
     */
    public List getContentListForApproval() throws SystemException {
        if (securitySession == null) {
            return null;
        }

        List list = ContentAO.getContentListForApproval();
        List approved = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            // Legg kun til elementer som brukeren har tilgang til
            Content c = (Content)list.get(i);
            if (securitySession.isApprover(c)) {
                approved.add(c);
            }
        }
        return approved;
    }


    /**
     * Henter parent cid
     * @param cid
     * @return
     * @throws SystemException
     */
    public ContentIdentifier getParent(ContentIdentifier cid) throws SystemException {
        if(cid == null) {
            return null;
        }
        return ContentAO.getParent(cid);
    }


    /**
     * Henter en liste over antall endringer gjort av brukere
     * @return Liste med innholdsobjekter
     * @throws SystemException
     */
    public List getNoChangesPerUser(int months) throws SystemException {
        return ContentAO.getNoChangesPerUser(months);
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
        return SiteMapWorker.getSiteMap(siteId, depth, language, category, rootId, currentId);
    }

    /**
     * Hent meny
     * @param siteId - Site det skal hentes for
     * @param idList - Liste med åpne element i menyen, henter alle med parent som ligger i lista
     * @param language - Språk det skal hentes for
     * @return
     * @throws SystemException
     */
    public SiteMapEntry getNavigatorMenu(int siteId, int[] idList, int language, String sort) throws SystemException {
        return SiteMapWorker.getPartialSiteMap(siteId, idList, language, true, sort);
    }


    /**
     * Hent meny
     * @param content - Innholdsobjekt
     * @param associationCategory -
     * @param useLocalMenus -
     * @return
     * @throws SystemException
     */
    public SiteMapEntry getMenu(Content content, String associationCategory, boolean useLocalMenus) throws SystemException {
        AssociationCategory category = null;
        if (associationCategory != null) {
            category = AssociationCategoryCache.getAssociationCategoryByPublicId(associationCategory);
        }

        return SiteMapWorker.getPartialSiteMap(content, category, useLocalMenus, false);
    }


    /**
     * Hent liste over alle dokumenttyper
     * @return Liste av DocumentType
     * @throws SystemException
     */
    public List getDocumentTypes() throws SystemException {
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
    public List getPathByAssociation(Association association) throws SystemException {
        return PathWorker.getPathByAssociation(association);
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
     * Utfører et SQL søk mot basen, brukes for internt søk i applikasjonen
     * @param phrase - søkeord
     * @return
     * @throws SystemException
     */
    public List search(String phrase) throws SystemException {
        return SearchAO.search(phrase);
    }


    /**
     * Søk i eventlogg
     * @param from - Dato fra
     * @param end - Dato til
     * @param userId - Brukerid
     * @param subjectName - Navn på objekt i loggen (navn på side f.eks)
     * @param eventName - Hendelse
     * @return
     * @throws SystemException
     */
    public List searchEventLog(Date from, Date end, String userId, String subjectName, String eventName) throws SystemException {
        return EventLogAO.search(from, end, userId, subjectName, eventName);
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
     * Setter rekkefølge på koplinger for sortering i menyer
     * @param associations
     * @throws SystemException
     */
    public void setAssociationsPriority(List associations) throws SystemException {
        AssociationAO.setAssociationsPriority(associations);
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
     * @throws SystemException
     */
    public void copyAssociations(Association source, Association target, AssociationCategory category, boolean copyChildren) throws SystemException {
        AssociationAO.copyAssociations(source, target, category, copyChildren);
    }


    /**
     * Legger til en kopling i basen
     *
     * @param association  - Kopling som skal legges til
     * @throws SystemException
     */
    public void addAssociation(Association association) throws SystemException {
        AssociationAO.addAssociation(association);
    }


    /**
     * Sletter de angitte koplinger fra basen, dvs markerer dem som slettet. Legger innslag i deleteditems
     * slik at brukeren kan gjenopprette dem senere.
     *
     * Dersom deleteMultiple = false og det finnes underobjekter vil ikke sletting bli utført, men
     * man får en liste med hva som blir slettet, som kan vises for brukeren
     *
     * @param associationIds - Koplinger som skal slettes
     * @param deleteMultiple - Må være satt til true for å utføre sletting hvis det finnes underobjekter
     * @return
     * @throws SystemException
     */
    public List deleteAssociationsById(int[] associationIds, boolean deleteMultiple) throws SystemException {
        List associations = new ArrayList();
        List deletedItems = new ArrayList();

        for (int i = 0; i < associationIds.length; i++) {
            Association a = AssociationAO.getAssociationById(associationIds[i]);
            if (a != null) {
                if (a.getAssociationtype() == AssociationType.SHORTCUT) {
                    // Sjekk tilgangen til snarvei
                    if (securitySession.isAuthorized(a, Privilege.APPROVE_CONTENT)) {
                        associations.add(new Integer(a.getId()));
                    }
                } else {
                    // Sjekk tilgangen til innholdsobjektet den peker på
                    ContentIdentifier cid = new ContentIdentifier();
                    cid.setAssociationId(a.getId());
                    Content c = ContentAO.getContent(cid, false);
                    if (c == null) {
                        Log.error(SOURCE, "Content == null, associationId =" + a.getId(), null, null);
                        throw new SystemException("Content == null, associationId =" + a.getId(), SOURCE, null);
                    }
                    int priv = Privilege.UPDATE_CONTENT;
                    if (c.getVersion() > 1 || c.getStatus() == ContentStatus.PUBLISHED) {
                        // Hvis siden er publisert eller versjon > 1 får ikke slettet uten godkjenningsrett
                        priv = Privilege.APPROVE_CONTENT;
                    }
                    if (securitySession.isAuthorized(c, priv)) {
                        deletedItems.add(c);
                        associations.add(new Integer(a.getId()));
                    }
                }
            }
        }

        List pagesToBeDeleted = AssociationAO.deleteAssociationsById(associations, deleteMultiple, securitySession.getUser().getId());

        // Hvis ikke brukeren har angitt at flere skal kunne slettes så blir de ikke slettet
        if (pagesToBeDeleted.size() == 1 || deleteMultiple) {
            // Dette er innholdsobjekter som er slettet i sin helhet
            for (int i = 0; i < pagesToBeDeleted.size(); i++) {
                Content c =  (Content)pagesToBeDeleted.get(i);
                EventLog.log(securitySession, request, Event.DELETE_CONTENT, c.getTitle());
            }
        }

        return pagesToBeDeleted;
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
            ContentIdentifier cid = new ContentIdentifier();
            cid.setAssociationId(aid);
            Content c = ContentAO.getContent(cid, true);
            Association old = AssociationAO.getAssociationById(association.getId());
            if (c != null && old != null) {
                ContentIdentifier cidOldParent = new ContentIdentifier();
                cidOldParent.setAssociationId(old.getParentAssociationId());
                Content oldParent = ContentAO.getContent(cidOldParent, true);

                ContentIdentifier cidNewParent = new ContentIdentifier();
                cidNewParent.setAssociationId(association.getParentAssociationId());
                Content newParent = ContentAO.getContent(cidNewParent, true);

                String event = Event.MOVE_CONTENT;
                if (oldParent != null && newParent != null) {
                    event += ": " + oldParent.getName() + " -&gt; " + newParent.getName();
                }
                EventLog.log(securitySession, request, event, c.getName(), c);
            }
        }

        ContentListenerUtil.getContentNotifier().beforeAssociationUpdate(association);

        AssociationAO.modifyAssociation(association, true, true);

        ContentListenerUtil.getContentNotifier().associationUpdated(association);
    }

    /**
     * Finner eventuelle duplikate alias innenfor område av strukturen
     *
     * @return - Liste med alias (String)
     * @throws SystemException
     */   
    public List findDuplicateAliases(Association parent) throws SystemException {
        return AssociationAO.findDuplicateAliases(parent);
    }


    /**
     * Henter en liste med innhold som er slettet av brukeren, slik at han kan angre på dette senere
     *
     * @return - Liste med DeletedItem
     * @throws SystemException
     */
    public List getDeletedItems() throws SystemException {
        return DeletedItemsAO.getDeletedItems(securitySession.getUser().getId());
    }

    /**
     * @param id - id til objekt som skal restores
     *
     * @throws SystemException
     */
    public void restoreDeletedItem(int id) throws SystemException {
        AssociationAO.restoreAssociations(id);
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
                ContentIdentifier cid = new ContentIdentifier();
                cid.setContentId(contentId);
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
     * @return
     * @throws SystemException
     * @throws SQLException
     */
    public int setAttachment(Attachment attachment) throws SystemException, SQLException {
        if (Aksess.isEventLogEnabled()) {
            EventLog.log(securitySession, request, Event.SAVE_ATTACHMENT, attachment.getFilename());
        }

        int id = AttachmentAO.setAttachment(attachment);
        attachment.setId(id);

        ContentListenerUtil.getContentNotifier().attachmentUpdated(attachment);

        return attachment.getId();
    }


    /**
     * Sletter et vedlegg fra basen
     * @param id - id til vedlegg som skal slettes
     * @throws SystemException
     */
    public void deleteAttachment(int id) throws SystemException {
        String title = null;
        if (id != -1 && Aksess.isEventLogEnabled()) {
            Attachment a = AttachmentAO.getAttachment(id);
            if (a != null) {
                title = a.getFilename();
            }
        }
        AttachmentAO.deleteAttachment(id);
        if (title != null) {
            EventLog.log(securitySession, request, Event.DELETE_ATTACHMENT, title);
        }
    }


    /**
     * Henter en liste med alle vedlegg til et innholdsobjekt
     * @param id - Id til innholdsobjekt
     * @return - liste med Attachment objekt
     * @throws SystemException
     */
    public List getAttachmentList(ContentIdentifier id) throws SystemException {
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
        return XMLCacheAO.getXMLFromCache(id);
    }

    /**
     * Henter en liste med innslag fra XML-cachen.  Brukes for å se hvilke objekter som ligger der og når
     * de er oppdatert.
     *
     * @return
     * @throws SystemException
     */
    public List getXMLCacheSummary() throws SystemException {
        return XMLCacheAO.getSummary();
    }
}
