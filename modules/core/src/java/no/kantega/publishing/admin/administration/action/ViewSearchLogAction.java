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

package no.kantega.publishing.admin.administration.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.ao.SearchAO;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 *
 */
public class ViewSearchLogAction extends AbstractController {
    private SiteCache siteCache;
    private String view;

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();

        List<Site> sites = siteCache.getSites();

        RequestParameters param = new RequestParameters(request, "utf-8");
        int siteId = param.getInt("siteId");
        if (siteId == -1) {
            if (sites.size() > 0) {
                siteId = sites.get(0).getId();
            }
        }

        Calendar cal = new GregorianCalendar();
        Date now = cal.getTime();
        cal.add(Calendar.MINUTE, -30);
        Date thirtyMinutesAgo = cal.getTime();

        model.put("last30min", SearchAO.getSearchCountForPeriod(thirtyMinutesAgo, now, siteId));

        cal = new GregorianCalendar();
        cal.add(Calendar.MONTH, -1);
        Date oneMonthAgo = cal.getTime();
        model.put("sumLastMonth", SearchAO.getSearchCountForPeriod(oneMonthAgo, now, siteId));

        List mostPopular = SearchAO.getMostPopularQueries(siteId);
        model.put("most", mostPopular);
        List leastHits = SearchAO.getQueriesWithLeastHits(siteId);
        model.put("least", leastHits);

        model.put("sites", sites);
        model.put("selectedSiteId", siteId);

        return new ModelAndView(view, model);
    }

    public void setSiteCache(SiteCache siteCache) {
        this.siteCache = siteCache;
    }

    public void setView(String view) {
        this.view = view;
    }
}
