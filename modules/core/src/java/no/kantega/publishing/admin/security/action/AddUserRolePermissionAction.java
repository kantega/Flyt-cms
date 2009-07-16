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

package no.kantega.publishing.admin.security.action;

import no.kantega.publishing.security.data.Permission;
import no.kantega.publishing.security.data.SecurityIdentifier;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.security.data.Role;
import no.kantega.publishing.security.data.enums.Privilege;
import no.kantega.publishing.security.data.enums.RoleType;
import no.kantega.publishing.common.data.BaseObject;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.admin.security.action.EditPermissionsAction;
import no.kantega.commons.client.util.RequestParameters;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.ModelAndView;

public class AddUserRolePermissionAction extends AbstractController {
    private String view;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        RequestParameters param = new RequestParameters(request, "utf-8");

        String roletype = param.getString("roletype");
        String[] roles = param.getStrings("role");

        HttpSession session = request.getSession();
        BaseObject permObject = (BaseObject)session.getAttribute(EditPermissionsAction.PERMISSIONS_OBJECT);
        List permissions = (List)session.getAttribute(EditPermissionsAction.PERMISSIONS_LIST);

        if (permissions != null && roles != null) {            
            for (int i = 0; i < roles.length; i++) {
                String role = roles[i];
                boolean found = false;

                // Check that role is not already added
                for (int j = 0; j < permissions.size(); j++) {
                    Permission p = (Permission)permissions.get(j);
                    SecurityIdentifier sid = p.getSecurityIdentifier();
                    String roleId = sid.getId();
                    if (roleId.equalsIgnoreCase(role) && roletype.equalsIgnoreCase(sid.getType())) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    // Add role / user
                    SecurityIdentifier newRole;
                    if (RoleType.USER.equalsIgnoreCase(roletype)) {
                        newRole = new User();
                        
                    } else {
                        newRole = new Role();
                    }
                    newRole.setId(role);

                    // New roles are given read permissions as standard
                    Permission newPermission = new Permission();
                    if (permObject instanceof Multimedia) {
                        // Multimedia objects only have update privileges
                        newPermission.setPrivilege(Privilege.UPDATE_CONTENT);
                    } else {
                        newPermission.setPrivilege(Privilege.VIEW_CONTENT);
                    }
                    newPermission.setSecurityIdentifier(newRole);
                    permissions.add(newPermission);
                }
            }
        }

        Map model = new HashMap();
        
        session.setAttribute(EditPermissionsAction.PERMISSIONS_LIST, permissions);

        model.put("reloadWindow", Boolean.TRUE);

        return new ModelAndView(view, model);
    }

    public void setView(String view) {
        this.view = view;
    }
}