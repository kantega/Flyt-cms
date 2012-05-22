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

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ContentIdHelper;
import no.kantega.publishing.common.data.enums.Language;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

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
    private final int defaultContentID = -1;
    private final int defaultSiteId = -1;
    private final int defaultContextId = -1;
    private final int defaultVersion = -1;

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
        String url = request.getServletPath();
        String path = request.getPathInfo();
        Content current = (Content)request.getAttribute("aksess_this");
        if (current != null) {
            this.associationId = current.getAssociation().getId();
            this.language = current.getLanguage();
            this.version = current.getVersion();
        } else  if (request.getParameter("contentId") != null || request.getParameter("thisId") != null) {
            if (request.getParameter("contentId") != null) {
                this.contentId = ServletRequestUtils.getIntParameter(request, "contentId", defaultContentID);
                this.siteId = ServletRequestUtils.getIntParameter(request, "siteId", defaultSiteId);
                this.contextId = ServletRequestUtils.getIntParameter(request, "contextId", defaultContextId);
            } else {
                try {
                    this.associationId = ServletRequestUtils.getIntParameter(request, "thisId");
                } catch (ServletRequestBindingException e) {
                    throw new ContentNotFoundException(request.getParameter("thisId"), SOURCE);
                }
            }

            this.language = ServletRequestUtils.getIntParameter(request, "language", Language.NORWEGIAN_BO);

        } else if (url.startsWith(Aksess.CONTENT_URL_PREFIX) && path != null && path.indexOf("/") == 0) {
            try {
                int slashIndex = path.indexOf("/", 1);
                if (slashIndex != -1) {
                    this.associationId = Integer.parseInt(path.substring(1, slashIndex));
                }
            } catch (NumberFormatException e) {
                throw new ContentNotFoundException(path, SOURCE);
            }
        } else {
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

        this.version = ServletRequestUtils.getIntParameter(request, "version", defaultVersion);
    }


    public ContentIdentifier(HttpServletRequest request, String url) throws ContentNotFoundException, SystemException {
        int siteId = ContentIdHelper.getSiteIdFromRequest(request, url);
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
