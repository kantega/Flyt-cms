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
import no.kantega.publishing.common.util.ImageHelper;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.ao.MultimediaAO;
import no.kantega.publishing.common.ao.MultimediaUsageAO;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.MultimediaMapEntry;
import no.kantega.publishing.common.data.enums.Event;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.exception.ObjectInUseException;
import no.kantega.publishing.common.service.impl.EventLog;
import no.kantega.publishing.common.service.impl.MultimediaMapWorker;
import no.kantega.publishing.common.service.impl.PathWorker;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MultimediaService {
    private static final String SOURCE = "aksess.ContentManagementService";

    HttpServletRequest request = null;
    SecuritySession securitySession = null;

    public MultimediaService(HttpServletRequest request) throws SystemException {
        this.request = request;
        this.securitySession = SecuritySession.getInstance(request);
    }

    public Multimedia getMultimedia(int id) throws SystemException {
        return MultimediaAO.getMultimedia(id);
    }


    public List getMultimediaPath(Multimedia mm) throws SystemException {
        return PathWorker.getMultimediaPath(mm);
    }

    public void streamMultimediaData(int id, InputStreamHandler ish) throws SystemException {
        MultimediaAO.streamMultimediaData(id, ish);
    }

    public List getMultimediaList(int parentId) throws SystemException {
        List list = MultimediaAO.getMultimediaList(parentId);

        List approved = new ArrayList();
        // Vis alle bilder + kun de mapper som brukeren har tilgang til
        for (int i = 0; i < list.size(); i++) {
            Multimedia m = (Multimedia)list.get(i);
            if (m.getType() != MultimediaType.FOLDER ||securitySession.isAuthorized(m, Privilege.VIEW_CONTENT)) {
                approved.add(m);
            }
        }

        return approved;
    }

    /**
     * Saves multimediaobject in database.
     * @param multimedia - Multimedia object
     * @return
     * @throws SystemException
     */
    public int setMultimedia(Multimedia multimedia) throws SystemException {
        return setMultimedia(multimedia, true);
    }

    
    /**
     * Saves multimediaobject in database.
     * @param multimedia - Multimedia object
     * @param preserveImageSize - Resizes image if false and dimensions are greater than max
     *
     * @return
     * @throws SystemException
     */
    public int setMultimedia(Multimedia multimedia, boolean preserveImageSize) throws SystemException {
        if (multimedia.getType() == MultimediaType.FOLDER || multimedia.getData() != null) {
            // For images / media files is updated is only set if a new file is uploaded
            multimedia.setModifiedBy(securitySession.getUser().getId());
        }

        if ((!preserveImageSize) && multimedia.getType() == MultimediaType.MEDIA && multimedia.getMimeType().getType().indexOf("image") != -1 && (Aksess.getMaxMediaWidth() > 0 || Aksess.getMaxMediaHeight() > 0)) {
            if (multimedia.getWidth() > Aksess.getMaxMediaWidth() ||  multimedia.getHeight() > Aksess.getMaxMediaHeight()) {
                try {
                    multimedia = ImageHelper.resizeImage(multimedia, Aksess.getMaxMediaWidth(), Aksess.getMaxMediaHeight());
                } catch (InterruptedException e) {
                    throw new SystemException(SOURCE, "InterruptedException", e);
                } catch (IOException e) {
                    throw new SystemException(SOURCE, "IOException", e);
                }
            }
        }

        int id = MultimediaAO.setMultimedia(multimedia);
        multimedia.setId(id);
        if (multimedia != null && Aksess.isEventLogEnabled()) {
            if (multimedia.getType() == MultimediaType.FOLDER) {
                EventLog.log(securitySession, request, Event.SAVE_MULTIMEDIA, multimedia.getName());
            } else {
                EventLog.log(securitySession, request, Event.SAVE_MULTIMEDIA, multimedia.getName(), multimedia);
            }
        }
        return id;
    }


    public void moveMultimedia(int mmId, int newParentId) throws SystemException, NotAuthorizedException {
        Multimedia mm = getMultimedia(mmId);
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
            throw new NotAuthorizedException("Kan ikke flytte multimedia", SOURCE);
        }

        MultimediaAO.moveMultimedia(mmId, newParentId);
    }

    public void deleteMultimedia(int id) throws SystemException, ObjectInUseException {
        String title = null;
        if (id != -1 && Aksess.isEventLogEnabled()) {
            Multimedia t = getMultimedia(id);
            if (t != null) {
                title = t.getName();
            }
        }
        MultimediaAO.deleteMultimedia(id);
        if (title != null) {
            EventLog.log(securitySession, request, Event.DELETE_MULTIMEDIA, title);
        }
    }

    /**
     * Performs a search for multimedia matching the given phrase, published on the given site and published in content
     * which is a child of the content given by parentId.
     *
     * @param phrase the text to search for
     * @param site the site to limit the search by, or -1 for global.
     * @param parentId the root of the subtree of contents to limit the search by, or -1 for all
     * @return a list of Multimedia-objects matching the given criteria
     * @throws SystemException if a SystemException is thrown by the underlying AO
     */
    public List<Multimedia> searchMultimedia(String phrase, int site, int parentId) throws SystemException {
        List<Multimedia> list = MultimediaAO.searchMultimedia(phrase, site, parentId);

        List<Multimedia> approved = new ArrayList<Multimedia>();
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

    public List getUsages(int multimediaId) throws SystemException {
        List pages = new ArrayList();

        List contentIds = MultimediaUsageAO.getUsagesForMultimediaId(multimediaId);
        for (int i = 0; i < contentIds.size(); i++) {
            ContentIdentifier cid = new ContentIdentifier();
            cid.setContentId((Integer)contentIds.get(i));
            Content content = ContentAO.getContent(cid, true);
            if (content != null) {
                pages.add(content);                
            }
        }

        return pages;
    }
}
