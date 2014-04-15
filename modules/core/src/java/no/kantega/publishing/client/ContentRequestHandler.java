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

package no.kantega.publishing.client;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.HttpHelper;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.util.RequestHelper;
import no.kantega.publishing.content.api.ContentIdHelper;
import no.kantega.publishing.security.SecuritySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.IDN;
import java.util.List;

/**
 * Receives all incoming request for content, fetches from database and sends request to a dispatcher
 */
@Controller
public abstract class ContentRequestHandler implements ServletContextAware {
    private static final Logger log = LoggerFactory.getLogger(ContentRequestHandler.class);

    @Autowired
    private SiteCache siteCache;

    @Autowired
    private ContentRequestDispatcher contentRequestDispatcher;

    @Autowired
    private ContentIdHelper contentIdHelper;

    private ServletContext servletContext;

    private boolean addPagetypeToResponseHeader;

    @RequestMapping("/")
    public ModelAndView handleRoot(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ContentNotFoundException {
        ContentIdentifier cid = contentIdHelper.fromRequestAndUrl(request, "/");
        return handleFromContentIdentifier(cid, request, response);
    }

    @RequestMapping("/content/{thisId:[0-9]+}/*")
    public ModelAndView handlePrettyUrl(@PathVariable int thisId, @RequestParam(required = false) ContentStatus contentStatus, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ContentIdentifier cid = ContentIdentifier.fromAssociationId(thisId);
	cid.setStatus(contentStatus);
        return handleFromContentIdentifier(cid, request, response);
    }

    @RequestMapping("/content.ap")
    public ModelAndView handleContent_Ap(@RequestParam(required = false, defaultValue = "-1") int thisId,
                                         @RequestParam(required = false, defaultValue = "-1") int contentId,
                                         @RequestParam(required = false, defaultValue = "-1") int version,
                                         @RequestParam(required = false, defaultValue = "-1") int language,
                                         @RequestParam(required = false) ContentStatus contentStatus,
                                         @RequestParam(required = false, defaultValue = "-1") int siteId,
                                         @RequestParam(required = false, defaultValue = "-1") int contextId,
                                         HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ContentIdentifier cid = ContentIdentifier.fromAssociationId(thisId);
        cid.setContentId(contentId);
        cid.setVersion(version);
        cid.setLanguage(language);
        cid.setStatus(contentStatus);
        cid.setSiteId(siteId);
        cid.setContextId(contextId);
        return handleFromContentIdentifier(cid, request, response);
    }

    ModelAndView handleFromContentIdentifier(ContentIdentifier cid, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        SecuritySession securitySession = getSecuritySession();

        ContentManagementService cms = new ContentManagementService(securitySession, request);
        try {
            Content content = cms.getContent(cid, true);
            // Send NOT_FOUND if expired or not published
            boolean isAdminMode = HttpHelper.isAdminMode(request);
            if (content != null) {
                // Send NOT_FOUND if expired or not published
                if(!isAdminMode && isExpiredOrNotPublished(content)) {
                    log.info("Content {} ({}) not shown because it is expired or not published", content.getTitle(), content.getId());
                    throw new ContentNotFoundException(request.getRequestURI());
                }
                if (redirectToCorrectSiteIfOtherSite(request, response, isAdminMode, content)){
                    return null;
                }
                if (isAdminMode) {
                    response.setDateHeader("Expires", 0);
                }

                if(addPagetypeToResponseHeader){
                    response.addHeader("PageType", String.valueOf(content.getDisplayTemplateId()));
                }

                contentRequestDispatcher.dispatchContentRequest(content, servletContext, request, response);
                logTimeSpent(start, content);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                throw new ContentNotFoundException(request.getRequestURI());
            }

        } catch (NotAuthorizedException e) {
            // Check if user is logged in
            SecuritySession secSession = getSecuritySession();
            if (secSession.isLoggedIn()) {
                RequestHelper.setRequestAttributes(request, null);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                request.getRequestDispatcher("/403.jsp").forward(request, response);
            } else {
                // Start login process (redirect)
                secSession.initiateLogin(request, response);
            }
        } catch (ContentNotFoundException e) {
            try {
                RequestHelper.setRequestAttributes(request, null);
            } catch (SystemException e1) {
                log.error("Could not set request attributes", e1);
            }
            request.getRequestDispatcher("/404.jsp").forward(request, response);
        } catch (Throwable e) {
            if (e instanceof ServletException) {
                ServletException sex = (ServletException)e;
                if (sex.getRootCause() != null) {
                    e = sex.getRootCause();
                }
            }
            log.error(request.getRequestURI(), e);
            throw new ServletException(e);
        }

        return null;
    }

    private boolean isExpiredOrNotPublished(Content content) {
        ContentVisibilityStatus visibilityStatus = content.getVisibilityStatus();
        return (visibilityStatus != ContentVisibilityStatus.ACTIVE && visibilityStatus != ContentVisibilityStatus.ARCHIVED);
    }

    protected abstract SecuritySession getSecuritySession();

    private void logTimeSpent(long start, Content content) {
        long end = System.currentTimeMillis();
        log.info("Execution time: {} ms ({}, id: {}, template:{})", (end - start), content.getTitle(), content.getId(), content.getDisplayTemplateId());
    }

    private boolean redirectToCorrectSiteIfOtherSite(HttpServletRequest request, HttpServletResponse response, boolean adminMode, Content content) throws IOException {
        Site currentSite = siteCache.getSiteByHostname(request.getServerName());
        if(currentSite != null && !adminMode && content.getAssociation().getSiteId() != currentSite.getId()) {
            // Send user to correct domain if page is from other site
            String url = content.getUrl();
            Site site = siteCache.getSiteById(content.getAssociation().getSiteId());
            List<String> hostnames = site.getHostnames();
            if (hostnames.size() > 0) {
                String hostname = IDN.toASCII(hostnames.get(0));
                int port = request.getServerPort();
                String scheme = site.getScheme();
                if (scheme == null) {
                    scheme = request.getScheme();
                }
                if ("GET".equalsIgnoreCase(request.getMethod())) {
                    url = createRedirectUrlWithIncomingParameters(request, url);
                }

                url = scheme + "://" + hostname + (port != 80 && port != 443 ? ":" + port : "") + url;
                response.sendRedirect(url);
                return true;
            }
        }
        return false;
    }

    @Autowired
    public void setContentRequestDispatcher(ContentRequestDispatcher contentRequestDispatcher) {
        this.contentRequestDispatcher = contentRequestDispatcher;
    }


    @Autowired
    public void setSiteCache(SiteCache siteCache) {
        this.siteCache = siteCache;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    private String createRedirectUrlWithIncomingParameters(HttpServletRequest request, String url) {
        String params = HttpHelper.createQueryStringFromRequestParameters(request);
        if (params.length() > 0) {
            if (url.contains("?")) {
                url = url + "&" + params;
            } else {
                url = url + "?" + params;
            }
        }
        return url;
    }

    public void setAddPagetypeToResponseHeader(boolean addPagetypeToResponseHeader) {
        this.addPagetypeToResponseHeader = addPagetypeToResponseHeader;
    }
}
