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

package no.kantega.useradmin.controls;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.security.api.role.DefaultRoleId;
import no.kantega.security.api.role.Role;
import no.kantega.security.api.role.RoleManager;
import no.kantega.security.api.role.RoleUpdateManager;
import no.kantega.useradmin.model.RoleManagementConfiguration;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Anders Skar, Kantega AS
 * Date: Jul 11, 2007
 * Time: 4:01:30 PM
 */
public class DeleteRoleController extends AbstractUserAdminController {
    
    public ModelAndView doHandleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);

        String id = param.getString("roleId");
        String domain = param.getString("domain");
        String confirm = param.getString("confirm");

        RoleManagementConfiguration config = getRoleConfiguration(domain);
        RoleManager manager = config.getRoleManager();

        DefaultRoleId roleId = new DefaultRoleId();
        roleId.setDomain(domain);
        roleId.setId(id);

        Map model = new HashMap();
        if (confirm != null) {
            // Delete role
            RoleUpdateManager updateManager = config.getRoleUpdateManager();
            if (updateManager != null) {
                updateManager.deleteRole(roleId);
            }
            return new ModelAndView(new RedirectView("search?message=useradmin.role.deleted&domain=" + URLEncoder.encode(domain, "iso-8859-1")));
        } else {
            // Confirm deletion
            Role role = manager.getRoleById(roleId);
            model.put("role", role);
            return new ModelAndView("role/delete", model);
        }
    }
}

