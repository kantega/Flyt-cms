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

import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import no.kantega.publishing.api.requestlisteners.ContentRequestListener;
import no.kantega.publishing.api.taglibs.content.util.AttributeTagHelper;
import no.kantega.publishing.client.DefaultDispatchContext;
import no.kantega.publishing.client.device.DeviceCategoryDetector;
import no.kantega.publishing.common.cache.DisplayTemplateCache;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.DisplayTemplate;
import no.kantega.publishing.common.util.RequestHelper;
import no.kantega.publishing.common.util.TemplateMacroHelper;
import org.kantega.jexmec.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import static no.kantega.publishing.api.ContentUtil.tryGetFromRequest;

public class MiniViewTag extends TagSupport {
    private static final Logger log = LoggerFactory.getLogger(MiniViewTag.class);

    private String collection = null;
    private String contentId = null;
    private Content contentObject;
    private static PluginManager<OpenAksessPlugin> pluginManager;
    private static SiteCache siteCache;

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public void setObj(Content obj) {
        this.contentObject = obj;
    }

    public void setContentid(String contentId) {
        this.contentId = contentId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setPageContext(PageContext pageContext) {
        super.setPageContext(pageContext);
        if (pluginManager == null) {
            WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext());
            pluginManager = (PluginManager<OpenAksessPlugin>) context.getBean("pluginManager", PluginManager.class);
            siteCache = context.getBean("aksessSiteCache", SiteCache.class);
        }
    }

    public int doStartTag() throws JspException {
        HttpServletRequest request   = (HttpServletRequest)pageContext.getRequest();

        DeviceCategoryDetector deviceCategoryDetector = new DeviceCategoryDetector();

        try {
            Content currentPage = tryGetFromRequest(request);

            if (contentObject == null) {
                contentObject = AttributeTagHelper.getContent(pageContext, collection, contentId, null);
            }

            if (contentObject != null) {

                String template = null;
                DisplayTemplate dt = DisplayTemplateCache.getTemplateById(contentObject.getDisplayTemplateId());
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
                    RequestHelper.setRequestAttributes(request, contentObject);
                    try {
                        for(OpenAksessPlugin plugin : pluginManager.getPlugins()) {
                            for(ContentRequestListener listener : plugin.getContentRequestListeners()) {
                                listener.beforeMiniviewDispatch(new DefaultDispatchContext((HttpServletRequest)pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse(), template));
                            }
                        }
                        pageContext.include(template);
                    } catch (Exception e) {
                        log.error( "Unable to display miniview for: " + contentObject.getTitle(), e);
                    }

                    // Sett tilbake til denne siden
                    RequestHelper.setRequestAttributes(request, currentPage);

                    request.removeAttribute("aksess_containingPage");
                }
            }
        } catch (Exception e) {
            log.error("Error displaying miniview", e);
            throw new JspException("Error in miniview");
        }

        return SKIP_BODY;
    }


    public int doEndTag() throws JspException {
        collection = null;
        contentId = null;
        contentObject = null;

        return EVAL_PAGE;
    }
}
