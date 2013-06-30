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

import com.yammer.metrics.annotation.Metered;
import com.yammer.metrics.annotation.Timed;
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.HttpHelper;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.Aksess;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.MultipartFilter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Receives all incoming request for content, fetches from database and sends request to a dispatcher
 */
public class ContentRequestHandler extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(ContentRequestHandler.class);

    private SiteCache siteCache;
    private ContentRequestDispatcher contentRequestDispatcher;
    @Autowired
    private ContentIdHelper contentIdHelper;

    @Metered
    @Timed
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        long start = System.currentTimeMillis();

        boolean isAdminMode = HttpHelper.isAdminMode(request);

        // Force getting a session since Tomcat does not always create a session
        request.getSession(true);

        try {
            ContentManagementService cms = new ContentManagementService(request);

            ContentIdentifier cid;
            String originalUri = (String)request.getAttribute("javax.servlet.error.request_uri");
            if (originalUri == null) {
                // Direct call
                cid = contentIdHelper.fromRequest(request);
            } else {
                // Called via 404 mechanism, eg. could be a page alias
                // request_uri contains contextpath, must remove contextpath
                String contextPath = Aksess.getContextPath();
                if (isNotBlank(contextPath) && originalUri.contains(contextPath)) {
                    originalUri = originalUri.substring(contextPath.length(), originalUri.length());
                }
                cid = contentIdHelper.fromRequestAndUrl(request, originalUri);
                response.setStatus(HttpServletResponse.SC_OK);

                if (request.getMethod().toLowerCase().equals("post") && (request instanceof MultipartHttpServletRequest || request.getAttribute("MultipartFilter" + MultipartFilter.ALREADY_FILTERED_SUFFIX) != null)) {
                    log.error( "multipart/form-data forms cannot post to aliases. Use contentId=${aksess_this.id} in form action");
                }
            }

            if("hearing".equals(request.getParameter("status"))) {
                cid.setStatus(ContentStatus.HEARING);
            }
            try {
                Content content = cms.getContent(cid, true);
                if (content != null) {
                    // Send NOT_FOUND if expired or not published
                    if(!isAdminMode && (content.getVisibilityStatus() != ContentVisibilityStatus.ACTIVE && content.getVisibilityStatus() != ContentVisibilityStatus.ARCHIVED)) {
                        throw new ContentNotFoundException(cid.toString());
                    }
                    if (redirectToCorrectSiteIfOtherSite(request, response, isAdminMode, content)){
                        return null;
                    }
                    if (isAdminMode) {
                        response.setDateHeader("Expires", 0);
                    }
                    contentRequestDispatcher.dispatchContentRequest(content, getServletContext(), request, response);
                    logTimeSpent(start, content);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    throw new ContentNotFoundException(cid.toString());
                }
            } catch (NotAuthorizedException e) {
                // Check if user is logged in
                SecuritySession secSession = SecuritySession.getInstance(request);
                if (secSession.isLoggedIn()) {
                    RequestHelper.setRequestAttributes(request, null);
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    request.getRequestDispatcher("/403.jsp").forward(request, response);
                } else {
                    // Start login process (redirect)
                    secSession.initiateLogin(request, response);
                }

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
            log.error( request.getRequestURI());
            log.error("", e);
            throw new ServletException(e);
        }
        return null;
    }

    private void logTimeSpent(long start, Content content) {
        long end = System.currentTimeMillis();
        StringBuilder message = new StringBuilder("Execution time: ");
        message.append((end - start));
        message.append(" ms (");
        message.append(content.getTitle());
        message.append(", id: ");
        message.append(content.getId());
        message.append(", template:");
        message.append(content.getDisplayTemplateId());
        message.append(")");
        log.info( message.toString());
    }

    private boolean redirectToCorrectSiteIfOtherSite(HttpServletRequest request, HttpServletResponse response, boolean adminMode, Content content) throws IOException {
        Site currentSite = siteCache.getSiteByHostname(request.getServerName());
        if(currentSite != null && !adminMode && content.getAssociation().getSiteId() != currentSite.getId()) {
            // Send user to correct domain if page is from other site
            String url = content.getUrl();
            Site site = siteCache.getSiteById(content.getAssociation().getSiteId());
            List hostnames = site.getHostnames();
            if (hostnames.size() > 0) {
                String hostname = (String)hostnames.get(0);
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
}
