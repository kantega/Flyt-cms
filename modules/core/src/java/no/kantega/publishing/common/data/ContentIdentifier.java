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

package no.kantega.publishing.common.data;

import no.kantega.publishing.common.data.enums.Language;
import no.kantega.publishing.common.ContentIdHelper;
import no.kantega.publishing.common.cache.SiteCache;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;


import javax.servlet.http.HttpServletRequest;

public class ContentIdentifier {
    private static final String SOURCE = "aksess.ContentIdentifier";

    private int contentId = -1; // Peker til innhold - tilsvarer content.getId()
    private int associationId = -1; // Peker til menypunkt - tilsvarer association.getAssociationId()
    private int siteId = -1;    // Brukes sammen contentId for å finne en associationId
    private int contextId = -1; // Brukes sammen contentId for å finne en associationId
    private int language = Language.NORWEGIAN_BO;
    private int version = -1;
    private int status = -1;

    public ContentIdentifier() {
    }

    /**
     * @deprecated Use setAssociationId()
     */
    public ContentIdentifier(int id) {
        this.contentId = id;
    }

    /**
     * @deprecated Use setAssociationId() and setLanguage()
     */
    public ContentIdentifier(int id, int language) {
        this.contentId = id;
        this.language = language;
    }

    public ContentIdentifier(HttpServletRequest request) throws ContentNotFoundException, SystemException {
        Content current = (Content)request.getAttribute("aksess_this");
        if (current != null) {
            this.associationId = current.getAssociation().getId();
            this.language = current.getLanguage();
            this.version = current.getVersion();
        } else  if (request.getParameter("contentId") != null || request.getParameter("thisId") != null) {
            if (request.getParameter("contentId") != null) {
                try {
                    this.contentId = Integer.parseInt(request.getParameter("contentId"));
                } catch (NumberFormatException e) {
                    throw new ContentNotFoundException(request.getParameter("contentId"), SOURCE);
                }
                if (request.getParameter("siteId") != null) {
                    try {
                        this.siteId = Integer.parseInt(request.getParameter("siteId"));
                    } catch (NumberFormatException e) {
                    }
                }
                if (request.getParameter("contextId") != null) {
                    try {
                        this.contextId = Integer.parseInt(request.getParameter("contextId"));
                    } catch (NumberFormatException e) {
                    }
                }
            } else {
                try {
                    this.associationId = Integer.parseInt(request.getParameter("thisId"));
                } catch (NumberFormatException e) {
                    throw new ContentNotFoundException(request.getParameter("thisId"), SOURCE);
                }
            }

            try {
                String lang = request.getParameter("language");
                if (lang != null) {
                    this.language = Integer.parseInt(request.getParameter("language"));
                }
            } catch (NumberFormatException e) {
                // Bruk standard språk
            }

            try {
                String ver = request.getParameter("version");
                if (ver != null) {
                    this.version = Integer.parseInt(request.getParameter("version"));
                }
            } catch (NumberFormatException e) {
                // Bruk default versjon
            }
        } else {
            String url = request.getServletPath();
            String queryString = request.getQueryString();
            if (queryString != null && queryString.length() > 0) {
                url = url + "?" + queryString;
            }

            int siteId = ContentIdHelper.getSiteIdFromRequest(request);

            ContentIdentifier cid = ContentIdHelper.findContentIdentifier(siteId, url);
            this.contentId = cid.contentId;
            this.associationId = cid.associationId;
            this.language = cid.language;

        }
    }


    public ContentIdentifier(HttpServletRequest request, String url) throws ContentNotFoundException, SystemException {
        int siteId = ContentIdHelper.getSiteIdFromRequest(request);                 
        ContentIdentifier cid = ContentIdHelper.findContentIdentifier(siteId, url);
        this.contentId = cid.contentId;
        this.associationId = cid.associationId;
        this.siteId = cid.siteId;
        this.language = cid.language;
    }

    public ContentIdentifier(int siteId, String url) throws SystemException, ContentNotFoundException {
        ContentIdentifier cid = ContentIdHelper.findContentIdentifier(siteId, url);
        this.contentId = cid.contentId;
        this.associationId = cid.associationId;
        this.siteId = cid.siteId;
        this.language = cid.language;
    }


    public ContentIdentifier(String url) throws ContentNotFoundException, SystemException {
        ContentIdentifier cid = ContentIdHelper.findContentIdentifier(-1, url);
        this.contentId = cid.contentId;
        this.associationId = cid.associationId;
        this.siteId = cid.siteId;
        this.language = cid.language;
    }

    public int getContentId() {
        if (contentId == -1 && associationId != -1) {
            try {
                contentId = ContentIdHelper.findContentIdFromAssociationId(associationId);
            } catch (SystemException e) {
                Log.error(SOURCE, e, null, null);
            }
        }
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public int getAssociationId() {
        if (contentId != -1 && associationId == -1) {
            try {
                associationId = ContentIdHelper.findAssociationIdFromContentId(contentId, siteId, contextId);
            } catch (SystemException e) {
                Log.error(SOURCE, e, null, null);
            }
        }
        return associationId;
    }

    public void setAssociationId(int associationId) {
        this.associationId = associationId;
    }

    public void setContextId(int contextId) {
        this.contextId = contextId;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String toString() {
        String idStr = "";
        if (this.associationId != -1) {
            idStr = "thisId=" + associationId;
        } else {
            idStr = "contentId=" + contentId;
        }
        if (version != -1) {
            idStr += "&amp;version=" + version;
        }
        if (siteId != -1) {
            idStr += "&amp;siteId=" + siteId;
        }

        return idStr;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContentIdentifier that = (ContentIdentifier) o;

        if (associationId != that.associationId) return false;
        if (contentId != that.contentId) return false;
        if (contextId != that.contextId) return false;
        if (language != that.language) return false;
        if (siteId != that.siteId) return false;
        if (status != that.status) return false;
        if (version != that.version) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = contentId;
        result = 31 * result + associationId;
        result = 31 * result + siteId;
        result = 31 * result + contextId;
        result = 31 * result + language;
        result = 31 * result + version;
        result = 31 * result + status;
        return result;
    }
}
