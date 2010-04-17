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
import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.api.model.Site;
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
        Map model = new HashMap();
        RequestParameters param = new RequestParameters(request);
        String title = param.getString("q");
        if (title != null && title.length() >= 3) {
            ContentQuery query = new ContentQuery();
            query.setKeyword(title + '%');
            ContentManagementService cms = new ContentManagementService(request);

            List pages = cms.getContentList(query, 100, new SortOrder(ContentProperty.TITLE, false));

            // Legg inn sitenavn slik at det blir enklere å finne siden og erstatt ulovlige tegn
            for (int i = 0; i < pages.size(); i++) {
                Content c =  (Content)pages.get(i);
                Site s = siteCache.getSiteById(c.getAssociation().getSiteId());
                String pageTitle = c.getTitle() + " (" + s.getName()  + ")";
                pageTitle = StringHelper.removeIllegalCharsInTitle(pageTitle);
                c.setTitle(pageTitle);
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
