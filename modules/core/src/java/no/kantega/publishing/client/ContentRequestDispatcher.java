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

import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.api.content.ContentRequestListener;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.cache.DisplayTemplateCache;
import no.kantega.publishing.common.data.DisplayTemplate;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.util.CharResponseWrapper;
import no.kantega.publishing.common.util.RequestHelper;
import no.kantega.publishing.common.util.URLRewriter;
import no.kantega.commons.log.Log;
import no.kantega.commons.util.HttpHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.kantega.jexmec.PluginManager;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.util.Date;

/**
 *
 */
public class ContentRequestDispatcher {
    private PluginManager<OpenAksessPlugin> pluginManager;

    private SiteCache siteCache;

    public void dispatchContentRequest(Content content, ServletContext servletContext, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String originalUri = (String)request.getAttribute("javax.servlet.error.request_uri");

        int siteId = content.getAssociation().getSiteId();
        Site site = siteCache.getSiteById(siteId);
        String alias = site.getAlias();

        boolean adminMode = HttpHelper.isAdminMode(request);

        RequestHelper.setRequestAttributes(request, content);

        if (content.getType() == ContentType.PAGE) {
            String template;
            DisplayTemplate dt = DisplayTemplateCache.getTemplateById(content.getDisplayTemplateId());
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
                    listener.beforeDisplayTemplateDispatch(new DefaultDispatchContext(request, response, template));
                }
            }
            // Forward request
            request.getRequestDispatcher(template).forward(request, response);

            if(shouldFilterOutput(adminMode, originalUri)) {
                // Write output
                if (wrappedResponse.isWrapped()) {
                    String result  = URLRewriter.rewriteURLs(request, wrappedResponse.toString());
                    PrintWriter out = originalResponse.getWriter();
                    out.write(result);
                    out.flush();
                }
            }

        } else {
            if (adminMode) {
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
                    throw new ContentNotFoundException("", this.getClass().getName());
                }
            }
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

}
