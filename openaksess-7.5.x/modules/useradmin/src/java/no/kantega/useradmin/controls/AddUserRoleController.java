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

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.kantega.security.api.role.*;
import no.kantega.security.api.identity.DefaultIdentity;
import no.kantega.useradmin.model.RoleManagementConfiguration;
import no.kantega.commons.client.util.RequestParameters;

import java.util.Date;

/**
 * User: Anders Skar, Kantega AS
 * Date: Jul 9, 2007
 * Time: 11:08:38 AM
 */
public class AddUserRoleController extends AbstractUserAdminController {

    public ModelAndView doHandleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);

        String userId = param.getString("userId");
        String userDomain = param.getString("userDomain");

        String roleId = param.getString("roleId");
        String roleDomain = param.getString("roleDomain");

        DefaultIdentity identity = new DefaultIdentity();
        identity.setUserId(userId);
        identity.setDomain(userDomain);


        RoleManagementConfiguration config = getRoleConfiguration(roleDomain);
        RoleManager manager = config.getRoleManager();
        RoleUpdateManager updateManager = config.getRoleUpdateManager();
        if (updateManager != null) {
            if (!manager.userHasRole(identity, roleId)) {
                DefaultRoleId role = new DefaultRoleId();
                role.setId(roleId);
                role.setDomain(roleDomain);
                updateManager.addUserToRole(identity, role);
            }
        }

        return new ModelAndView(new RedirectView("user?domain=" + userDomain + "&userId=" + userId + "&refresh=" + new Date().getTime()));
    }
}
