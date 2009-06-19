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

package no.kantega.publishing.admin.searchlog.action;

import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.common.data.Site;
import no.kantega.publishing.common.ao.SearchAO;
import no.kantega.publishing.api.cache.SiteCache;

/**
 *
 */
public class ViewSearchLogAction extends AbstractController {
    private SiteCache siteCache;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();

        List sites = siteCache.getSites();

        RequestParameters param = new RequestParameters(request, "utf-8");
        int siteId = param.getInt("siteId");
        if (siteId == -1) {
            if (sites.size() > 0) {
                siteId = ((Site)sites.get(0)).getId();
            }
        }

        Calendar cal = new GregorianCalendar();
        Date now = cal.getTime();
        cal.add(Calendar.MINUTE, -30);
        Date before = cal.getTime();

        model.put("last30min", SearchAO.getSearchCountForPeriod(before, now, siteId));
        model.put("sumAllTime", SearchAO.getSearchCountForPeriod(null,null, siteId));

        List mostPopular  = SearchAO.getMostPopularQueries(siteId);
        model.put("most", mostPopular);
        List leastHits= SearchAO.getQueriesWithLeastHits(siteId);
        model.put("least", leastHits);


        model.put("sites", sites);
        model.put("selectedSiteId", siteId);

        return new ModelAndView("/WEB-INF/jsp/admin/searchlog/viewsearchlog.jsp", model);
    }

    public void setSiteCache(SiteCache siteCache) {
        this.siteCache = siteCache;
    }
}
