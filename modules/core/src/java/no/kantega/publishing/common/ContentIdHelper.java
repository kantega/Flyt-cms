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

import com.google.gdata.util.common.base.Pair;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.commons.util.StringHelper;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentIdentifierDao;
import no.kantega.publishing.api.content.Language;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.data.SortOrder;
import no.kantega.publishing.common.data.enums.AssociationType;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.spring.RootContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.removeEnd;

public class ContentIdHelper {
    private static final String SOURCE = "aksess.ContentIdHelper";

    private static final int defaultContentID = -1;
    private static final int defaultSiteId = -1;
    private static final int defaultContextId = -1;
    private static final int defaultVersion = -1;

    private static SiteCache siteCache;
    private static ContentIdentifierDao contentIdentifierDao;

    /**
     * Find the ContentIdentifer for a Content, relative to the context-Content and expr.
     * @param context - The current Content-contect
     * @param expr    - «..[/..]+» for n levels up.
     *                - «.» for this page.
     *                - «group» for the context's group.
     *                - «/+» for from root n levels down.
     *                - «next» or «previous» for the next or previous child below the context's parent.
     *                - «/alias» for getting content with given alias
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

            ContentIdentifier cid =  ContentIdentifier.fromAssociationId(pathElements[pathElements.length - exprLength]);
            assureContentIdAndAssociationIdSet(cid);
            cid.setLanguage(context.getLanguage());
            return cid;
        } else if (expr.equalsIgnoreCase(".")) {
            // Hent fra denne siden
            ContentIdentifier cid =  ContentIdentifier.fromAssociationId(context.getAssociation().getAssociationId());
            cid.setLanguage(context.getLanguage());
            assureContentIdAndAssociationIdSet(cid);
            return cid;
        } else if (expr.equalsIgnoreCase("group")) {
            // Hent fra group
            ContentIdentifier cid =  ContentIdentifier.fromContentId(context.getGroupId());
            cid.setLanguage(context.getLanguage());
            assureContentIdAndAssociationIdSet(cid);
            return cid;
        } else if (expr.startsWith("/+")) {
            // Hent fra root + nivå n
            int level = Integer.parseInt(expr.substring("/+".length(), expr.length()));
            String path = context.getAssociation().getPath() + context.getAssociation().getId() + "/";
            int[] pathElements = StringHelper.getInts(path, "/");

            if (level > pathElements.length) {
                level = pathElements.length;
            }

            ContentIdentifier contentIdentifier = ContentIdentifier.fromAssociationId(pathElements[level]);
            assureContentIdAndAssociationIdSet(contentIdentifier);
            return contentIdentifier;
        } else if (expr.equalsIgnoreCase("next") || expr.equalsIgnoreCase("previous")){

            boolean next = expr.equalsIgnoreCase("next");
            Association association = context.getAssociation();
            ContentIdentifier parent = ContentIdentifier.fromAssociationId(association.getParentAssociationId());

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
            if(contentIdentifierDao == null){
                contentIdentifierDao = RootContext.getInstance().getBean(ContentIdentifierDao.class);
            }
            return contentIdentifierDao.getContentIdentifierBySiteIdAndAlias(context.getAssociation().getSiteId(), expr);
        }

    }

    private static int findAssociationIdFromContentId(int contentId, int siteId, int contextId) throws SystemException {
        int associationId = -1;

        try (Connection c = dbConnectionFactory.getConnection()){

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
        }

        return associationId;
    }

    private static int findContentIdFromAssociationId(int associationId) throws SystemException {
        int contentId = -1;

        try (Connection c = dbConnectionFactory.getConnection()){
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
        }

        return contentId;
    }

    private static int getSiteIdFromRequest(HttpServletRequest request) throws SystemException {
        return getSiteIdFromRequest(request, null).first;
    }

    /**
     * @return pair containing found siteId and the alias tried found.
     * If url is siteId/alias, url is adjusted to /alias
     */
    private static Pair<Integer, String> getSiteIdFromRequest(HttpServletRequest request, String url) throws SystemException {
        int siteId = -1;
        String adjustedUrl = url;
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

        setSiteCacheIfNull();
        if (siteId == -1) {
            Site site = siteCache.getSiteByHostname(request.getServerName());
            if (site != null) {
                siteId = site.getId();
            }
            if(url != null) {
                List<Site> sites = siteCache.getSites();
                for (Site s : sites){
                    String siteAliasWithoutTrailingSlash = removeEnd(s.getAlias(), "/");
                    if(url.startsWith(siteAliasWithoutTrailingSlash)){
                        adjustedUrl = StringUtils.remove(url, siteAliasWithoutTrailingSlash);
                        if(adjustedUrl.equals("")) adjustedUrl = "/";
                        siteId = s.getId();
                        break;
                    }
                }
            }
        }
        if(siteId == -1){
            siteId = siteCache.getDefaultSite().getId();
        }

        return new Pair<>(siteId, adjustedUrl);
    }

    /**
     * // TODO ATTACK!
     * @param request - The current request
     * @return ContentIdentifier for the given request.
     * @throws ContentNotFoundException
     */
    public static ContentIdentifier fromRequest(HttpServletRequest request) throws ContentNotFoundException {
        ContentIdentifier contentIdentifier = null;
        String url = request.getServletPath();
        String path = request.getPathInfo();
        Content current = (Content)request.getAttribute("aksess_this");
        if (current != null) {
            contentIdentifier = ContentIdentifier.fromAssociationId(current.getAssociation().getId());
            contentIdentifier.setLanguage(current.getLanguage());
            contentIdentifier.setVersion(current.getVersion());
        } else  if (request.getParameter("contentId") != null || request.getParameter("thisId") != null) {
            if (request.getParameter("contentId") != null) {
                contentIdentifier = ContentIdentifier.fromContentId(ServletRequestUtils.getIntParameter(request, "contentId", defaultContentID));
                contentIdentifier.setSiteId(ServletRequestUtils.getIntParameter(request, "siteId", defaultSiteId));
                contentIdentifier.setContextId(ServletRequestUtils.getIntParameter(request, "contextId", defaultContextId));
            } else {
                try {
                    contentIdentifier = ContentIdentifier.fromAssociationId(ServletRequestUtils.getIntParameter(request, "thisId"));
                } catch (ServletRequestBindingException e) {
                    throw new ContentNotFoundException(request.getParameter("thisId"), SOURCE);
                }
            }

            contentIdentifier.setLanguage(ServletRequestUtils.getIntParameter(request, "language", Language.NORWEGIAN_BO));

        } else if (url.startsWith(Aksess.CONTENT_URL_PREFIX) && path != null && path.indexOf("/") == 0) {
            try {
                int slashIndex = path.indexOf("/", 1);
                if (slashIndex != -1) {
                    contentIdentifier = ContentIdentifier.fromAssociationId(Integer.parseInt(path.substring(1, slashIndex)));
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

            contentIdentifier = ContentIdHelper.findContentIdentifier(siteId, url);
        }

        if (contentIdentifier != null) {
            contentIdentifier.setVersion(ServletRequestUtils.getIntParameter(request, "version", defaultVersion));
        }
        return contentIdentifier;
    }

    /**
     *      * // TODO ATTACK!
     * @param request - The current request
     * @param url - The url of ContentIdentifier is desired for.
     * @return ContentIdentifier for url.
     * @throws ContentNotFoundException
     * @throws SystemException
     */
    public static ContentIdentifier fromRequestAndUrl(HttpServletRequest request, String url) throws ContentNotFoundException, SystemException {
        Pair<Integer, String> siteId = ContentIdHelper.getSiteIdFromRequest(request, url);
        return ContentIdHelper.findContentIdentifier(siteId.first, siteId.second);
    }

    /**
     * Make sure the given ContentIdentifier has both contentId and associationId set.
     * @param contentIdentifier assure both are set on.
     */
    public static void assureContentIdAndAssociationIdSet(ContentIdentifier contentIdentifier){
        if (contentIdentifier != null) {
            int associationId = contentIdentifier.getAssociationId();
            int contentId = contentIdentifier.getContentId();

            if (contentId != -1 && associationId == -1) {
                try {
                    associationId = ContentIdHelper.findAssociationIdFromContentId(contentId, contentIdentifier.getSiteId(), contentIdentifier.getContextId());
                    contentIdentifier.setAssociationId(associationId);
                } catch (SystemException e) {
                    Log.error(SOURCE, e, null, null);
                }
            } else if (contentId == -1 && associationId != -1) {
                try {
                    contentId = ContentIdHelper.findContentIdFromAssociationId(associationId);
                    contentIdentifier.setContentId(contentId);
                } catch (SystemException e) {
                    Log.error(SOURCE, e, null, null);
                }
            }
        }
    }

    private static void setSiteCacheIfNull() {
        if(siteCache == null){
            siteCache = RootContext.getInstance().getBean(SiteCache.class);
        }
    }
}
