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
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentIdentifierDao;
import no.kantega.publishing.api.content.Language;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.ao.AssociationAO;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.data.SortOrder;
import no.kantega.publishing.common.data.enums.AssociationType;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.content.api.ContentAO;
import no.kantega.publishing.content.api.ContentIdHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static no.kantega.publishing.api.ContentUtil.tryGetFromRequest;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

public class ContentIdHelperImpl extends JdbcDaoSupport implements ContentIdHelper, ServletContextAware {
    private static final Logger log = LoggerFactory.getLogger(ContentIdHelperImpl.class);
    private static final Pattern dotPattern = Pattern.compile("\\.\\.");

    private final int defaultContentID = -1;
    private final int defaultSiteId = -1;
    private final int defaultContextId = -1;
    private final int defaultVersion = -1;
    private final Pattern siteIdPattern = Pattern.compile(".*siteId=(?<siteId>\\d+).*");
    private final Pattern thisIdPattern = Pattern.compile(".*thisId=(?<thisId>\\d+).*");

    @Autowired
    private SiteCache siteCache;

    @Autowired
    private ContentAO contentAO;

    @Autowired
    private ContentIdentifierDao contentIdentifierDao;
    public Pattern CONTENT_URL_PATTERN;


    @Override
    public ContentIdentifier findRelativeContentIdentifier(Content context, String expr) throws SystemException, ContentNotFoundException {
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
            String[] exprPathElements = dotPattern.split(expr);
            int exprLength = exprPathElements.length - 1;
            if (exprLength > pathElements.length) {
                throw new ContentNotFoundException(expr);
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

            List<Content> children = contentAO.getContentList(query, -1, new SortOrder(ContentProperty.PRIORITY, false), false);
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
     * @param siteId - Site
     * @param url    - Url/alias, e.g. /nyheter/
     * @return ContentIdentifier for the given site and url.
     * @throws no.kantega.publishing.common.exception.ContentNotFoundException if no Content is found.
     * @throws no.kantega.commons.exception.SystemException
     */
    private ContentIdentifier findContentIdentifier(int siteId, String url) throws ContentNotFoundException, SystemException {
        if (url == null) {
            throw new ContentNotFoundException("");
        }
        Matcher contentUrlMatcher = CONTENT_URL_PATTERN.matcher(url.trim());
        if(!contentUrlMatcher.matches()){
            throw new ContentNotFoundException(url);
        }

        int contentId = -1;
        int associationId = -1;
        int version  = -1;
        int language = Language.NORWEGIAN_BO;

        String thisIdGroup = contentUrlMatcher.group("thisId");
        String contentIdGroup = contentUrlMatcher.group("contentId");
        String versionGroup = contentUrlMatcher.group("version");
        String languageGroup = contentUrlMatcher.group("language");
        String prettythisIdGroup = contentUrlMatcher.group("prettythisId");

        if(thisIdGroup != null){
            associationId = Integer.parseInt(thisIdGroup);
        }
        if(contentIdGroup != null){
            contentId = Integer.parseInt(contentIdGroup);
        }
        if(versionGroup != null){
            version = Integer.parseInt(versionGroup);
        }
        if(languageGroup != null){
            language = Integer.parseInt(languageGroup);
        }
        if(prettythisIdGroup != null){
            associationId = Integer.parseInt(prettythisIdGroup);
        }


        if (contentId != -1 || associationId != -1) {
            ContentIdentifier cid = new ContentIdentifier();
            cid.setContentId(contentId);
            cid.setAssociationId(associationId);
            cid.setSiteId(siteId);
            cid.setVersion(version);
            cid.setLanguage(language);
            assureContentIdAndAssociationIdSet(cid);
            return cid;
        } else {
            url = defaultIfBlank(contentUrlMatcher.group("content"), "/");
            if (siteId != -1) {
                if ("/".equalsIgnoreCase(url)) {
                    Site site = siteCache.getSiteById(siteId);
                    if (site != null) {
                        url = site.getAlias();
                    }
                }
            } else if ("/".equalsIgnoreCase(url)) {
                Site defaultSite = siteCache.getDefaultSite();
                if (defaultSite != null) {
                    siteId = defaultSite.getId();
                    url = defaultSite.getAlias();
                } else {
                    siteId = 1;
                    url = siteCache.getSiteById(siteId).getAlias();
                }
            } else {
                Pair<Integer, String> urlAdjustedBySiteAlias = getUrlAdjustedBySiteAlias(null, url);
                siteId = urlAdjustedBySiteAlias.getLeft();
                url = urlAdjustedBySiteAlias.getRight();
            }

            return getContentIdentifier(siteId, url);
        }
    }

    /**
     * Check url for whether it starts with a site alias. If it does, remove site alias.
     * Eg site with alias /sitealias and url /sitealias results in url /, url /sitealias/alias gived /alias
     * @param site to check url against. May be null, then all sites are tested.
     * @param url Url to test
     * @return pair with siteId and adjusted url. If site given is null and no site alias match, the resulting siteId
     * is -1 and the same url as given is returned.
     */
    private Pair<Integer, String> getUrlAdjustedBySiteAlias(Site site, String url){
        String ajustedUrl = url.endsWith("/") ? url : url + "/";
        int siteId = -1;
        if(site == null){
            List<Site> sites = siteCache.getSites();
            for (Site s : sites) {
                String siteAliasWithTrailingSlash = s.getAlias().endsWith("/") ? s.getAlias() : s.getAlias() + "/";
                if (ajustedUrl.startsWith(siteAliasWithTrailingSlash)) {
                    url = "/" + StringUtils.removeStart(ajustedUrl, siteAliasWithTrailingSlash);
                    siteId = s.getId();
                    break;
                }
            }
        } else {
            String siteAliasWithTrailingSlash = site.getAlias().endsWith("/") ? site.getAlias() : site.getAlias() + "/";
            if (ajustedUrl.startsWith(siteAliasWithTrailingSlash)) {
                url = "/" + StringUtils.remove(ajustedUrl, siteAliasWithTrailingSlash);
            }
            siteId = site.getId();
        }

        return Pair.of(siteId, url);
    }

    private ContentIdentifier getContentIdentifier(int siteId, String url) throws ContentNotFoundException {
        ContentIdentifier cid = null;
        if (siteId > 0) {
            cid = contentIdentifierDao.getContentIdentifierBySiteIdAndAlias(siteId, url);
        }

        if(cid == null) {
            // we are likely in development, where no sites are configured.
            List<ContentIdentifier> contentIdentifiersByAlias = contentIdentifierDao.getContentIdentifiersByAlias(url);
            if(!contentIdentifiersByAlias.isEmpty()){
                cid = contentIdentifiersByAlias.get(0);
            }
        }

        if (cid == null) {
            throw new ContentNotFoundException(url);
        }
        return cid;
    }

    private int findAssociationIdFromContentId(int contentId, int siteId, int contextId) throws SystemException {
        int associationId = -1;

        try {
            if (contextId != -1) {
                // Først prøver vi å finne siden under der lenka kom fra
                List<Integer> associationIds = getJdbcTemplate().queryForList("SELECT AssociationId FROM associations WHERE ContentId = ? AND Path like ? AND (IsDeleted IS NULL OR IsDeleted = 0)", Integer.class,
                        contentId, "%/" + contextId + "/%");

                if(!associationIds.isEmpty()) {
                    associationId = associationIds.get(0);
                }

                // Så i samme nettsted som lenka kom fra
                if (associationId == -1) {
                    siteId = getJdbcTemplate().queryForObject("SELECT SiteId FROM associations WHERE AssociationId = ? AND (IsDeleted IS NULL OR IsDeleted = 0)", Integer.class,  contextId);
                }
            }

            if (associationId == -1) {
                List<Map<String,Object>> ids = getJdbcTemplate().queryForList("SELECT AssociationId, SiteId FROM associations WHERE ContentId = ? AND Type = ? AND (IsDeleted IS NULL OR IsDeleted = 0)", contentId, AssociationType.DEFAULT_POSTING_FOR_SITE);

                for(Map<String, Object> id : ids) {
                    int tmp = ((Number) id.get("AssociationId")).intValue();
                    int tmpSiteId = ((Number) id.get("SiteId")).intValue();
                    if (associationId == -1 || tmpSiteId == siteId) {
                        associationId = tmp;
                    }
                }
            }
        } catch (DataAccessException e) {
            log.error("Could not find associationId for contentid " + contentId);
        }

        return associationId;
    }

    private int findContentIdFromAssociationId(int associationId) throws SystemException {
        int contentId = -1;

        List<Pair<Integer, Integer>> contentIdAndTypes = getJdbcTemplate().query("select ContentId, Type from associations where AssociationId = ?", associationContentIdAndTypeMapper, associationId);
        if(contentIdAndTypes.size() == 1){
            contentId = contentIdAndTypes.get(0).getLeft();
        } else if( contentIdAndTypes.size() > 1){
            log.warn("Got several results for select ContentId, Type from associations where AssociationId = {}: {}", associationId, contentIdAndTypes);
            for (Pair<Integer, Integer> contentIdAndType : contentIdAndTypes) {
                if(contentIdAndType.getRight() != AssociationType.SHORTCUT){
                    contentId = contentIdAndType.getLeft();
                    break;
                }
            }

        } else {
            log.error("Could not find contentid for associationid " + associationId);
        }
        return contentId;
    }

    private int getSiteIdFromRequest(HttpServletRequest request) throws SystemException {
        return getSiteIdFromRequest(request, null).getLeft();
    }

    /**
     * @return pair containing found siteId and the alias tried found.
     * If url is siteId/alias, url is adjusted to /alias
     */
    private Pair<Integer, String> getSiteIdFromRequest(HttpServletRequest request, String url) throws SystemException {
        int siteId = ServletRequestUtils.getIntParameter(request, "siteId", -1);
        String adjustedUrl = url;

        if (siteId == -1) {
            Content content = tryGetFromRequest(request);
            if (content != null) {
                siteId = content.getAssociation().getSiteId();
            }
        }

        if (siteId == -1 && url != null) {
            Matcher siteIdMatcher = siteIdPattern.matcher(url);
            if(siteIdMatcher.matches()){
                siteId = Integer.parseInt(siteIdMatcher.group("siteId"));
            }
        }

        if (siteId == -1 && url != null) {
            Matcher thisIdMatcher = thisIdPattern.matcher(url);
            if (thisIdMatcher.matches()) {
                int thisId = Integer.parseInt(thisIdMatcher.group("thisId"));
                siteId = AssociationAO.getAssociationById(thisId).getSiteId();
            }
        }

        if (siteId == -1) {
            Site site = siteCache.getSiteByHostname(request.getServerName());
            if (site != null) {
                siteId = site.getId();
            }
            if(url != null) {
                Pair<Integer, String> urlAdjustedBySiteAlias = getUrlAdjustedBySiteAlias(site, url);
                siteId = urlAdjustedBySiteAlias.getLeft();
                adjustedUrl = urlAdjustedBySiteAlias.getRight();

            }
        }
        if(siteId == -1){
            siteId = siteCache.getDefaultSite().getId();
        }

        return  Pair.of(siteId, adjustedUrl);
    }

    @Override
    public ContentIdentifier fromRequest(HttpServletRequest request) throws ContentNotFoundException {
        ContentIdentifier contentIdentifier = null;
        String url = request.getServletPath();
        String path = request.getPathInfo();
        Content current = tryGetFromRequest(request);
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
                    throw new ContentNotFoundException(request.getParameter("thisId"));
                }
            }

            contentIdentifier.setLanguage(ServletRequestUtils.getIntParameter(request, "language", Language.NORWEGIAN_BO));

        } else if (url.startsWith(Aksess.CONTENT_URL_PREFIX) && path != null && path.indexOf('/') == 0) {
            try {
                int slashIndex = path.indexOf('/', 1);
                if (slashIndex != -1) {
                    contentIdentifier = ContentIdentifier.fromAssociationId(Integer.parseInt(path.substring(1, slashIndex)));
                }
            } catch (NumberFormatException e) {
                throw new ContentNotFoundException(path);
            }
        } else {
            if (url.endsWith(Aksess.getStartPage())) {
                // Used when a directory with a index.jsp is accessed directly
                url = url.substring(0, url.length() - Aksess.getStartPage().length() + 1);
            }

            String queryString = request.getQueryString();
            if (queryString != null && queryString.length() > 0) {
                url = url + "?" + queryString;
            }

            int siteId = getSiteIdFromRequest(request);

            contentIdentifier = findContentIdentifier(siteId, url);
        }

        if (contentIdentifier != null) {
            contentIdentifier.setVersion(ServletRequestUtils.getIntParameter(request, "version", defaultVersion));
        }
        return contentIdentifier;
    }

    @Override
    public ContentIdentifier fromRequestAndUrl(HttpServletRequest request, String url) throws ContentNotFoundException, SystemException {
        Pair<Integer, String> siteId = getSiteIdFromRequest(request, url);
        return findContentIdentifier(siteId.getLeft(), siteId.getRight());
    }

    @Override
    public ContentIdentifier fromSiteIdAndUrl(int siteId, String url) throws SystemException, ContentNotFoundException {
        return findContentIdentifier(siteId, url);
    }

    @Override
    public ContentIdentifier fromUrl(String url) throws ContentNotFoundException, SystemException {
        return findContentIdentifier(-1, url);
    }

    @Override
    public void assureContentIdAndAssociationIdSet(ContentIdentifier contentIdentifier){
        if (contentIdentifier != null) {
            int associationId = contentIdentifier.getAssociationId();
            int contentId = contentIdentifier.getContentId();

            if (contentId != -1 && associationId == -1) {
                try {
                    associationId = findAssociationIdFromContentId(contentId, contentIdentifier.getSiteId(), contentIdentifier.getContextId());
                    contentIdentifier.setAssociationId(associationId);
                } catch (SystemException e) {
                    log.error("", e);
                }
            } else if (contentId == -1 && associationId != -1) {
                try {
                    contentId = findContentIdFromAssociationId(associationId);
                    contentIdentifier.setContentId(contentId);
                } catch (SystemException e) {
                    log.error("", e);
                }
            }
        }
    }

    @Override
    public void assureAssociationIdSet(ContentIdentifier contentIdentifier){
        if (contentIdentifier != null) {
            int associationId = contentIdentifier.getAssociationId();
            int contentId = contentIdentifier.getContentId();

            if (contentId != -1 && associationId == -1) {
                try {
                    associationId = findAssociationIdFromContentId(contentId, contentIdentifier.getSiteId(), contentIdentifier.getContextId());
                    contentIdentifier.setAssociationId(associationId);
                } catch (SystemException e) {
                    log.error("", e);
                }
            }
        }
    }


    @Override
    public void setServletContext(ServletContext servletContext) {
        CONTENT_URL_PATTERN = Pattern.compile(ContentPatterns.getPatternWithContextPath(servletContext.getContextPath()), Pattern.UNICODE_CHARACTER_CLASS);
    }

    private static final RowMapper<Pair<Integer, Integer>> associationContentIdAndTypeMapper = new RowMapper<Pair<Integer, Integer>>() {
        @Override
        public Pair<Integer, Integer> mapRow(ResultSet rs, int rowNum) throws SQLException {
            return  Pair.of(rs.getInt("ContentId"), rs.getInt("Type"));
        }
    };
}
