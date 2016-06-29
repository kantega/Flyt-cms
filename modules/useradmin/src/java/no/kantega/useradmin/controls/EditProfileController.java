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
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.security.api.identity.DefaultIdentity;
import no.kantega.security.api.password.PasswordManager;
import no.kantega.security.api.profile.DefaultProfile;
import no.kantega.security.api.profile.Profile;
import no.kantega.security.api.profile.ProfileManager;
import no.kantega.security.api.profile.ProfileUpdateManager;
import no.kantega.useradmin.model.ProfileManagementConfiguration;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


public class EditProfileController extends AbstractUserAdminController {
    public ModelAndView doHandleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);

        String id = param.getString("userId", true);
        String domain = param.getString("domain");
        String save = param.getString("save");

        ValidationErrors errors = new ValidationErrors();

        Map<String, Object> model = new HashMap<>();
        model.put("domain", domain);
        model.put("errors", errors);
        model.put("configurations", getProfileConfiguration());
        model.put("numConfigurations", getProfileConfiguration().size());

        ProfileManagementConfiguration config = getProfileConfiguration(domain);
        ProfileManager manager = config.getProfileManager();

        if (config.getProfileUpdateManager() != null) {
            model.put("canEdit", Boolean.TRUE);
        }

        if (save != null) {
            // Oppdater profil
            boolean isNew = param.getBoolean("isNew", true);

            DefaultIdentity identity = new DefaultIdentity();
            identity.setDomain(domain);
            identity.setUserId(id);

            DefaultProfile profile = new DefaultProfile();
            profile.setIdentity(identity);

            profile.setGivenName(param.getString("givenName", true));
            if(profile.getGivenName() != null) {
                profile.setGivenName(profile.getGivenName());
            }
            profile.setSurname(param.getString("surname", true));
            if(profile.getSurname() != null) {
                profile.setSurname(profile.getSurname());
            }
            profile.setEmail(param.getString("email", true));
            profile.setDepartment(param.getString("department", true));

            model.put("profile", profile);
            model.put("isNew", isNew);

            // Hent eksisterende profil hvis den finnes
            Profile oldProfile = manager.getProfileForUser(identity);
            if (oldProfile != null) {
                if (isNew) {
                    model.put("errors", errors);
                    errors.add(null, "useradmin.profile.duplicateuserid");
                    model.put("error", "useradmin.profile.duplicateuserid");
                    return new ModelAndView("profile/edit", model);
                } else {
                    profile.setRawAttributes(oldProfile.getRawAttributes());
                }
            }

            if (id == null || id.length() < 3) {
                errors.add(null, "useradmin.profile.useridmissing");
                return new ModelAndView("profile/edit", model);
            } else if (profile.getGivenName() == null || profile.getGivenName().length() < 1) {
                errors.add(null, "useradmin.profile.givennameepty");
                return new ModelAndView("profile/edit", model);
            }

            // Lagre profil
            ProfileUpdateManager updateManager = config.getProfileUpdateManager();
            if (updateManager != null) {
                updateManager.saveOrUpdateProfile(profile);
            }


            // Redirect til innlegging av passord eller brukeroversikt
            Map<String, Object> redirectModel = new HashMap<>();
            redirectModel.put("domain", domain);

            PasswordManager passwordManager = config.getPasswordManager();
            if (isNew && passwordManager != null) {
                // Legg inn passord for nye brukere
                redirectModel.put("userId", profile.getIdentity().getUserId());
                return new ModelAndView(new RedirectView("../password/reset"), redirectModel);
            } else {
                // Gå til brukeroversikt
                redirectModel.put("userId", profile.getIdentity().getUserId());
                redirectModel.put("message", "useradmin.profile.saved");
                return new ModelAndView(new RedirectView("search"), redirectModel);
            }

        } else {
            DefaultIdentity identity = new DefaultIdentity();
            identity.setDomain(domain);

            // Vis profil
            if (id != null && id.length() > 0) {
                identity.setUserId(id);
                model.put("profile", manager.getProfileForUser(identity));
                model.put("isNew", Boolean.FALSE);
            } else {
                DefaultProfile profile = new DefaultProfile();
                profile.setIdentity(identity);
                model.put("profile", profile);
                model.put("isNew", Boolean.TRUE);
            }

            return new ModelAndView("profile/edit", model);
        }
    }
}
