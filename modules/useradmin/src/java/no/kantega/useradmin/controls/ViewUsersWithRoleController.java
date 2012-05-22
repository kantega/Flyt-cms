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
import no.kantega.security.api.identity.Identity;
import no.kantega.security.api.profile.DefaultProfile;
import no.kantega.security.api.profile.Profile;
import no.kantega.security.api.profile.ProfileManager;
import no.kantega.security.api.role.DefaultRoleId;
import no.kantega.security.api.role.RoleManager;
import no.kantega.useradmin.model.ProfileManagementConfiguration;
import no.kantega.useradmin.model.ProfileSet;
import no.kantega.useradmin.model.RoleManagementConfiguration;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * User: Anders Skar, Kantega AS
 * Date: Jul 19, 2007
 * Time: 2:41:31 PM
 */
public class ViewUsersWithRoleController extends AbstractUserAdminController {
    public ModelAndView doHandleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);

        String id = param.getString("roleId");
        String domain = param.getString("domain");

        Map model = new HashMap();
        model.put("roleId", id);
        model.put("roleDomain", domain);


        DefaultRoleId role = new DefaultRoleId();
        role.setId(id);
        role.setDomain(domain);

        List<ProfileSet> profileSets = new ArrayList<ProfileSet>();

        List<RoleManagementConfiguration> roleConfigs = getRoleConfiguration();

        /**
         * Hent alle rollesett brukeren har og legg dem inn lista
         * Dersom et rollesett har en UpdateRoleManager, sett flagg at disse er redigerbare
         */
        for (RoleManagementConfiguration roleConfig : roleConfigs) {
            ProfileManagementConfiguration profileConfig = getProfileConfiguration(domain);

            RoleManager roleManager = roleConfig.getRoleManager();

            ProfileSet profileSet = new ProfileSet();
            profileSet.setDomain(profileConfig.getDomain());
            profileSet.setDescription(profileConfig.getDescription());

            if (roleConfig.getRoleUpdateManager() != null) {
                profileSet.setIsEditable(true);
            }

            List<Profile> profiles = new ArrayList<Profile>();
            Iterator identities = roleManager.getUsersWithRole(role);
            while (identities.hasNext()) {
                Identity identity = (Identity) identities.next();

                // Ønsker å vise fullt navn for de som har brukerprofil
                ProfileManager profileManager = profileConfig.getProfileManager();
                Profile p = profileManager.getProfileForUser(identity);

                if (p != null) {
                    profiles.add(p);
                } else {
                    DefaultProfile tmp = new DefaultProfile();
                    tmp.setIdentity(identity);
                    tmp.setGivenName(identity.getUserId());
                    profiles.add(tmp);
                }
            }

            profileSet.setProfiles(profiles.iterator());

            profileSets.add(profileSet);
        }

        model.put("profileSets", profileSets);

        return new ModelAndView("/role/userswithrole", model);
    }
}

