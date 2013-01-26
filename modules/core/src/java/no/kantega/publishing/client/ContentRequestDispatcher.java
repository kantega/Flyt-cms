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

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.HttpHelper;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import no.kantega.publishing.api.requestlisteners.ContentRequestListener;
import no.kantega.publishing.client.device.DeviceCategory;
import no.kantega.publishing.client.device.DeviceCategoryDetector;
import no.kantega.publishing.client.filter.UrlContentRewriter;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.cache.DisplayTemplateCache;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.DisplayTemplate;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.util.CharResponseWrapper;
import no.kantega.publishing.common.util.RequestHelper;
import no.kantega.publishing.common.util.TemplateMacroHelper;
import org.kantega.jexmec.PluginManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 */
public class ContentRequestDispatcher {
    private PluginManager<OpenAksessPlugin> pluginManager;

    private SiteCache siteCache;
    private UrlContentRewriter urlRewriter;
    private DeviceCategoryDetector deviceCategoryDetector = new DeviceCategoryDetector();

    public void dispatchContentRequest(Content content, ServletContext servletContext, HttpServletRequest request, HttpServletResponse response) throws Exception {

        boolean adminMode = HttpHelper.isAdminMode(request);

        RequestHelper.setRequestAttributes(request, content);

        if (content.getType() == ContentType.PAGE) {
            dispatchPage(content, servletContext, request, response, adminMode);
        } else {
            if (adminMode) {
                dispatchAdminMode(content, request, response);
            } else {
                dispatchNonPage(content, request, response);
            }
        }
    }

    private void dispatchNonPage(Content content, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ContentNotFoundException {
        String url = content.getLocation();
        if (url != null && url.length() > 0) {
            if (content.getType() == ContentType.FILE) {
                request.setAttribute("attachment-id", url);
                request.getRequestDispatcher("/attachment.ap").forward(request, response);
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
                    // Rewrite URL if necessary (may be in form /content.ap?xxx should change to alias
                    url = urlRewriter.rewriteContent(request, url);

                    response.sendRedirect(url);
                }

            }
        } else {
            throw new ContentNotFoundException("", this.getClass().getName());
        }
    }

    private void dispatchPage(Content content, ServletContext servletContext, HttpServletRequest request, HttpServletResponse response, boolean adminMode) throws Exception {
        int siteId = content.getAssociation().getSiteId();

        String originalUri = (String)request.getAttribute("javax.servlet.error.request_uri");

        DisplayTemplate dt = DisplayTemplateCache.getTemplateById(content.getDisplayTemplateId());
        if (dt == null) {
            throw new SystemException("DisplayTemplate not found. Check if displaytemplate with databaseid=" + content.getDisplayTemplateId() + " has been deleted from aksess-templateconfig.xml", getClass().getName(), null);
        }

        DeviceCategory deviceCategory = deviceCategoryDetector.getUserAgentDeviceCategory(request);

        String view = getView(siteId, dt, deviceCategory);

        response.addHeader("X-Powered-By", "OpenAksess " + Aksess.getVersion());

        // Run template controllers
        RequestHelper.runTemplateControllers(dt, request, response, servletContext);

        // Check if we need to filter URLs - unable to rewrite using filter if call is via 404 (alias)
        CharResponseWrapper wrappedResponse = null;
        HttpServletResponse originalResponse = response;

        if (shouldFilterOutput(adminMode, originalUri)) {
            // Rewrite URLs
            wrappedResponse = new CharResponseWrapper(response);
            response = wrappedResponse;
        }

        for(OpenAksessPlugin plugin : pluginManager.getPlugins()) {
            for(ContentRequestListener listener : plugin.getContentRequestListeners()) {
                listener.beforeDisplayTemplateDispatch(new DefaultDispatchContext(request, response, view));
            }
        }

        // Only forward to view template if contentrequestlistener hasn't commited the response yet
        if(!response.isCommitted()) {
            // Forward request
            request.getRequestDispatcher(view).forward(request, response);

        }
        if(shouldFilterOutput(adminMode, originalUri)) {
            // Write output
            if (wrappedResponse.isWrapped()) {
                String result = urlRewriter.rewriteContent(request,wrappedResponse.toString());
                PrintWriter out = originalResponse.getWriter();
                out.write(result);
                out.flush();
            }
        }
    }

    private String getView(int siteId, DisplayTemplate dt, DeviceCategory deviceCategory) {
        Site site = siteCache.getSiteById(siteId);
        return TemplateMacroHelper.replaceMacros(dt.getView(), site, deviceCategory);
    }

    private void dispatchAdminMode(Content content, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (content.getType() == ContentType.FILE) {
            request.getRequestDispatcher("/admin/file.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/admin/link.jsp").forward(request, response);
        }
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

    @Autowired
    public void setUrlRewriter(UrlContentRewriter urlRewriter) {
        this.urlRewriter = urlRewriter;
    }
}
