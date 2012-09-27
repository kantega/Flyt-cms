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

package no.kantega.publishing.admin.security.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.common.data.enums.ObjectType;
import no.kantega.publishing.security.service.SecurityService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ViewAllPermissionsAction extends AbstractController {
    private String view;

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();

        List permissionsOverview = null;

        RequestParameters param = new RequestParameters(request, "utf-8");
        int objectType = param.getInt("objectType");
        if (objectType != -1) {
            permissionsOverview = SecurityService.getPermissionsOverview(objectType);
            if (objectType == ObjectType.ASSOCIATION) {
                model.put("associationSelected", "selected");
            } else if (objectType == ObjectType.MULTIMEDIA) {
                model.put("multimediaSelected", "selected");
            } else if (objectType == ObjectType.TOPICMAP) {
                model.put("topicMapSelected", "selected");
            }
        }

        model.put("objectType", objectType);
        model.put("permissionsOverview", permissionsOverview);

        return new ModelAndView(view, model);
    }

    public void setView(String view) {
        this.view = view;
    }
}
