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

import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.admin.viewcontroller.AdminController;
import no.kantega.commons.client.util.RequestParameters;

/**
 *
 */
public class SearchEventLogAction extends AdminController {
    private String formView;
    private String resultsView;

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();

        if (request.getMethod().equalsIgnoreCase("POST")) {
            ContentManagementService cms = new ContentManagementService(request);

            RequestParameters p = new RequestParameters(request, "utf-8");

            Date from = p.getDate("from_date", Aksess.getDefaultDateFormat());
            Date end  = p.getDate("end_date", Aksess.getDefaultDateFormat());
            List events = cms.searchEventLog(from, end, p.getString("userid"), p.getString("subject"), p.getString("event"));
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
