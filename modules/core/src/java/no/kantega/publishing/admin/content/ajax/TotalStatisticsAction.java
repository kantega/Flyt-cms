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
import no.kantega.publishing.api.content.ContentIdHelper;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.trafficlog.TrafficLogDao;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.TrafficLogQuery;
import no.kantega.publishing.common.data.enums.TrafficOrigin;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class TotalStatisticsAction  extends SimpleAdminController {
    @Autowired
    TrafficLogDao trafficLogDao;

    @Autowired
    private ContentIdHelper contentIdHelper;

    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        RequestParameters params = new RequestParameters(request);

        Map<String, Object> model = new HashMap<>();

        ContentManagementService aksessService = new ContentManagementService(request);

        String url = params.getString(AdminRequestParameters.ITEM_IDENTIFIER);

        int trafficOrigin = TrafficOrigin.ALL_USERS;

        model.put("trafficOrigin", trafficOrigin);

        // Extracting currently selected content from it's url
        ContentIdentifier cid = null;
        if (!"".equals(url)) {
            try {
                cid = contentIdHelper.fromRequestAndUrl(request, url);

                Content content = aksessService.getContent(cid);
                if (content != null) {
                    Calendar cal = new GregorianCalendar();
                    Date now = cal.getTime();
                    cal.add(Calendar.MINUTE, -30);
                    Date before = cal.getTime();

                    TrafficLogQuery queryNow = new TrafficLogQuery();
                    queryNow.setCid(content.getContentIdentifier());
                    queryNow.setIncludeSubPages(true);
                    queryNow.setStart(before);
                    queryNow.setEnd(now);
                    queryNow.setTrafficOrigin(trafficOrigin);

                    // Visits and hits last 30 mins
                    int sumSessionsNow = trafficLogDao.getNumberOfHitsOrSessionsInPeriod(queryNow, true);
                    int sumHitsNow = trafficLogDao.getNumberOfHitsOrSessionsInPeriod(queryNow, false);

                    model.put("sumSessionsNow", sumSessionsNow);
                    model.put("sumHitsNow", sumHitsNow);

                    // Visits and hits all time
                    TrafficLogQuery queryAll = new TrafficLogQuery();
                    queryAll.setCid(content.getContentIdentifier());
                    queryAll.setIncludeSubPages(true);
                    queryAll.setTrafficOrigin(trafficOrigin);

                    int sumHits = trafficLogDao.getNumberOfHitsOrSessionsInPeriod(queryAll, false);
                    int sumSessions = trafficLogDao.getNumberOfHitsOrSessionsInPeriod(queryAll, true);

                    model.put("sumSessions", sumSessions);
                    model.put("sumHits", sumHits);

                    double avgHitsPerSession = 0;
                    if (sumSessions > 0) {
                        avgHitsPerSession = ((double)sumHits)/((double)sumSessions);
                    }

                    model.put("avgHitsPerSession", avgHitsPerSession);


                    TrafficLogQuery query = new TrafficLogQuery();
                    query.setCid(content.getContentIdentifier());
                    query.setIncludeSubPages(true);
                    query.setTrafficOrigin(trafficOrigin);

                    if (sumHits > 0) {
                        // Most viewed pages
                        model.put("contentViewStats", trafficLogDao.getMostVisitedContentStatistics(query, 50));

                        // Views per date
                        model.put("dateViewStatistics", trafficLogDao.getPeriodViewStatistics(query, java.util.Calendar.DATE));

                        // Views per hour
                        model.put("hourViewStatistics", trafficLogDao.getPeriodViewStatistics(query, java.util.Calendar.HOUR));
                    }
                }



            } catch (ContentNotFoundException e) {
                // Do nothing
            }
        }

        return new ModelAndView(getView(), model);
    }
}


