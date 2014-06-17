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

package no.kantega.publishing.common.util;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.Language;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.DisplayTemplate;
import no.kantega.publishing.common.data.DisplayTemplateControllerId;
import no.kantega.publishing.controls.AksessController;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class RequestHelper {
    private static SiteCache siteCache;
    private static BeanFactory beanFactory;

    public static void setRequestAttributes(HttpServletRequest request, Content content) throws SystemException {
        setSiteCacheIfNull(request);
        if (content == null) {
            Site site = siteCache.getSiteByHostname(request.getServerName());
            if (site != null){
                String alias = site.getAlias();
                request.setAttribute("aksess_site", alias);
            }

        } else {
            int siteId = content.getAssociation().getSiteId();
            Site site = siteCache.getSiteById(siteId);
            String alias = site.getAlias();
            request.setAttribute("aksess_locale", (Language.getLanguageAsLocale(content.getLanguage())));
            request.setAttribute("aksess_language", content.getLanguage());
            request.setAttribute("aksess_site", alias);
            request.setAttribute("aksess_this", content);
        }
    }

    public static void runTemplateControllers(DisplayTemplate dt, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {

        Map<String, Object> model = new HashMap<>();

        setBeanFactoryIfNull(servletContext);

        // Run all controllers
        if(dt.getControllers() != null) {
            for (DisplayTemplateControllerId displayTemplateController : dt.getControllers()) {
                AksessController aksessController = beanFactory.getBean(displayTemplateController.getId(), AksessController.class);
                model.putAll(aksessController.handleRequest(request, response));
            }
        }

        // Put the model on the request as request attributes
        for (Map.Entry<String, Object> o : model.entrySet()) {
            request.setAttribute(o.getKey(), o.getValue());
        }
    }

    private static void setBeanFactoryIfNull(ServletContext servletContext) {
        if (beanFactory == null) {
            beanFactory = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        }
    }

    private static void setSiteCacheIfNull(HttpServletRequest request) {
        if(siteCache == null){
            siteCache = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext()).getBean(SiteCache.class);
        }
    }
}
