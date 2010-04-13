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

import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.util.RequestHelper;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.commons.log.Log;
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.HttpHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.util.*;

import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Receives all incoming request for content, fetches from database and sends request to a dispatcher
 */
public class ContentRequestHandler extends AbstractController {
    private static String SOURCE = "ContentRequestHandler";

    private SiteCache siteCache;
    private ContentRequestDispatcher contentRequestDispatcher;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        long start = new Date().getTime();

        boolean isAdminMode = HttpHelper.isAdminMode(request);

        // Force getting a session since Tomcat does not always create a session
        request.getSession(true);

        try {
            ContentManagementService cms = new ContentManagementService(request);

            Site currentSite = siteCache.getSiteByHostname(request.getServerName());

            ContentIdentifier cid;
            String originalUri = (String)request.getAttribute("javax.servlet.error.request_uri");
            if (originalUri == null) {
                // Direct call
                cid = new ContentIdentifier(request);
            } else {
                // Called via 404 mechanism, eg. could be a page alias
                // request_uri contains contextpath, must remove contextpath
                String contextPath = Aksess.getContextPath();
                if (contextPath != null && contextPath.length() != 0 && originalUri.indexOf(contextPath) != -1) {
                    originalUri = originalUri.substring(contextPath.length(), originalUri.length());
                }
                cid = new ContentIdentifier(request, originalUri);
                response.setStatus(HttpServletResponse.SC_OK);

                if (request instanceof MultipartHttpServletRequest) {
                    Log.error(SOURCE, "multipart/form-data forms cannot post to aliases. Use contentId=${aksess_this.id} in form action", null, null);
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
                        throw new ContentNotFoundException("", SOURCE);
                    }
                    if(!isAdminMode && content.getAssociation().getSiteId() != currentSite.getId()) {
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
                            url = scheme + "://" + hostname + (port != 80 && port != 443 ? ":" + port : "") + url;
                            response.sendRedirect(url);
                            return null;
                        }
                    }
                    if (isAdminMode) {
                        response.setDateHeader("Expires", 0);                        
                    }
                    contentRequestDispatcher.dispatchContentRequest(content, getServletContext(), request, response);
                    long end = new Date().getTime();
                    Log.info(this.getClass().getName(), "Tidsforbruk:" + (end- start) + " ms (" + content.getTitle() + ", id: " + content.getId() + ", template:" + content.getDisplayTemplateId() + ")", null, null);
                } else {
                    throw new ContentNotFoundException(SOURCE, "");
                }
            } catch (NotAuthorizedException e) {
                // Check if user is logged in
                SecuritySession secSession = SecuritySession.getInstance(request);
                if (secSession.isLoggedIn()) {
                    RequestHelper.setRequestAttributes(request, null);
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
                Log.error(SOURCE, e1, null, null);
            }
            request.getRequestDispatcher("/404.jsp").forward(request, response);
        } catch (Throwable e) {
            if (e instanceof ServletException) {
                ServletException sex = (ServletException)e;
                if (sex.getRootCause() != null) {
                    e = sex.getRootCause();
                }
            }
            Log.error(SOURCE, e, null, null);
            throw new ServletException(e);
        }
        return null;
    }


    @Autowired
    public void setContentRequestDispatcher(ContentRequestDispatcher contentRequestDispatcher) {
        this.contentRequestDispatcher = contentRequestDispatcher;
    }


    @Autowired
    public void setSiteCache(SiteCache siteCache) {
        this.siteCache = siteCache;
    }
}
