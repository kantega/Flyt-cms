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
import no.kantega.security.api.role.DefaultRoleId;
import no.kantega.security.api.role.RoleUpdateManager;
import no.kantega.useradmin.model.RoleManagementConfiguration;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * User: Anders Skar, Kantega AS
 * Date: Jul 9, 2007
 * Time: 1:03:37 PM
 */
public class RemoveUserRoleController extends AbstractUserAdminController {

    public ModelAndView doHandleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);

        String userId = param.getString("userId");
        String userDomain = param.getString("userDomain");

        String roleId = param.getString("roleId");
        String roleDomain = param.getString("roleDomain");

        String context = param.getString("context");

        DefaultIdentity identity = new DefaultIdentity();
        identity.setUserId(userId);
        identity.setDomain(userDomain);


        RoleManagementConfiguration config = getRoleConfiguration(roleDomain);
        RoleUpdateManager updateManager = config.getRoleUpdateManager();
        if (updateManager != null) {
            DefaultRoleId role = new DefaultRoleId();
            role.setId(roleId);
            role.setDomain(roleDomain);
            updateManager.removeUserFromRole(identity, role);
        }

        if ("role".equals(context)) {
            return new ModelAndView(new RedirectView("userswithrole?domain=" + roleDomain + "&roleId=" + roleId + "&refresh=" + new Date().getTime()));
        } else {
            return new ModelAndView(new RedirectView("user?domain=" + userDomain + "&userId=" + userId + "&refresh=" + new Date().getTime()));
        }
    }
}

