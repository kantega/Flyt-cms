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

package no.kantega.publishing.security.realm;

import no.kantega.publishing.security.data.User;
import no.kantega.publishing.security.data.Role;
import no.kantega.publishing.security.util.SecurityHelper;
import no.kantega.publishing.common.Aksess;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.LocaleLabels;
import no.kantega.security.api.role.RoleManager;
import no.kantega.security.api.profile.ProfileManager;
import no.kantega.security.api.profile.Profile;
import no.kantega.security.api.identity.IdentityResolver;
import no.kantega.security.api.identity.Identity;
import no.kantega.security.api.password.PasswordManager;
import no.kantega.security.api.search.SearchResult;


import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class SecurityRealm {
    private static final String SOURCE = "aksess.SecurityRealm";

    ProfileManager profileManager;
    RoleManager roleManager;
    IdentityResolver identityResolver;
    PasswordManager passwordManager;

    public List searchUsers(String name) throws SystemException {
        List results = new ArrayList();
        try {
            SearchResult result = profileManager.searchProfiles(name);
            if (result != null) {
                Iterator it = result.getAllResults();
                while (it.hasNext()) {
                    Profile p =  (Profile)it.next();
                    results.add(SecurityHelper.createAksessUser(p));
                }
            }

        } catch (no.kantega.security.api.common.SystemException e) {
            throw new SystemException(SOURCE, "searchUsers failed", e);
        }

        return results;
    }


    public List getAllRoles() throws SystemException {
        List results = new ArrayList();

        Role everyone = new Role();
        everyone.setId(Aksess.getEveryoneRole());
        everyone.setName("Alle brukere");
        results.add(everyone);

        Role owner = new Role();
        owner.setId(Aksess.getOwnerRole());
        owner.setName("Eier (ansvarlig person)");
        results.add(owner);

        try {
            Iterator it = roleManager.getAllRoles();
            if (it != null) {
                while (it.hasNext()) {
                    no.kantega.security.api.role.Role role =  (no.kantega.security.api.role.Role)it.next();
                    results.add(SecurityHelper.createAksessRole(role));
                }
            }

        } catch (no.kantega.security.api.common.SystemException e) {
            throw new SystemException(SOURCE, "getAllRoles failed", e);
        }

        return results;
    }


    public User lookupUser(String userid) throws SystemException {
        try {
            if (userid==null || userid.equalsIgnoreCase("")) { return null;}
            Profile p = profileManager.getProfileForUser(SecurityHelper.createApiIdentity(userid));
            if (p==null) {return null;}
            return SecurityHelper.createAksessUser(p);
        } catch (no.kantega.security.api.common.SystemException e) {
            throw new SystemException(SOURCE, "lookupUser failed", e);
        }
    }


    public List lookupRolesForUser(String userid) throws SystemException {
        List roles = new ArrayList();
        try {
            Iterator it = roleManager.getRolesForUser(SecurityHelper.createApiIdentity(userid));
            if (it != null) {
                while (it.hasNext()) {
                    no.kantega.security.api.role.Role role =  (no.kantega.security.api.role.Role)it.next();
                    roles.add(SecurityHelper.createAksessRole(role));
                }
            }

            Role everyone = new Role();
            everyone.setId(Aksess.getEveryoneRole());
            everyone.setName("Alle brukere");
            roles.add(everyone);

            return roles;
        } catch (no.kantega.security.api.common.SystemException e) {
            throw new SystemException(SOURCE, "lookupUser failed", e);
        }
    }

    public List lookupUsersWithRole(String roleId) throws SystemException {
        List userIds = new ArrayList();
        try {
            Iterator it = roleManager.getUsersWithRole(SecurityHelper.createApiRole(roleId));
            if (it != null) {
                while (it.hasNext()) {
                    Identity role =  (Identity)it.next();
                    if(role != null) {
                        userIds.add(role.getUserId());
                    }
                }
            }
            return userIds;
        } catch (no.kantega.security.api.common.SystemException e) {
            throw new SystemException(SOURCE, "lookupUsersWithRole failed", e);
        }
    }


    public Role lookupRole(String roleid) throws SystemException {
        try {
            if (Aksess.getEveryoneRole().equalsIgnoreCase(roleid)) {
                Role role = new Role();
                role.setName(LocaleLabels.getLabel("aksess.editpermissions.everyone", Aksess.getDefaultAdminLocale()));
                role.setId(roleid);
                return role;
            } else if (Aksess.getOwnerRole().equalsIgnoreCase(roleid)) {
                Role role = new Role();
                role.setName(LocaleLabels.getLabel("aksess.editpermissions.owner", Aksess.getDefaultAdminLocale()));
                role.setId(roleid);
                return role;
            } else {
                no.kantega.security.api.role.Role role = roleManager.getRoleById(SecurityHelper.createApiRole(roleid));
                return SecurityHelper.createAksessRole(role);
            }
        } catch (no.kantega.security.api.common.SystemException e) {
             throw new SystemException(SOURCE, "lookupRole failed", e);
        }
    }


    public ProfileManager getProfileManager() {
        return profileManager;
    }


    public void setProfileManager(ProfileManager profileManager) {
        this.profileManager = profileManager;
    }


    public RoleManager getRoleManager() {
        return roleManager;
    }


    public void setRoleManager(RoleManager roleManager) {
        this.roleManager = roleManager;
    }


    public IdentityResolver getIdentityResolver() {
        return identityResolver;
    }


    public void setIdentityResolver(IdentityResolver identityResolver) {
        this.identityResolver = identityResolver;
    }


    public PasswordManager getPasswordManager() {
        return passwordManager;
    }


    public void setPasswordManager(PasswordManager passwordManager) {
        this.passwordManager = passwordManager;
    }
}
