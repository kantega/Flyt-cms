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
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.util.RequestHelper;
import no.kantega.publishing.common.util.CharResponseWrapper;
import no.kantega.publishing.common.util.URLRewriter;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentRequestListener;
import no.kantega.commons.log.Log;
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.HttpHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;
import org.kantega.jexmec.PluginManager;

/**
 *
 */
public class ContentRequestHandler extends AbstractController {
    private static String SOURCE = "ContentRequestHandler";


    private PluginManager<OpenAksessPlugin> pluginManager;

    private SiteCache siteCache;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        long start = new Date().getTime();

        boolean isAdminMode = HttpHelper.isAdminMode(request);

        try {
            ContentManagementService cms = new ContentManagementService(request);

            Site currentSite = siteCache.getSiteByHostname(request.getServerName());

            ContentIdentifier cid;
            String originalUri = (String)request.getAttribute("javax.servlet.error.request_uri");
            if (originalUri == null) {
                // Direkte kall
                cid = new ContentIdentifier(request);
            } else {
                // Kall via 404 handler
                // request_uri inneholder også subsite info (http://angel/buffy, må fjerne dette
                String contextPath = Aksess.getContextPath();
                if (contextPath != null && contextPath.length() != 0 && originalUri.indexOf(contextPath) != -1) {
                    originalUri = originalUri.substring(contextPath.length(), originalUri.length());
                }
                cid = new ContentIdentifier(request, originalUri);
                response.setStatus(HttpServletResponse.SC_OK);
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

                    int siteId = content.getAssociation().getSiteId();
                    Site site = siteCache.getSiteById(siteId);
                    String alias = site.getAlias();

                    RequestHelper.setRequestAttributes(request, content);

                    if (content.getType() == ContentType.PAGE) {
                        String template;
                        DisplayTemplate dt = cms.getDisplayTemplate(content.getDisplayTemplateId());
                        if (dt != null) {
                            template = dt.getView();
                        } else {
                            template = Aksess.getStartPage();
                        }

                        // If template filename contains macro $SITE, replace with correct site
                        if (template.indexOf("$SITE") != -1) {
                            template = template.replaceAll("\\$SITE", alias.substring(0, alias.length() - 1));
                        }
                        response.addHeader("X-Powered-By", "Aksess Publisering " + Aksess.getVersion());

                        // Run template controllers
                        RequestHelper.runTemplateControllers(dt, request, response, getServletContext());

                        // Check if we need to filter URLs - unable to rewrite using filter if call is via 404 (alias)
                        CharResponseWrapper wrapper = null;
                        if (shouldFilterOutput(isAdminMode, originalUri)) {
                            // Rewrite URLs
                            wrapper = new CharResponseWrapper(response);
                            response = wrapper;
                        }

                        for(OpenAksessPlugin plugin : pluginManager.getPlugins()) {
                            for(ContentRequestListener listener : plugin.getContentRequestListeners()) {
                                listener.beforeDisplayTemplateDispatch(new DefaultDispatchContext(request, response, template));
                            }
                        }
                        // Forward request
                        request.getRequestDispatcher(template).forward(request, response);

                        if(shouldFilterOutput(isAdminMode, originalUri)) {
                            // Write output
                            if (wrapper.isWrapped()) {
                                String result  = URLRewriter.rewriteURLs(request, wrapper.toString());
                                PrintWriter out = response.getWriter();
                                out.write(result);
                                out.flush();
                            }
                        }

                        long end = new Date().getTime();
                        Log.info(SOURCE, "Tidsforbruk:" + (end-start) + " ms (" + content.getTitle() + ", id: " + content.getId() + ", template:" + template + ")", null, null);
                    } else {
                        if (isAdminMode) {
                            request.getRequestDispatcher("/admin/showcontentinframe.jsp").forward(request, response);
                        } else {
                            String url = content.getLocation();
                            if (url != null && url.length() > 0) {
                                if (content.getType() == ContentType.FILE) {
                                    request.setAttribute("attachment-id", url);
                                    request.getRequestDispatcher("/attachment.ap").forward(request, response);
                                } else  if (content.getType() == ContentType.FORM) {
                                    response.sendRedirect(Aksess.getContextPath() + "/forms/flow?_flowId=fillform&formId=" + url);
                                } else {
                                    String redirector = Aksess.getCustomUrlRedirector();
                                    if (content.isExternalLink() && redirector != null && redirector.length() > 0) {
                                        // Use a custom redirect page for external links
                                        request.setAttribute("url", url);
                                        request.getRequestDispatcher(redirector).forward(request, response);
                                    } else {
                                        if (url.charAt(0) == '/') {
                                            url = Aksess.getContextPath() + url;
                                        }
                                        response.sendRedirect(url);
                                    }

                                }
                            } else {
                                throw new ContentNotFoundException("", SOURCE);
                            }
                        }
                    }


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

    private boolean shouldFilterOutput(boolean adminMode, String originalUri) {
        return !adminMode && Aksess.isUrlRewritingEnabled() && originalUri != null;
    }

    @Autowired
    public void setPluginManager(PluginManager<OpenAksessPlugin> pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Autowired
    public void setSiteCache(SiteCache siteCache) {
        this.siteCache = siteCache;
    }

    class DefaultDispatchContext implements ContentRequestListener.DispatchContext {
        private HttpServletRequest request;
        private HttpServletResponse response;
        private String templateUrl;

        DefaultDispatchContext(HttpServletRequest request, HttpServletResponse response, String templateUrl) {
            this.request = request;
            this.response = response;
            this.templateUrl = templateUrl;
        }

        public HttpServletRequest getRequest() {
            return request;
        }

        public HttpServletResponse getResponse() {
            return response;
        }

        public String getTemplateUrl() {
            return templateUrl;
        }
    }
}
