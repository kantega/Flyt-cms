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
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.eventlog.EventLog;
import no.kantega.publishing.eventlog.EventLogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 *
 */
public class SearchEventLogAction extends AbstractController {
    private String formView;
    private String resultsView;
    @Autowired
    private EventLog eventLog;

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();

        if (request.getMethod().equalsIgnoreCase("POST")) {
            RequestParameters p = new RequestParameters(request, "utf-8");

            Date from = p.getDate("from_date", Aksess.getDefaultDateFormat());

            // Dates inclusive
            Date end = p.getDate("end_date", Aksess.getDefaultDateFormat());
            if (end != null) {
                GregorianCalendar endInclusive  = new GregorianCalendar();
                endInclusive.setTime(end);
                endInclusive.add(Calendar.DATE, 1);
                end.setTime(endInclusive.getTimeInMillis());
            }

            EventLogQuery eventLogQuery = new EventLogQuery(from, end, p.getString("userid"), p.getString("subject"), p.getString("event"));
            List events = eventLog.getQueryResult(eventLogQuery);
            model.put("events", events);

            return new ModelAndView(resultsView, model);
        } else {
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE, -7);

            model.put("fromDate", calendar.getTime());

            return new ModelAndView(formView, model);
        }
    }

    public void setFormView(String formView) {
        this.formView = formView;
    }

    public void setResultsView(String resultsView) {
        this.resultsView = resultsView;
    }
}
