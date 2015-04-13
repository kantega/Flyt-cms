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

package no.kantega.publishing.security.util;

import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.security.data.Role;
import no.kantega.publishing.security.data.User;
import no.kantega.security.api.identity.DefaultIdentity;
import no.kantega.security.api.identity.Identity;
import no.kantega.security.api.profile.DefaultProfile;
import no.kantega.security.api.profile.Profile;
import no.kantega.security.api.role.DefaultRole;

public class SecurityHelper {
    public static User createAksessUser(Profile userProfile) {
        if (userProfile == null) {
            return null;
        }

        User user = new User();
        Identity identity = userProfile.getIdentity();
        if (Aksess.getDefaultSecurityDomain().equals(identity.getDomain())) {
            user.setId(identity.getUserId());
        } else {
            user.setId(identity.getDomain() + ":" + identity.getUserId());
        }

        if (userProfile.getGivenName() != null && userProfile.getGivenName().length() > 0) {
            user.setGivenName(userProfile.getGivenName());
        }

        if (userProfile.getSurname() != null && userProfile.getSurname().length() > 0) {
            user.setSurname(userProfile.getSurname());
        }

        user.setEmail(userProfile.getEmail());
        user.setDepartment(userProfile.getDepartment());
        user.setAttributes(userProfile.getRawAttributes());

        return user;
    }

    public static Role createAksessRole(no.kantega.security.api.role.Role role) {
        if (role == null) {
            return null;
        }

        Role aksessRole = new Role();
        if (Aksess.getDefaultSecurityDomain().equals(role.getDomain())) {
            aksessRole.setId(role.getId());
        } else {
            aksessRole.setId(role.getDomain() + ":" + role.getId());
        }
        aksessRole.setName(role.getName());

        return aksessRole;
    }

    public static Identity createApiIdentity(String aksessUserId) {
        String userId;
        String domain;
        if (!aksessUserId.contains(":")) {
            userId = aksessUserId;
            domain = Aksess.getDefaultSecurityDomain();
        } else {
            userId = aksessUserId.substring(aksessUserId.indexOf(":") + 1, aksessUserId.length());
            domain = aksessUserId.substring(0, aksessUserId.indexOf(":"));
        }

        return DefaultIdentity.withDomainAndUserId(domain, userId);
    }

    public static no.kantega.security.api.role.Role createApiRole(String aksessRoleId) {
        String roleId;
        String domain;
        if (!aksessRoleId.contains(":")) {
            roleId = aksessRoleId;
            domain = Aksess.getDefaultSecurityDomain();
        } else {
            roleId = aksessRoleId.substring(aksessRoleId.indexOf(":") + 1, aksessRoleId.length());
            domain = aksessRoleId.substring(0, aksessRoleId.indexOf(":"));
        }

        DefaultRole role = new DefaultRole();
        role.setId(roleId);
        role.setDomain(domain);

        return role;
    }

    public static Profile createApiProfile(User user) {
        if(user==null){
            return null;
        }
        DefaultProfile profile = new DefaultProfile();
        profile.setIdentity(createApiIdentity(user.getId()));
        profile.setRawAttributes(user.getAttributes());
        profile.setDepartment(user.getDepartment());
        profile.setEmail(user.getEmail());
        profile.setGivenName(user.getGivenName());
        profile.setSurname(user.getSurname());
        return profile;
    }
}
