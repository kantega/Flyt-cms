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

package no.kantega.openaksess.search.searchlog.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.openaksess.search.searchlog.dao.SearchLogDao;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.model.Site;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class ViewSearchLogAction extends AbstractController {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private SiteCache siteCache;
    private String view;
    @Autowired
    private SearchLogDao searchLogDao;

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<>();

        List<Site> sites = siteCache.getSites();

        RequestParameters param = new RequestParameters(request, "utf-8");
        int siteId = getSiteId(sites, param);

        String fromDateParam = param.getString("fromdate");
        String toDateParam = param.getString("todate");
        if("".equals(toDateParam)) toDateParam = null;
        Integer numberOfRows = Integer.parseInt(defaultIfBlank(param.getString("numberofRows"), "100"));

        if(isBlank(fromDateParam) && isBlank(toDateParam)){
            defaultSearch(model, siteId);
        } else {
            dateSpecifiedSearch(model, siteId, fromDateParam, toDateParam, numberOfRows);
        }

        model.put("numberofRows", numberOfRows);
        model.put("sites", sites);
        model.put("selectedSiteId", siteId);

        return new ModelAndView(view, model);
    }

    private void dateSpecifiedSearch(Map<String, Object> model, int siteId, String fromDateParam, String toDateParam, Integer numberOfRows) {
        LocalDateTime fromDate = getFromDate(fromDateParam).atTime(0, 0);
        LocalDateTime toDate = getToDate(toDateParam).atTime(23, 59, 59, 999);

        model.put("numSearches", searchLogDao.getSearchCountForPeriod(fromDate, toDate, siteId));
        model.put("most", searchLogDao.getMostPopularQueries(siteId, fromDate, toDate, numberOfRows));
        model.put("least", searchLogDao.getQueriesWithLeastHits(siteId, fromDate, toDate, numberOfRows));

        model.put("startDate", fromDate.format(formatter));
        model.put("endDate", toDate.format(formatter));
    }

    private LocalDate getFromDate(String fromDateParam) {
        return java.util.Optional.ofNullable(defaultIfBlank(fromDateParam, null))
                .map(s -> LocalDate.parse(s, formatter))
                .orElseGet(() -> LocalDate.now().minusMonths(1));
    }

    private LocalDate getToDate(String toDateParam) {
        return java.util.Optional.ofNullable(defaultIfBlank(toDateParam, null))
                .map(s -> LocalDate.parse(s, formatter))
                .orElseGet(LocalDate::now);
    }

    private void defaultSearch(Map<String, Object> model, int siteId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyMinutesAgo = now.minusMinutes(30);
        model.put("last30min", searchLogDao.getSearchCountForPeriod(thirtyMinutesAgo, now, siteId));

        LocalDateTime oneMonthAgo = now.minusDays(30);
        model.put("sumLastMonth", searchLogDao.getSearchCountForPeriod(oneMonthAgo, now, siteId));

        model.put("most", searchLogDao.getMostPopularQueries(siteId, 100));
        model.put("least", searchLogDao.getQueriesWithLeastHits(siteId, 100));
    }

    private int getSiteId(List<Site> sites, RequestParameters param) {
        int siteId = param.getInt("siteId");
        if (siteId == -1) {
            if (sites.size() > 0) {
                siteId = sites.get(0).getId();
            }
        }
        return siteId;
    }

    public void setSiteCache(SiteCache siteCache) {
        this.siteCache = siteCache;
    }

    public void setView(String view) {
        this.view = view;
    }
}
