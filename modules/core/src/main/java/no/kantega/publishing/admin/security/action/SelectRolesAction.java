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
import no.kantega.publishing.security.SecuritySession;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class SelectRolesAction extends AbstractController {
    private String view;

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);
        String action = param.getString("action");
        boolean multiple = param.getBoolean("multiple", true);
        if (action == null || action.length() == 0) {
            action = "AddUserRolePermission.action";
        }

        Map<String, Object> model =  new HashMap<>();
        model.put("action", action);
        model.put("multiple", multiple);

        model.put("roles", SecuritySession.getInstance(request).getAllRoles());        

        return new ModelAndView(view, model);
    }

    public void setView(String view) {
        this.view = view;
    }
}

