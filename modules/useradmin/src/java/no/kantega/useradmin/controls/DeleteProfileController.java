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

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.useradmin.model.ProfileManagementConfiguration;
import no.kantega.security.api.profile.ProfileManager;
import no.kantega.security.api.profile.Profile;
import no.kantega.security.api.profile.ProfileUpdateManager;
import no.kantega.security.api.identity.DefaultIdentity;

import java.util.Map;
import java.util.HashMap;
import java.net.URLEncoder;

/**
 * User: Anders Skar, Kantega AS
 * Date: Jul 11, 2007
 * Time: 3:14:13 PM
 */
public class DeleteProfileController extends AbstractUserAdminController {
    
    public ModelAndView doHandleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);

        String id = param.getString("userId");
        String domain = param.getString("domain");
        String confirm = param.getString("confirm");

        ProfileManagementConfiguration config = getProfileConfiguration(domain);
        ProfileManager manager = config.getProfileManager();

        DefaultIdentity identity = new DefaultIdentity();
        identity.setDomain(domain);
        identity.setUserId(id);

        Map model = new HashMap();
        if (confirm != null) {
            // Delete profile
            ProfileUpdateManager updateManager = config.getProfileUpdateManager();
            if (updateManager != null) {
                updateManager.deleteProfile(identity);
            }
            return new ModelAndView(new RedirectView("search?message=useradmin.profile.deleted&domain=" + URLEncoder.encode(domain, "iso-8859-1")));
        } else {
            // Confirm deletion
            Profile profile = manager.getProfileForUser(identity);
            model.put("profile", profile);
            return new ModelAndView("profile/delete", model);
        }
    }
}
