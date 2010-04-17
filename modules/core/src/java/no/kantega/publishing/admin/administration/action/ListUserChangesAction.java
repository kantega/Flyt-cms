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
import org.springframework.web.servlet.mvc.AbstractController;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.admin.viewcontroller.AdminController;

/**
 *
 */
public class ListUserChangesAction extends AdminController {
    private String view;
    private String viewDocuments;

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();

        RequestParameters param = new RequestParameters(request, "utf-8");
        int months = param.getInt("months");
        if (months == -1) {
            months = 3;
        }

        String username = param.getString("username");
        if (username == null) {
            ContentManagementService cms = new ContentManagementService(request);

            model.put("months", months);
            model.put("userChanges", cms.getNoChangesPerUser(months));

            return new ModelAndView(view, model);

        } else {
            username = username.replaceAll("'", "");
            ContentQuery query = new ContentQuery();
            query.setSql(" and contentversion.LastModifiedBy = '" + username + "' and associations.Type = 1");
            model.put("cq", query);
            model.put("username", username);
            return new ModelAndView(viewDocuments, model);
        }
    }

    public void setView(String view) {
        this.view = view;
    }

    public void setViewDocuments(String viewDocuments) {
        this.viewDocuments = viewDocuments;
    }
}