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
import no.kantega.security.api.identity.Identity;
import no.kantega.security.api.profile.Profile;
import no.kantega.security.api.profile.ProfileManager;
import no.kantega.security.api.search.SearchResult;
import no.kantega.useradmin.model.ProfileManagementConfiguration;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchProfilesController extends AbstractUserAdminController  {

    public ModelAndView doHandleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);

        String domain = param.getString("domain");
        String query  = param.getString("q");
        String userId  = param.getString("userId");

        Map<String, Object> model = new HashMap<>();

        ProfileManagementConfiguration config = getProfileConfiguration(domain);
        if (config != null) {
            model.put("domain", config.getDomain());
            model.put("profileConfigurations", getProfileConfiguration());
            model.put("numProfileConfigurations", getProfileConfiguration().size());
            if (config.getProfileUpdateManager() != null) {
                model.put("canEdit", Boolean.TRUE);
            }
            if (config.getPasswordManager() != null) {
                model.put("canSetPassword", Boolean.TRUE);
            }
            ProfileManager manager = config.getProfileManager();

            if (userId != null && userId.length() > 0) {
                // Opprettet en bruker, vis denne
                List<Profile> users = new ArrayList<>();
                Identity identity = DefaultIdentity.withDomainAndUserId(domain, userId);
                Profile p = manager.getProfileForUser(identity);
                if (p != null) {
                    users.add(p);
                    model.put("users", users.iterator());
                }
            } else {
                // Søkt etter bruker eller vis alle
                SearchResult result = manager.searchProfiles(query == null ? "" : query);
                if (result != null) {
                    model.put("users", result.getAllResults());
                }
                model.put("query", query);
            }
        }

        return new ModelAndView("profile/search", model);
    }
}
