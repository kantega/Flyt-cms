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

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.multimedia.event.MultimediaEvent;
import no.kantega.publishing.api.multimedia.event.MultimediaEventListener;
import no.kantega.publishing.api.path.PathEntry;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.MultimediaAO;
import no.kantega.publishing.common.ao.MultimediaDao;
import no.kantega.publishing.common.ao.MultimediaUsageDao;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.MultimediaMapEntry;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.exception.ObjectInUseException;
import no.kantega.publishing.common.service.impl.MultimediaMapWorker;
import no.kantega.publishing.common.service.impl.PathWorker;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.publishing.content.api.ContentAO;
import no.kantega.publishing.eventlog.Event;
import no.kantega.publishing.eventlog.EventLog;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import no.kantega.publishing.spring.RootContext;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class MultimediaService {

    private MultimediaUsageDao multimediaUsageDao;
    private MultimediaDao multimediaDao;

    private HttpServletRequest request = null;
    private SecuritySession securitySession = null;
    private EventLog eventLog;
    private ContentAO contentAO;
    private MultimediaEventListener multimediaListenerNotifier;

    public MultimediaService() {
        ApplicationContext ctx = RootContext.getInstance();
        multimediaUsageDao = ctx.getBean(MultimediaUsageDao.class);
        multimediaDao = ctx.getBean(MultimediaDao.class);
        eventLog = ctx.getBean(EventLog.class);
        contentAO = ctx.getBean(ContentAO.class);
        multimediaListenerNotifier = ctx.getBean("multimediaListenerNotifier", MultimediaEventListener.class);
    }

    public MultimediaService(HttpServletRequest request) throws SystemException {
        this();
        this.request = request;
        this.securitySession = SecuritySession.getInstance(request);
    }

    public MultimediaService(SecuritySession securitySession) {
        this();
        this.securitySession = securitySession;
    }

    public Multimedia getMultimediaCheckAuthorization(int id) throws SystemException, NotAuthorizedException {
        Multimedia multimedia = multimediaDao.getMultimedia(id);

        if (multimedia != null && multimedia.getContentId() > 0) {
            ContentIdentifier cid =  ContentIdentifier.fromContentId(multimedia.getContentId());
            Content content = contentAO.getContent(cid, false);
            if (!securitySession.isAuthorized(content, Privilege.VIEW_CONTENT)) {
                throw new NotAuthorizedException("Not authorized for id:" + id);
            }
        } else if (multimedia != null && !securitySession.isAuthorized(multimedia, Privilege.VIEW_CONTENT)) {
             throw new NotAuthorizedException("Not authorized for id:" + id);
        }

        return multimedia;
    }

    public Multimedia getMultimedia(int id) throws SystemException {
        return multimediaDao.getMultimedia(id);
    }


    public List<PathEntry> getMultimediaPath(Multimedia mm) throws SystemException {
        return PathWorker.getMultimediaPath(mm);
    }

    public void streamMultimediaData(int id, InputStreamHandler ish) throws SystemException {
        multimediaDao.streamMultimediaData(id, ish);
    }

    public List<Multimedia> getMultimediaList(int parentId) throws SystemException {
        List<Multimedia> list = multimediaDao.getMultimediaList(parentId);

        List<Multimedia> approved = new ArrayList<>();
        // Vis alle bilder + kun de mapper som brukeren har tilgang til
        for (Multimedia m : list) {
            if (m.getType() != MultimediaType.FOLDER || securitySession.isAuthorized(m, Privilege.VIEW_CONTENT)) {
                approved.add(m);
            }
        }

        return approved;
    }

    /**
     * Saves multimediaobject in database.
     *
     * @param multimedia - Multimedia object
     * @return
     * @throws SystemException
     */
    public int setMultimedia(Multimedia multimedia) throws SystemException {
        multimediaListenerNotifier.beforeSetMultimedia(new MultimediaEvent(multimedia));
        if (multimedia.getType() == MultimediaType.FOLDER || multimedia.getData() != null) {
            // For images / media files is updated is only set if a new file is uploaded
            multimedia.setModifiedBy(securitySession.getUser().getId());
        }

        int id = MultimediaAO.setMultimedia(multimedia);
        multimedia.setId(id);
        if (Aksess.isEventLogEnabled()) {
            if (multimedia.getType() == MultimediaType.FOLDER) {
                eventLog.log(securitySession, request, Event.SAVE_MULTIMEDIA, multimedia.getName());
            } else {
                eventLog.log(securitySession, request, Event.SAVE_MULTIMEDIA, multimedia.getName(), multimedia);
            }
        }
        multimediaListenerNotifier.afterSetMultimedia(new MultimediaEvent(multimedia));
        return id;
    }


    public void moveMultimedia(int mmId, int newParentId) throws SystemException, NotAuthorizedException {
        Multimedia mm = getMultimedia(mmId);
        multimediaListenerNotifier.beforeMoveMultimedia(new MultimediaEvent(mm));
        Multimedia newParent;
        if (newParentId > 0) {
            newParent = getMultimedia(newParentId);
        } else {
            // Rot-katalog finnes ikke som innslag
            newParent = new Multimedia();
            newParent.setId(0);
            newParent.setSecurityId(0);
        }
        if (!securitySession.isAuthorized(newParent, Privilege.UPDATE_CONTENT) || (!securitySession.isAuthorized(mm, Privilege.UPDATE_CONTENT))) {
            throw new NotAuthorizedException("Kan ikke flytte multimedia");
        }

        multimediaDao.moveMultimedia(mmId, newParentId);
        multimediaListenerNotifier.afterMoveMultimedia(new MultimediaEvent(getMultimedia(mmId)));
    }

    /**
     * Delete a multimedia folder with contents.
     *
     * @param id - id of object to be deleted
     * @throws SystemException - Thrown if there is an error during logging to the eventlog.
     * @throws NotAuthorizedException - Thrown if the user is not authorized to delete folder and contained objects.
     */
    public void deleteMultimediaFolder(int id) throws SystemException, NotAuthorizedException {
        multimediaListenerNotifier.beforeDeleteMultimedia(new MultimediaEvent(getMultimedia(id)));

        List<Multimedia> children = getMultimediaList(id);
        for (Multimedia child : children) {
            if (child.getType() == MultimediaType.MEDIA) {
                try {
                    deleteMultimedia(child.getId());
                } catch (ObjectInUseException e) {
                    // Should never occur, because MultimediaType is MEDIA. Can only occur with MultimediaType FOLDER.
                    throw new SystemException("Error deleting multimediafolder with id " + id + ".", e);
                }
            } else {
                deleteMultimediaFolder(child.getId());
            }
        }
        try {

            MultimediaEvent event = new MultimediaEvent(getMultimedia(id));
            deleteMultimedia(id);
            multimediaListenerNotifier.afterDeleteMultimedia(event);
        } catch (ObjectInUseException e) {
            throw new SystemException("Error deleting multimediafolder with id "+id+".", e);
        }
    }

    public void deleteMultimedia(int id) throws SystemException, ObjectInUseException, NotAuthorizedException {
        String title = null;
        if (id != -1 && Aksess.isEventLogEnabled()) {
            Multimedia t = getMultimedia(id);
            multimediaListenerNotifier.beforeDeleteMultimedia(new MultimediaEvent(t));
            if (t != null) {
                title = t.getName();
            }
            if (!securitySession.isAuthorized(t, Privilege.APPROVE_CONTENT)) {
                throw new NotAuthorizedException("Not authorized to delete multimedia object with id "+id+".");
            }
        }
        Multimedia multimedia = getMultimedia(id);
        multimediaDao.deleteMultimedia(id);
        multimediaListenerNotifier.afterDeleteMultimedia(new MultimediaEvent(multimedia));
        if (title != null) {
            eventLog.log(securitySession, request, Event.DELETE_MULTIMEDIA, title);
        }
    }

    /**
     * Performs a search for multimedia matching the given phrase, published on the given site and published in content
     * which is a child of the content given by parentId.
     *
     * @param phrase   the text to search for
     * @param site     the site to limit the search by, or -1 for global.
     * @param parentId the root of the subtree of contents to limit the search by, or -1 for all
     * @return a list of Multimedia-objects matching the given criteria
     * @throws SystemException if a SystemException is thrown by the underlying AO
     */
    public List<Multimedia> searchMultimedia(String phrase, int site, int parentId) throws SystemException {
        List<Multimedia> list = multimediaDao.searchMultimedia(phrase, site, parentId);

        List<Multimedia> approved = new ArrayList<>();
        // Legg kun til bilder og mapper som brukeren har tilgang til
        for (Multimedia m : list) {
            if (securitySession.isAuthorized(m, Privilege.VIEW_CONTENT)) {
                approved.add(m);
            }
        }

        return approved;
    }

    public List<Multimedia> searchMultimedia(String phrase) throws SystemException {
        return searchMultimedia(phrase, -1, -1);
    }

    public MultimediaMapEntry getPartialMultimediaMap(int[] idList, boolean getOnlyFolders) throws SystemException {
        return MultimediaMapWorker.getPartialSiteMap(idList, getOnlyFolders);
    }

    public List<Content> getUsages(int multimediaId) throws SystemException {
        List<Content> pages = new ArrayList<>();

        List<Integer> contentIds = multimediaUsageDao.getUsagesForMultimediaId(multimediaId);
        for (Integer contentId : contentIds) {
            ContentIdentifier cid =  ContentIdentifier.fromContentId(contentId);
            Content content = contentAO.getContent(cid, true);
            if (content != null) {
                pages.add(content);
            }
        }

        return pages;
    }

    /**
     * Retrieves an image associated with the user's profile.
     *
     * @param userId
     * @return
     */
    public Multimedia getProfileImageForUser(String userId) {
        return multimediaDao.getProfileImageForUser(userId);
    }

    /**
     * Saves or updates a user's profile image.
     *
     * @param mm
     */
    public void setProfileImageForUser(Multimedia mm) {
        if (mm == null || mm.getProfileImageUserId() == null || mm.getProfileImageUserId().trim().equals("")) {
            return;
        }
        //Check if the user already has an image.
        Multimedia profileImage = multimediaDao.getProfileImageForUser(mm.getProfileImageUserId());
        if (profileImage != null) {
            mm.setId(profileImage.getId());
        }
        MultimediaAO.setMultimedia(mm);
    }

}
