/*
 * Copyright 2010 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.mypage.plugins;

import no.kantega.publishing.common.ao.TrafficLogDao;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.TrafficLogQuery;
import no.kantega.publishing.common.data.enums.TrafficOrigin;
import no.kantega.publishing.content.api.ContentAO;
import no.kantega.publishing.org.OrgUnit;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class OrgUnitStatisticsAction implements Controller {
    private static final Logger log = LoggerFactory.getLogger(OrgUnitStatisticsAction.class);

    private String view;

    @Autowired
    private TrafficLogDao trafficLogDao;

    @Autowired
    private ContentAO contentAO;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<>();

        SecuritySession securitySession = SecuritySession.getInstance(request);
        User user = securitySession.getUser();
        List<OrgUnit> orgUnits = user.getOrgUnits();
        if (!orgUnits.isEmpty()) {
            OrgUnit orgUnit = orgUnits.get(0);
            model.put("orgUnit", orgUnit);
            Content content = contentAO.getContent(orgUnit);
            log.debug( "Looked up organization unit with name '" + orgUnit.getName() + "'");
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
                queryNow.setTrafficOrigin(TrafficOrigin.ALL_USERS);

                // Visits and hits last 30 mins
                int sumSessionsNow = trafficLogDao.getNumberOfHitsOrSessionsInPeriod(queryNow, true);
                int sumHitsNow = trafficLogDao.getNumberOfHitsOrSessionsInPeriod(queryNow, false);

                model.put("sumSessionsNow", sumSessionsNow);
                model.put("sumHitsNow", sumHitsNow);

                // Visits and hits all time
                TrafficLogQuery queryAll = new TrafficLogQuery();
                queryAll.setCid(content.getContentIdentifier());
                queryAll.setIncludeSubPages(true);
                queryAll.setTrafficOrigin(TrafficOrigin.ALL_USERS);

                int sumHits = trafficLogDao.getNumberOfHitsOrSessionsInPeriod(queryAll, false);
                int sumSessions = trafficLogDao.getNumberOfHitsOrSessionsInPeriod(queryAll, true);

                model.put("sumSessions", sumSessions);
                model.put("sumHits", sumHits);

                double avgHitsPerSession = 0;
                if (sumSessions > 0) {
                    avgHitsPerSession = ((double)sumHits)/((double)sumSessions);
                }
                model.put("avgHitsPerSession", avgHitsPerSession);
            }
        }

        return new ModelAndView(view, model);
    }

    public void setView(String view) {
        this.view = view;
    }
}
