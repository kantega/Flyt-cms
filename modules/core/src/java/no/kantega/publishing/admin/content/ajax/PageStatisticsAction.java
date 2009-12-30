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

import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.TrafficLogQuery;
import no.kantega.publishing.common.data.enums.TrafficOrigin;
import no.kantega.publishing.common.ao.LinkDao;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.service.TrafficStatisticsService;
import no.kantega.publishing.modules.linkcheck.check.LinkOccurrence;
import no.kantega.publishing.modules.linkcheck.check.LinkOccurrenceHandler;
import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.admin.viewcontroller.SimpleAdminController;
import no.kantega.commons.client.util.RequestParameters;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class PageStatisticsAction  extends SimpleAdminController {

    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        RequestParameters params = new RequestParameters(request);

        TrafficStatisticsService trafficService = new TrafficStatisticsService();

        String url = params.getString(AdminRequestParameters.ITEM_IDENTIFIER);

        Map<String, Object> model = new HashMap<String, Object>();

        // Extracting currently selected content from it's url
        ContentIdentifier cid = null;
        if (!"".equals(url)) {
            try {
                cid = new ContentIdentifier(request, url);

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
                    intHits = trafficService.getNumberOfVisitsInPeriod(query);

                    query.setTrafficOrigin(TrafficOrigin.EXTERNAL);
                    extHits = trafficService.getNumberOfVisitsInPeriod(query);
                    sumHits = intHits + extHits;

                    query.setTrafficOrigin(TrafficOrigin.INTERNAL);
                    intSessions = trafficService.getNumberOfSessionsInPeriod(query);

                    query.setTrafficOrigin(TrafficOrigin.EXTERNAL);
                    extSessions = trafficService.getNumberOfSessionsInPeriod(query);
                    sumSessions = intSessions + extSessions;

                    model.put("intHits", sumHits);
                    model.put("intSessions", sumSessions);

                    model.put("extHits", sumHits);
                    model.put("extSessions", sumSessions);

                    model.put("sumHits", sumHits);
                    model.put("sumSessions", sumSessions);

                    model.put("showInternalAndExternal", Boolean.TRUE);
                } else {
                    query.setTrafficOrigin(TrafficOrigin.ALL_USERS);
                    sumHits = trafficService.getNumberOfVisitsInPeriod(query);
                    sumSessions = trafficService.getNumberOfVisitsInPeriod(query);
                    model.put("sumHits", sumHits);
                    model.put("sumSessions", sumSessions);
                }


                query.setTrafficOrigin(TrafficOrigin.ALL_USERS);

                List topReferers = trafficService.getReferersInPeriod(query);
                List topReferingHosts = trafficService.getReferingHostsInPeriod(query);
                List topReferingQueries = trafficService.getReferingQueriesInPeriod(query);

                model.put("topReferers", topReferers);
                model.put("topReferingHosts", topReferingHosts);
                model.put("topReferingQueries", topReferingQueries);

            } catch (ContentNotFoundException e) {
                // Do nothing
            }
        }

        return new ModelAndView(getView(), model);
    }
}

