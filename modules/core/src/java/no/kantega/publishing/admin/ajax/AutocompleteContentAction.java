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

package no.kantega.publishing.admin.ajax;

import no.kantega.commons.util.StringHelper;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.data.SortOrder;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.service.ContentManagementService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutocompleteContentAction implements Controller {

    private SiteCache siteCache;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<>();
        String title = request.getParameter("term");
        String contentTemplateFilter = request.getParameter("contentTemplate");

        if (title != null && title.trim().length() > 0) {
            ContentQuery query = new ContentQuery();
            query.setKeyword(title + '%');
            if (contentTemplateFilter != null && !contentTemplateFilter.isEmpty() && contentTemplateFilter != "-1"){
                query.setContentTemplate(contentTemplateFilter);
            }
            ContentManagementService cms = new ContentManagementService(request);

            List<Content> pages = cms.getContentList(query, 100, new SortOrder(ContentProperty.TITLE, false));

            // Add site name and replace illegal charachters.
            for (Content page : pages) {
                String pageTitle = page.getTitle();
                if (siteCache.getSites().size() > 1) {
                    Site site = siteCache.getSiteById(page.getAssociation().getSiteId());
                    pageTitle += " (" + site.getName() + ")";
                }
                pageTitle = StringHelper.removeIllegalCharsInTitle(pageTitle);
                page.setTitle(pageTitle);
            }

            model.put("contentlist", pages);
            if (request.getParameter("useContentId") != null) {
                model.put("useContentId", Boolean.TRUE);
            }
        }

        return new ModelAndView("/WEB-INF/jsp/ajax/searchresult-content.jsp", model);
    }

    public void setSiteCache(SiteCache siteCache) {
        this.siteCache = siteCache;
    }
}
