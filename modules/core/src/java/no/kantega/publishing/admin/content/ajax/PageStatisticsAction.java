/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.content.ajax;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.admin.viewcontroller.SimpleAdminController;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ContentIdHelper;
import no.kantega.publishing.common.ao.TrafficLogDao;
import no.kantega.publishing.common.data.TrafficLogQuery;
import no.kantega.publishing.common.data.enums.TrafficOrigin;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class PageStatisticsAction extends SimpleAdminController {
    @Autowired
    TrafficLogDao trafficLogDao;

    private boolean totalStatsEnabled;

    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        RequestParameters params = new RequestParameters(request);

        String url = params.getString(AdminRequestParameters.ITEM_IDENTIFIER);

        Map<String, Object> model = new HashMap<String, Object>();

        model.put("totalStatsEnabled", totalStatsEnabled);

        // Extracting currently selected content from it's url
        ContentIdentifier cid = null;
        if (!"".equals(url)) {
            try {
                cid = ContentIdHelper.fromRequestAndUrl(request, url);

                int intHits = -1;
                int extHits = -1;
                int sumHits = -1;

                int intSessions = -1;
                int extSessions = -1;
                int sumSessions = -1;

                TrafficLogQuery query = new TrafficLogQuery();
                query.setCid(cid);

                if (Aksess.getInternalIpSegment() != null) {
                    // Distinguish between internal and external hits
                    query.setTrafficOrigin(TrafficOrigin.INTERNAL);
                    intHits = trafficLogDao.getNumberOfHitsOrSessionsInPeriod(query, false);

                    query.setTrafficOrigin(TrafficOrigin.EXTERNAL);
                    extHits = trafficLogDao.getNumberOfHitsOrSessionsInPeriod(query, false);
                    sumHits = intHits + extHits;

                    query.setTrafficOrigin(TrafficOrigin.INTERNAL);
                    intSessions = trafficLogDao.getNumberOfHitsOrSessionsInPeriod(query, true);

                    query.setTrafficOrigin(TrafficOrigin.EXTERNAL);
                    extSessions = trafficLogDao.getNumberOfHitsOrSessionsInPeriod(query, true);
                    sumSessions = intSessions + extSessions;

                    model.put("intHits", intHits);
                    model.put("intSessions", intSessions);

                    model.put("extHits", extHits);
                    model.put("extSessions", extSessions);

                    model.put("sumHits", sumHits);
                    model.put("sumSessions", sumSessions);

                    model.put("showInternalAndExternal", Boolean.TRUE);
                } else {
                    query.setTrafficOrigin(TrafficOrigin.ALL_USERS);
                    sumHits = trafficLogDao.getNumberOfHitsOrSessionsInPeriod(query, false);
                    sumSessions = trafficLogDao.getNumberOfHitsOrSessionsInPeriod(query, true);
                    model.put("sumHits", sumHits);
                    model.put("sumSessions", sumSessions);
                }


                query.setTrafficOrigin(TrafficOrigin.ALL_USERS);

                List topReferers = trafficLogDao.getReferersInPeriod(query);
                List topReferingHosts = trafficLogDao.getReferingHostsInPeriod(query);
                List topReferingQueries = trafficLogDao.getReferingQueriesInPeriod(query);

                model.put("topReferers", topReferers);
                model.put("topReferingHosts", topReferingHosts);
                model.put("topReferingQueries", topReferingQueries);

            } catch (ContentNotFoundException e) {
                // Do nothing
            }
        }

        return new ModelAndView(getView(), model);
    }

    public void setTotalStatsEnabled(boolean totalStatsEnabled) {
        this.totalStatsEnabled = totalStatsEnabled;
    }
}

