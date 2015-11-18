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

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.useradmin.model.ProfileManagementConfiguration;
import no.kantega.useradmin.model.RoleManagementConfiguration;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public abstract class AbstractUserAdminController extends AbstractController {

    protected List<ProfileManagementConfiguration> profileConfiguration;
    protected List<RoleManagementConfiguration> roleConfiguration;

    public abstract ModelAndView doHandleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;

    public ProfileManagementConfiguration getProfileConfiguration(String domain) {
        if (profileConfiguration == null) {
            return null;
        }

        for (ProfileManagementConfiguration pmc : profileConfiguration) {
            if (pmc.getDomain().equalsIgnoreCase(domain)) {
                return pmc;
            }
        }

        return profileConfiguration.get(0);
    }

    public RoleManagementConfiguration getRoleConfiguration(String domain) {
        if (roleConfiguration == null) {
            return null;
        }

        for (RoleManagementConfiguration rmc : roleConfiguration) {
            if (rmc.getDomain().equalsIgnoreCase(domain)) {
                return rmc;
            }
        }

        return roleConfiguration.get(0);
    }

    public List<ProfileManagementConfiguration> getProfileConfiguration() {
        return profileConfiguration;
    }

    public void setProfileConfiguration(List<ProfileManagementConfiguration> profileConfigurations) {
        this.profileConfiguration = profileConfigurations;
    }

    public List<RoleManagementConfiguration> getRoleConfiguration() {
        return roleConfiguration;
    }

    public void setRoleConfiguration(List<RoleManagementConfiguration> roleConfiguration) {
        this.roleConfiguration = roleConfiguration;
    }

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SecuritySession session = SecuritySession.getInstance(request);
        if (!session.isUserInRole(Aksess.getAdminRole())) {
            throw new NotAuthorizedException("Need admin role");
        }

        return doHandleRequest(request, response);
    }
}
