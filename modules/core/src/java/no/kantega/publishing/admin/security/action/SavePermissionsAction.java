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

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.common.data.BaseObject;
import no.kantega.publishing.common.data.enums.Event;
import no.kantega.publishing.common.service.impl.EventLog;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.Permission;
import no.kantega.publishing.security.data.Role;
import no.kantega.publishing.security.data.SecurityIdentifier;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.security.data.enums.NotificationPriority;
import no.kantega.publishing.security.data.enums.Privilege;
import no.kantega.publishing.security.data.enums.RoleType;
import no.kantega.publishing.security.service.SecurityService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class SavePermissionsAction extends AbstractController {
    private String view;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        RequestParameters param = new RequestParameters(request, "utf-8");

        HttpSession session = request.getSession(true);

        List permissions = new ArrayList();
        Enumeration params = request.getParameterNames();
        while(params.hasMoreElements()) {
            String name = (String)params.nextElement();
            int priv = param.getInt(name);

            if (priv != -1 && name.contains("role_")) {
                Permission permission = new Permission();
                permission.setPrivilege(priv);

                String role = name.substring(name.indexOf("_") + 1, name.length());
                String roletype = param.getString("roletype_" + role);

                int notification = param.getInt("notification_" + role);
                if (priv >= Privilege.APPROVE_CONTENT && notification > 0) {
                    permission.setNotificationPriority(NotificationPriority.getNotificationPriorityAsEnum(notification));
                } else {
                    permission.setNotificationPriority(null);
                }

                SecurityIdentifier newRole;
                if (RoleType.USER.equalsIgnoreCase(roletype)) {
                    newRole = new User();
                } else {
                    newRole = new Role();
                }
                newRole.setId(role);
                permission.setSecurityIdentifier(newRole);
                permissions.add(permission);
            }

        }

        BaseObject object = (BaseObject)session.getAttribute(EditPermissionsAction.PERMISSIONS_OBJECT);
        if (object != null) {
            setUpdatedPermissions(request, permissions, object);
        }

        session.removeAttribute(EditPermissionsAction.PERMISSIONS_OBJECT);
        session.removeAttribute(EditPermissionsAction.PERMISSIONS_LIST);

        return new ModelAndView(view);
    }

    private void setUpdatedPermissions(HttpServletRequest request, List permissions, BaseObject object) {
        EventLog.log(SecuritySession.getInstance(request), request, Event.SET_PERMISSIONS, object.getName());
        SecurityService.setPermissions(object, permissions);
    }

    public void setView(String view) {
        this.view = view;
    }
}