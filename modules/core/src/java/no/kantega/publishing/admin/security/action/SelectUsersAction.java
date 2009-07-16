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

import no.kantega.publishing.admin.viewcontroller.AdminController;
import no.kantega.publishing.security.data.enums.RoleType;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.commons.client.util.RequestParameters;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 *
 */
public class SelectUsersAction extends AdminController {
    private String view;

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);
        String action = param.getString("action");
        String name = param.getString("name");
        boolean select = param.getBoolean("select", false);
        if (action == null || action.length() == 0) {
            action = "AddUserRolePermission.action";
        }

        Map<String, Object> model =  new HashMap<String, Object>();
        model.put("action", action);
        model.put("select", select);
        model.put("name", name);

        List users;
        if(name != null && name.length() > 0) {
            users = SecuritySession.getInstance(request).searchUsers(name);
            if (users.size() == 0) {
                model.put("notFound", Boolean.TRUE);
            }
        } else {
            users = new ArrayList();
        }

        model.put("users", users);        

        return new ModelAndView(view, model);
    }

    public void setView(String view) {
        this.view = view;
    }
}
