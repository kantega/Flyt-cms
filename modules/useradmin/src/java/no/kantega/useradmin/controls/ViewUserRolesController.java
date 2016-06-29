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
import no.kantega.security.api.identity.DefaultIdentity;
import no.kantega.security.api.identity.Identity;
import no.kantega.security.api.role.RoleManager;
import no.kantega.security.api.role.RoleUpdateManager;
import no.kantega.useradmin.model.RoleManagementConfiguration;
import no.kantega.useradmin.model.RoleSet;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewUserRolesController extends AbstractUserAdminController {
    public ModelAndView doHandleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);

        String id = param.getString("userId");
        String domain = param.getString("domain");

        Map<String, Object> model = new HashMap<>();
        model.put("userId", id);
        model.put("userDomain", domain);


        Identity identity = DefaultIdentity.withDomainAndUserId(domain, id);

        List<RoleSet> roleSets = new ArrayList<>();

        List roleConfigs = getRoleConfiguration();

        /**
         * Hent alle rollesett brukeren har og legg dem inn lista
         * Dersom et rollesett har en UpdateRoleManager, sett flagg at disse er redigerbare
         */
        for (int i = 0; i < roleConfigs.size(); i++) {
            RoleManagementConfiguration config =  (RoleManagementConfiguration)roleConfigs.get(i);
            RoleManager roleManager = config.getRoleManager();
            RoleUpdateManager updateManager = config.getRoleUpdateManager();

            RoleSet roleSet = new RoleSet();
            roleSet.setDomain(config.getDomain());
            roleSet.setDescription(config.getDescription());
            roleSet.setUserRoles(roleManager.getRolesForUser(identity));

            if (updateManager != null) {
                roleSet.setIsEditable(true);
                roleSet.setAvailableRoles(roleManager.getAllRoles());
            }
            roleSets.add(roleSet);
        }

        model.put("roleSets", roleSets);

        return new ModelAndView("role/user", model);
    }
}
