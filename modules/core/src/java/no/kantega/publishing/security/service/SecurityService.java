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

package no.kantega.publishing.security.service;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.services.security.PermissionAO;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.BaseObject;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.security.data.Permission;
import no.kantega.publishing.security.data.Role;
import no.kantega.publishing.security.data.SecurityIdentifier;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.security.data.enums.NotificationPriority;
import no.kantega.publishing.security.data.enums.Privilege;
import no.kantega.publishing.spring.RootContext;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class SecurityService {
    private static PermissionAO permissionAO;

    /**
     * Check whether a user with the given role is authorized to do the given privilege on the given object.
     * @return true if the user is authorized.
     * @throws SystemException if loading of the privilegies fails.
     */
    public static boolean isAuthorized(Role role, BaseObject object, int privilege) throws SystemException {
        if (object instanceof Content) {
            Content c = (Content)object;
            object = c.getAssociation();
        }
        setPermissionAOIfNotSet();
        List<Permission> permissions = permissionAO.getPermissions(object);
        if (permissions.isEmpty()) {
            // Ingen rettigheter definert for dette privilegium, ok
            return true;
        }

        for (Permission permission : permissions) {
            if (permission.getPrivilege() >= privilege) {
                SecurityIdentifier sid = permission.getSecurityIdentifier();
                String id = sid.getId();
                if (role.getId().equalsIgnoreCase(id)) {
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * Check whether the given user is authorized to do the given privilege on the given object.
     * @return true if the user is authorized.
     * @throws SystemException if loading of the privilegies fails.
     */
    public static boolean isAuthorized(User user, BaseObject object, int privilege) throws SystemException {

        String ownerUnit = object.getOwner();
        if ("".equals(ownerUnit)) ownerUnit = null;

        String ownerPerson = object.getOwnerPerson();
        if ("".equals(ownerPerson)) ownerPerson = null;

        if (object instanceof Content) {
            Content c = (Content)object;
            object = c.getAssociation();
        }
        setPermissionAOIfNotSet();
        List<Permission> permissions = permissionAO.getPermissions(object);
        if (permissions.isEmpty()) {
            // Ingen rettigheter definert for dette privilegium, ok
            return true;
        }

        Permission p = getPermissionForUser(user, privilege, permissions, ownerPerson, ownerUnit);
        if (p != null) {
            return true;
        } else if (isUserInRole(user, Aksess.getAdminRole())) {
            return true;
        }

        return false;
    }


    /**
     * Check whether the given user has permission to approve the given Content object
     * @return true if the user can approve the content object
     * @throws SystemException if loading of the privilegies fails.
     */
    public static boolean isApprover(User user, Content object) throws SystemException {

        String ownerUnit = object.getOwner();
        if ("".equals(ownerUnit)) ownerUnit = null;

        String ownerPerson = object.getOwnerPerson();
        if ("".equals(ownerPerson)) ownerPerson = null;

        setPermissionAOIfNotSet();
        List<Permission> permissions = permissionAO.getPermissions(object.getAssociation());
        if (permissions.isEmpty()) {
            // Ingen rettigheter definert for dette privilegium, ok
            return true;
        }

        long now = new Date().getTime();
        long lastmod = object.getLastModified().getTime();
        long week = 1000*60*60*24*7;

        Permission p = getPermissionForUser(user, Privilege.APPROVE_CONTENT, permissions, ownerPerson, ownerUnit);
        if (p != null) {
            NotificationPriority priority = p.getNotificationPriority();
            if (priority == NotificationPriority.PRIORITY1) {
                // Approve at once
                return true;
            } else if (priority == NotificationPriority.PRIORITY2) {
                // Approve if content mod date > 1 week
                if (now - lastmod > week) return true;
            } else if (priority == NotificationPriority.PRIORITY3) {
                // Approve if content mod date > 2 weeks
                if (now - lastmod > 2*week) return true;
            }

        } else if (isUserInRole(user, Aksess.getAdminRole())) {
            // User has no direct permissions to approve, but is admin
            boolean othersAreAuthorized = false;
            for (Permission tmpP : permissions) {
                if (tmpP.getPrivilege() >= Privilege.APPROVE_CONTENT) {
                    othersAreAuthorized = true;
                    break;
                }
            }

            if (othersAreAuthorized) {
                // Approve if content mod date > 2 weeks
                if (now - lastmod > 2*week) return true;
            } else {
                // No other roles are given permission to approve, admin should approve at once
                return true;
            }
        }

        return false;
    }


    private static Permission getPermissionForUser(User user, int privilege, List<Permission> permissions, String ownerPerson, String ownerUnit) {
        for (Permission p : permissions) {
            if (p.getPrivilege() >= privilege) {
                SecurityIdentifier sid = p.getSecurityIdentifier();
                String id = sid.getId();

                if (sid.equals(Aksess.getEveryoneRole())) {
                    // All users are authorized if everyone roles is given permission
                    return p;
                } else if (user != null) {
                    if (sid instanceof User && user.equals(id)) {
                        return p;
                    } else if (sid.equals(Aksess.getOwnerRole()) && user.getId().equals(ownerPerson)){
                        return p;
                    } else {
                        if (isUserInRole(user, id)) {
                            // Brukeren har nå normalt tilgang
                            // Men dersom det er en enhetsrolle, må det sjekkes nærmere
                            if (sid.equals(Aksess.getUnitRole())) {
                                // Enhetsrolle gir kun tilgang når enheten eier objektet
                                if (ownerUnit != null && ownerUnit.length() > 0 && isUserInRole(user, ownerUnit)) {
                                    // Dette er en enhetsrolle, er kun gyldig dersom brukeren har tilgang til enheten
                                    return p;
                                }
                            } else {
                                return p;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }



    /**
     * Check whether the given user has the role with the given name.
     * @return true if the user has the given role.
     */
    public static boolean isUserInRole(User user, String role) {
        if (role.equalsIgnoreCase(Aksess.getEveryoneRole())) {
            return true;
        } else if (user == null) {
            return false;
        } else {
            Map<String, Role> roles = user.getRoles();
            if (roles == null) {
                return false;
            }
            Role r = roles.get(role);
            return r != null;
        }
    }


    /**
     * @return Permissions defined for the given object.
     * @throws SystemException if loading of the privilegies fails.
     */
    public static List<Permission> getPermissions(BaseObject object) throws SystemException {
        if (object instanceof Content) {
            Content c = (Content)object;
            object = c.getAssociation();
        }

        return permissionAO.getPermissions(object);
    }


    /**
     * Set the given Permissions on the given object.
     * @throws SystemException if persistence of permissions fails.
     */
    public static void setPermissions(BaseObject object, List<Permission> permissions) throws SystemException {
        if (object instanceof Content) {
            Content c = (Content)object;
            object = c.getAssociation();
        }
        setPermissionAOIfNotSet();
        permissionAO.setPermissions(object, permissions);
    }

    private static void setPermissionAOIfNotSet(){
        if(permissionAO == null){
            permissionAO = RootContext.getInstance().getBean(PermissionAO.class);
        }
    }
}

