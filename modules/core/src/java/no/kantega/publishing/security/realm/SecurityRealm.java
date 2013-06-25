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

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.LocaleLabels;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.security.data.Role;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.security.util.SecurityHelper;
import no.kantega.security.api.identity.Identity;
import no.kantega.security.api.identity.IdentityResolver;
import no.kantega.security.api.password.PasswordManager;
import no.kantega.security.api.profile.Profile;
import no.kantega.security.api.profile.ProfileManager;
import no.kantega.security.api.role.RoleManager;
import no.kantega.security.api.search.SearchResult;
import org.springframework.cache.annotation.Cacheable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class SecurityRealm {
    private static final String SOURCE = "aksess.SecurityRealm";

    ProfileManager profileManager;
    RoleManager roleManager;
    IdentityResolver identityResolver;
    PasswordManager passwordManager;

    public List<User> searchUsers(String name) throws SystemException {
        List<User> results = new ArrayList<User>();
        try {
            SearchResult<Profile> result = profileManager.searchProfiles(name);
            if (result != null) {
                Iterator<Profile> it = result.getAllResults();
                while (it.hasNext()) {
                    Profile p =  it.next();
                    results.add(SecurityHelper.createAksessUser(p));
                }
            }

        } catch (no.kantega.security.api.common.SystemException e) {
            throw new SystemException("searchUsers failed", e);
        }

        return results;
    }


    public List<Role> getAllRoles() throws SystemException {
        List<Role> results = new ArrayList<Role>();

        Role everyone = new Role();
        everyone.setId(Aksess.getEveryoneRole());
        everyone.setName("Alle brukere");
        results.add(everyone);

        Role owner = new Role();
        owner.setId(Aksess.getOwnerRole());
        owner.setName("Eier (ansvarlig person)");
        results.add(owner);

        try {
            Iterator<no.kantega.security.api.role.Role> it = roleManager.getAllRoles();
            if (it != null) {
                while (it.hasNext()) {
                    no.kantega.security.api.role.Role role =  it.next();
                    results.add(SecurityHelper.createAksessRole(role));
                }
            }

        } catch (no.kantega.security.api.common.SystemException e) {
            throw new SystemException("getAllRoles failed", e);
        }

        return results;
    }

    /**
     * Returns an instance of User for the given userid.
     * @param userid
     * @return User or null if not found.
     * @throws SystemException
     * @throws IllegalArgumentException if userid is null or empty String
     */
    @Cacheable("UserCache")
    public User lookupUser(String userid) throws SystemException, IllegalArgumentException {
        if(isBlank(userid)) throw new IllegalArgumentException("Userid was null or empty String");
        try {
            Profile profile = profileManager.getProfileForUser(SecurityHelper.createApiIdentity(userid));
            return SecurityHelper.createAksessUser(profile);
        } catch (no.kantega.security.api.common.SystemException e) {
            throw new SystemException("Exception when retrieving profile for user " + userid, e);
        }
    }

    /**
     * Returns an instance of User for the given userid.
     * @param userid
     * @param useCache - If true, the userid is first looked up in the cache.
     * If the user is not found in the cache, an ordinary user lookup is performed and the user is added to the cache.
     * @return User or null if not found.
     * @throws SystemException
     * @deprecated use lookupUser(String userid)
     */
    @Deprecated
    public User lookupUser(String userid, boolean useCache) throws SystemException {
        return lookupUser(userid);
    }

    public List<Role> lookupRolesForUser(String userid) throws SystemException {
        List<Role> roles = new ArrayList<Role>();
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
            throw new SystemException("lookupUser failed", e);
        }
    }

    public List<String> lookupUsersWithRole(String roleId) throws SystemException {
        List<String> userIds = new ArrayList<String>();
        try {
            Iterator<Identity> it = roleManager.getUsersWithRole(SecurityHelper.createApiRole(roleId));
            if (it != null) {
                while (it.hasNext()) {
                    Identity role =  it.next();
                    if(role != null) {
                        userIds.add(role.getUserId());
                    }
                }
            }
            return userIds;
        } catch (no.kantega.security.api.common.SystemException e) {
            throw new SystemException("lookupUsersWithRole failed", e);
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
             throw new SystemException("lookupRole failed", e);
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
