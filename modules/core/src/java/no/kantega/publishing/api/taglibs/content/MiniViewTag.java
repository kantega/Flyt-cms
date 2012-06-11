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

package no.kantega.publishing.api.taglibs.content;

import no.kantega.commons.log.Log;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import no.kantega.publishing.api.requestlisteners.ContentRequestListener;
import no.kantega.publishing.client.DefaultDispatchContext;
import no.kantega.publishing.client.device.DeviceCategoryDetector;
import no.kantega.publishing.common.cache.DisplayTemplateCache;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.DisplayTemplate;
import no.kantega.publishing.common.util.RequestHelper;
import no.kantega.publishing.common.util.TemplateMacroHelper;
import no.kantega.publishing.spring.RootContext;
import org.kantega.jexmec.PluginManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class MiniViewTag extends TagSupport {
    private static final String SOURCE = "aksess.MiniViewTag";

    private String collection = null;

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public int doStartTag() throws JspException {
        HttpServletRequest request   = (HttpServletRequest)pageContext.getRequest();
        PluginManager<OpenAksessPlugin> pluginManager = (PluginManager<OpenAksessPlugin>) RootContext.getInstance().getBean("pluginManager", PluginManager.class);
        SiteCache siteCache = (SiteCache) RootContext.getInstance().getBean("aksessSiteCache", SiteCache.class);
        DeviceCategoryDetector deviceCategoryDetector = new DeviceCategoryDetector();

        try {
            Content currentPage = (Content)request.getAttribute("aksess_this");
            Content content = (Content)pageContext.getAttribute("aksess_collection_" + collection);

            if (content != null) {
                String template = null;
                DisplayTemplate dt = DisplayTemplateCache.getTemplateById(content.getDisplayTemplateId());
                if (dt != null) {
                    template = dt.getMiniView();
                }
                if (template != null && template.length() > 0) {
                    if (TemplateMacroHelper.containsMacro(template)) {
                        int siteId = currentPage.getAssociation().getSiteId();
                        Site site = siteCache.getSiteById(siteId);

                        template = TemplateMacroHelper.replaceMacros(template, site, deviceCategoryDetector.getUserAgentDeviceCategory(request));
                    }

                    request.setAttribute("aksess_containingPage", currentPage);

                    // Ved å legge content på request'en med navn aksess_this vil malen kunne bruke standard tagger
                    RequestHelper.setRequestAttributes(request, content);
                    try {
                        for(OpenAksessPlugin plugin : pluginManager.getPlugins()) {
                            for(ContentRequestListener listener : plugin.getContentRequestListeners()) {
                                listener.beforeMiniviewDispatch(new DefaultDispatchContext((HttpServletRequest)pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse(), template));
                            }
                        }
                        pageContext.include(template);
                    } catch (Exception e) {
                        Log.error(SOURCE, "Unable to display miniview for: " + content.getTitle(), null, null);
                        Log.error(SOURCE, e, null, null);
                    }

                    // Sett tilbake til denne siden
                    RequestHelper.setRequestAttributes(request, currentPage);

                    request.removeAttribute("aksess_containingPage");
                }
            }
        } catch (Exception e) {
            Log.error(SOURCE, e, null, null);
            throw new JspException("Error in miniview");
        }

        return SKIP_BODY;
    }


    public int doEndTag() throws JspException {
        collection = null;

        return EVAL_PAGE;
    }
}
