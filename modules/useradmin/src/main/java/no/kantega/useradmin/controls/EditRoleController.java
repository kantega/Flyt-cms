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
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.security.api.role.DefaultRole;
import no.kantega.security.api.role.DefaultRoleId;
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

public class EditRoleController extends AbstractUserAdminController {

    public ModelAndView doHandleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);        

        String id = param.getString("roleId");
        // Trim whitespace
        id = id == null ? null : id.trim();
        String domain = param.getString("domain");
        String save = param.getString("save");

        ValidationErrors errors = new ValidationErrors();

        Map<String, Object> model = new HashMap<>();
        model.put("domain", domain);
        model.put("errors", errors);
        model.put("configurations", getRoleConfiguration());
        model.put("numConfigurations", getRoleConfiguration().size());

        RoleManagementConfiguration config = getRoleConfiguration(domain);
        RoleManager manager = config.getRoleManager();
        RoleUpdateManager updateManager = config.getRoleUpdateManager();



        if (save != null) {
            DefaultRole role = new DefaultRole();
            role.setDomain(domain);
            role.setId(id);
            role.setName(id);

            if (id == null  || id.length() == 0) {
                model.put("isNew", Boolean.TRUE);
                model.put("canEdit", Boolean.TRUE);
                model.put("role", role);
                model.put("domain", role.getDomain());
                errors.add(null, "useradmin.role.rolenamemissing");
                return new ModelAndView("role/edit", model);
            } else if (updateManager != null) {
                updateManager.saveOrUpdateRole(role);
                return new ModelAndView(new RedirectView("search?message=useradmin.role.saved&domain=" + URLEncoder.encode(domain, "iso-8859-1")));
            }
        } else {
            // Existing role
            if (id != null && id.length() > 0) {
                DefaultRoleId role = new DefaultRoleId();
                role.setDomain(domain);
                role.setId(id);
                model.put("role", manager.getRoleById(role));
                model.put("domain", role.getDomain());
            } else {
                // New role
                if (updateManager != null) {
                    model.put("canEdit", Boolean.TRUE);
                }
            }
        }

        model.put("configurations", getRoleConfiguration());

        return new ModelAndView("role/edit", model);

    }
}
