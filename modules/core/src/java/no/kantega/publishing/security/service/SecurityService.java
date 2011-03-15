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

import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.BaseObject;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.security.data.*;
import no.kantega.publishing.security.data.enums.Privilege;
import no.kantega.publishing.security.data.enums.NotificationPriority;
import no.kantega.publishing.security.ao.PermissionsAO;
import no.kantega.commons.exception.SystemException;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;

public class SecurityService {

    /**
     * Sjekk om rolle har tilgang til objekt
     * @param role
     * @param object
     * @param privilege
     * @return
     * @throws SystemException
     */
    public static boolean isAuthorized(Role role, BaseObject object, int privilege) throws SystemException {
        if (object instanceof Content) {
            Content c = (Content)object;
            object = c.getAssociation();
        }

        List permissions = PermissionsCache.getPermissions(object);
        if (permissions == null || permissions.size() == 0) {
            // Ingen rettigheter definert for dette privilegium, ok
            return true;
        }

        for (int i = 0; i < permissions.size(); i++) {
            Permission p = (Permission)permissions.get(i);
            if (p.getPrivilege() >= privilege) {
                SecurityIdentifier sid = p.getSecurityIdentifier();
                String id = sid.getId();
                if (role.getId().equalsIgnoreCase(id)) {
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * Sjekk om bruker har tilgang til objekt
     * @param user
     * @param object
     * @param privilege
     * @return
     * @throws SystemException
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

        List permissions = PermissionsCache.getPermissions(object);
        if (permissions == null || permissions.size() == 0) {
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
     * Sjekk om bruker er godkjenner av objekt
     * @param user
     * @param object
     * @return
     * @throws SystemException
     */
    public static boolean isApprover(User user, Content object) throws SystemException {

        String ownerUnit = object.getOwner();
        if ("".equals(ownerUnit)) ownerUnit = null;

        String ownerPerson = object.getOwnerPerson();
        if ("".equals(ownerPerson)) ownerPerson = null;

        List<Permission> permissions = PermissionsCache.getPermissions(object.getAssociation());
        if (permissions == null || permissions.size() == 0) {
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
     * Sjekk om bruker har rolle
     * @param user
     * @param role
     * @return true/false
     */
    public static boolean isUserInRole(User user, String role) {
        if (role.equalsIgnoreCase(Aksess.getEveryoneRole())) {
            return true;
        } else if (user == null) {
            return false;
        } else {
            HashMap roles = user.getRoles();
            if (roles == null) {
                return false;
            }
            Role r = (Role)roles.get(role);
            return r != null;
        }
    }


    /**
     * Hent rettigheter for objekt
     * @param object
     * @return
     * @throws SystemException
     */
    public static List getPermissions(BaseObject object) throws SystemException {
        if (object instanceof Content) {
            Content c = (Content)object;
            object = c.getAssociation();
        }

        List permissions = new ArrayList();

        // Klone liste i tilfelle den blir endret
        List tmp = PermissionsCache.getPermissions(object);
        if (tmp != null) {
            for (int i = 0; i < tmp.size(); i++) {
                Permission p = (Permission)tmp.get(i);
                Permission newP = new Permission(p);
                permissions.add(newP);
            }
        }

        return permissions;
    }


    /**
     * Lagre rettigheter for objekt
     * @param object
     * @param permissions
     * @throws SystemException
     */
    public static void setPermissions(BaseObject object, List permissions) throws SystemException {
        if (object instanceof Content) {
            Content c = (Content)object;
            object = c.getAssociation();
        }

        PermissionsAO.setPermissions(object, permissions);
        // Oppdater cache med nye rettigheter
        PermissionsCache.reloadCache();
    }

    /**
     * Hent oversikt over alle rettigheter
     * @param objectType
     * @return
     * @throws SystemException
     */
    public static List getPermissionsOverview(int objectType) throws SystemException {
        return PermissionsAO.getPermissionsOverview(objectType);
    }
}

