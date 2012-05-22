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

package no.kantega.publishing.common;

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.StringHelper;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.cache.ContentIdentifierCache;
import no.kantega.publishing.common.cache.SiteCache;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.enums.AssociationType;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.data.enums.Language;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.util.database.dbConnectionFactory;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ContentIdHelper {
    private static final String SOURCE = "aksess.ContentIdHelper";

    /**
     * Finner en identifikator til et innholdobjekt som er relativt til context
     * @param context - Innholdsobjekt
     * @param expr    - Path, f.eks ../ eller ../../ eller /
     * @return        - ContentIdentifier
     * @throws SystemException
     * @throws ContentNotFoundException
     */

    public static ContentIdentifier findRelativeContentIdentifier(Content context, String expr) throws SystemException, ContentNotFoundException {
        if (context == null || expr == null) {
            return null;
        }

        if (expr.contains("..")) {
            // Hent fra N nivåer lengre opp
            if (expr.charAt(expr.length() - 1) != '/') {
                expr += "/";
            }

            Association association = context.getAssociation();
            String path = association.getPath();
            if (path == null || path.length() == 0) {
                // Finn path'en
                int parentId = association.getParentAssociationId();
                path = AssociationHelper.getPathForId(parentId);
            }

            int[] pathElements = StringHelper.getInts(path, "/");
            String[] exprPathElements = expr.split("\\.\\.");
            int exprLength = exprPathElements.length - 1;
            if (exprLength > pathElements.length) {
                throw new ContentNotFoundException(SOURCE, expr);
            }

            ContentIdentifier cid = new ContentIdentifier();
            cid.setAssociationId(pathElements[pathElements.length - exprLength]);
            cid.setLanguage(context.getLanguage());
            return cid;
        } else if (expr.equalsIgnoreCase(".")) {
            // Hent fra denne siden
            ContentIdentifier cid = new ContentIdentifier();
            cid.setAssociationId(context.getAssociation().getAssociationId());
            cid.setLanguage(context.getLanguage());
            return cid;
        } else if (expr.equalsIgnoreCase("group")) {
            // Hent fra group
            ContentIdentifier cid = new ContentIdentifier();
            cid.setContentId(context.getGroupId());
            cid.setLanguage(context.getLanguage());
            return cid;
        } else if (expr.startsWith("/+")) {
            // Hent fra root + nivå n
            int level = Integer.parseInt(expr.substring("/+".length(), expr.length()));
            String path = context.getAssociation().getPath() + context.getAssociation().getId() + "/";
            int[] pathElements = StringHelper.getInts(path, "/");

            if (level > pathElements.length) {
                level = pathElements.length;
            }

            ContentIdentifier cid = new ContentIdentifier();
            cid.setAssociationId(pathElements[level]);
            return cid;
        } else if (expr.equalsIgnoreCase("next") || expr.equalsIgnoreCase("previous")){

            boolean next = expr.equalsIgnoreCase("next");
            ContentIdentifier parent = new ContentIdentifier();
            Association association = context.getAssociation();
            parent.setAssociationId(association.getParentAssociationId());
            ContentQuery query = new ContentQuery();
            query.setAssociatedId(parent);
            query.setAssociationCategory(association.getCategory());
            List<Content> children = ContentAO.getContentList(query, -1, new SortOrder(ContentProperty.PRIORITY, false), false);
            for (int i = 0; i < children.size(); i++) {
                Content c = children.get(i);
                if (c.getAssociation().getId() == context.getAssociation().getId()) {
                    if (next){
                        if (i < children.size() -1){
                            return children.get(i + 1).getContentIdentifier();
                        } else {
                            return null;
                        }
                    } else {
                        if (i > 0) {
                            return children.get(i -1).getContentIdentifier();
                        } else {
                            return null;
                        }
                    }
                }
            }
            return null;

        } else {
            return findContentIdentifier(context.getAssociation().getSiteId(), expr);
        }

    }


    /**
     * Finner en identifikator til et innholdsobjekt basert på url/alias
     * @param siteId - Site
     * @param url    - Url/alias, f.eks /nyheter/
     * @return
     * @throws ContentNotFoundException
     * @throws SystemException
     */
    public static ContentIdentifier findContentIdentifier(int siteId, String url) throws ContentNotFoundException, SystemException {
        int contentId = -1;
        int associationId = -1;
        int version  = -1;
        int language = Language.NORWEGIAN_BO;

        if (url == null) {
            throw new ContentNotFoundException("", SOURCE);
        }

        if (url.indexOf('#') > 0) {
            url = url.substring(0, url.indexOf("#"));
        }

        url = url.toLowerCase();
        if (url.startsWith("http://") || url.startsWith("https://")) {
            url = url.substring(url.indexOf("://") + 3, url.length());
            if (url.indexOf('/') != -1) {
                url = url.substring(url.indexOf('/'), url.length());
            }

            String contextPath = Aksess.getContextPath().toLowerCase();
            if (url.length() > contextPath.length()) {
                url = url.substring(contextPath.length(), url.length());
            }
            if (url.length() == 0) {
                url = "/";
            }
        }

        int contentPos = url.indexOf(Aksess.CONTENT_URL_PREFIX);
        if (contentPos != -1 && url.length() > Aksess.CONTENT_URL_PREFIX.length() + 1) {
            String idStr = url.substring(contentPos + Aksess.CONTENT_URL_PREFIX.length() + 1, url.length());
            int end = idStr.indexOf("/");
            if (end != -1) {
                idStr = idStr.substring(0, end);
            }
            try {
                associationId = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                // Do nothing
            }
        }

        String aIdToken = "thisid=";
        int aIdPos = url.indexOf(aIdToken);
        if (aIdPos != -1) {
            String idStr = url.substring(aIdPos + aIdToken.length(), url.length());
            int end = idStr.indexOf('&');
            if (end != -1) {
                idStr = idStr.substring(0, end);
            }
            try {
                associationId = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                // Gjør ingenting
            }
        }

        if (associationId == -1) {
            String idToken = "contentid=";
            int idPos = url.indexOf(idToken);
            if (idPos != -1) {
                String idStr = url.substring(idPos + idToken.length(), url.length());
                int end = idStr.indexOf('&');
                if (end != -1) {
                    idStr = idStr.substring(0, end);
                }
                try {
                    contentId = Integer.parseInt(idStr);
                } catch (NumberFormatException e) {
                    // Gjør ingenting
                }
            }
        }

        String languageToken = "language=";
        int languagePos = url.indexOf(languageToken);
        if (languagePos != -1) {
            String languageStr = url.substring(languagePos + languageToken.length(), url.length());
            int end = languageStr.indexOf('&');
            if (end != -1) {
                languageStr = languageStr.substring(0, end);
            }
            try {
                language = Integer.parseInt(languageStr);
            } catch (NumberFormatException e) {
                // Standard språk
            }
        }

        String versionToken = "version=";
        int versionPos = url.indexOf(versionToken);
        if (versionPos != -1) {
            String versionStr = url.substring(versionPos + versionToken.length(), url.length());
            int end = versionStr.indexOf('&');
            if (end != -1) {
                versionStr = versionStr.substring(0, end);
            }
            try {
                version = Integer.parseInt(versionStr);
            } catch (NumberFormatException e) {
                // Siste versjon
            }
        }

        // Hvis contentId ikke finnes i URL, slå opp i basen
        if (contentId != -1 || associationId != -1) {
            ContentIdentifier cid = new ContentIdentifier();
            cid.setVersion(version);
            cid.setLanguage(language);

            if (associationId != -1) {
                cid.setAssociationId(associationId);
            } else {
                cid.setContentId(contentId);
                cid.setSiteId(siteId);
            }

            return cid;
        } else {
            int end = url.indexOf('?');
            if (end != -1) {
                url = url.substring(0, end);
            }

            end = url.lastIndexOf(Aksess.CONTENT_REQUEST_HANDLER);
            if (end != -1) {
                url = url.substring(0, end);
            } else {
                end = url.lastIndexOf(Aksess.getStartPage());
                if (end != -1) {
                    url = url.substring(0, end);
                }
            }
            if (siteId != -1) {
                if ("/".equalsIgnoreCase(url)) {
                    Site site = SiteCache.getSiteById(siteId);
                    if (site != null) {
                        url = site.getAlias();
                    }
                }
            }

            ContentIdentifier cid = ContentIdentifierCache.getContentIdentifierByAlias(siteId, url);
            if (cid == null) {
                throw new ContentNotFoundException(url, SOURCE);
            }
            return cid;
        }
    }

    public static int findAssociationIdFromContentId(int contentId, int siteId, int contextId) throws SystemException {
        int associationId = -1;

        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();

            PreparedStatement st;
            ResultSet rs;
            if (contextId != -1) {
                // Først prøver vi å finne siden under der lenka kom fra
                st = c.prepareStatement("SELECT AssociationId FROM associations WHERE ContentId = ? AND Path like ? AND (IsDeleted IS NULL OR IsDeleted = 0)");
                st.setInt(1, contentId);
                st.setString(2, "%/" + contextId + "/%");
                rs = st.executeQuery();
                if(rs.next()) {
                    associationId = rs.getInt("AssociationId");
                }

                rs.close();
                st.close();

                // Så i samme nettsted som lenka kom fra
                if (associationId == -1) {
                    st = c.prepareStatement("SELECT SiteId FROM associations WHERE AssociationId = ? AND (IsDeleted IS NULL OR IsDeleted = 0)");
                    st.setInt(1, contextId);
                    rs = st.executeQuery();
                    if(rs.next()) {
                        siteId = rs.getInt("SiteId");
                    }
                    rs.close();
                    st.close();
                }
            }

            if (associationId == -1) {
                st = c.prepareStatement("SELECT AssociationId, SiteId FROM associations WHERE ContentId = ? AND Type = ? AND (IsDeleted IS NULL OR IsDeleted = 0)");
                st.setInt(1, contentId);
                st.setInt(2, AssociationType.DEFAULT_POSTING_FOR_SITE);
                rs = st.executeQuery();
                while(rs.next()) {
                    int tmp = rs.getInt("AssociationId");
                    int tmpSiteId = rs.getInt("SiteId");
                    if (associationId == -1 || tmpSiteId == siteId) {
                        associationId = tmp;
                    }
                }

                rs.close();
                st.close();
            }
        } catch (SQLException e) {
            throw new SystemException("Feil ved SQL kall", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {

            }
        }

        return associationId;
    }

    public static int findContentIdFromAssociationId(int associationId) throws SystemException {
        int contentId = -1;

        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement st = c.prepareStatement("select ContentId from associations where AssociationId = ?");
            st.setInt(1, associationId);
            ResultSet rs = st.executeQuery();
            while(rs.next()) {
                contentId = rs.getInt("ContentId");
            }

            rs.close();
            st.close();
        } catch (SQLException e) {
            throw new SystemException("Feil ved SQL kall", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {

            }
        }

        return contentId;
    }

    public static int getSiteIdFromRequest(HttpServletRequest request) throws SystemException {
        return getSiteIdFromRequest(request, null);
    }

    public static int getSiteIdFromRequest(HttpServletRequest request, String url) throws SystemException {
        int siteId = -1;
        if (request.getParameter("siteId") != null) {
            try {
                siteId = Integer.parseInt(request.getParameter("siteId"));
            } catch (NumberFormatException e) {
            }
        }

        if (siteId == -1) {
            Content content = (Content)request.getAttribute("aksess_this");
            if (content != null) {
                siteId = content.getAssociation().getSiteId();
            }
        }

        if (siteId == -1 && url != null) {
            int siteIdPos = url.indexOf("siteId=");
            if (siteIdPos != -1) {
                String siteIdStr = url.substring(siteIdPos + "siteId=".length(), url.length());
                int siteIdEndPos = siteIdStr.indexOf("&");
                if (siteIdEndPos != -1) {
                    siteIdStr = siteIdStr.substring(0, siteIdEndPos);
                }
                try {
                    siteId = Integer.parseInt(siteIdStr);
                } catch (NumberFormatException e) {

                }
            }
        }

        if (siteId == -1) {
            Site site = SiteCache.getSiteByHostname(request.getServerName());
            if (site != null) {
                siteId = site.getId();
            } else {
                siteId = 1;
            }
        }

        return siteId;
    }
}
