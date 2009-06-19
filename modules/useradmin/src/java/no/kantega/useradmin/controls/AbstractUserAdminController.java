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
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * User: Anders Skar, Kantega AS
 * Date: Jun 26, 2007
 * Time: 2:05:18 PM
 */
public abstract class AbstractUserAdminController implements Controller {
    private final static String SOURCE = "AbstractUserAdminController";

    protected List profileConfiguration;
    protected List roleConfiguration;

    public abstract ModelAndView doHandleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;

    public ProfileManagementConfiguration getProfileConfiguration(String domain) {
        if (profileConfiguration == null) {
            return null;
        }

        for (int i = 0; i < profileConfiguration.size(); i++) {
            ProfileManagementConfiguration pmc = (ProfileManagementConfiguration) profileConfiguration.get(i);
            if (pmc.getDomain().equalsIgnoreCase(domain)) {
                return pmc;
            }
        }

        return (ProfileManagementConfiguration)profileConfiguration.get(0);
    }

    public RoleManagementConfiguration getRoleConfiguration(String domain) {
        if (roleConfiguration == null) {
            return null;
        }

        for (int i = 0; i < roleConfiguration.size(); i++) {
            RoleManagementConfiguration rmc = (RoleManagementConfiguration) roleConfiguration.get(i);
            if (rmc.getDomain().equalsIgnoreCase(domain)) {
                return rmc;
            }
        }

        return (RoleManagementConfiguration)roleConfiguration.get(0);
    }

    public List getProfileConfiguration() {
        return profileConfiguration;
    }

    public void setProfileConfiguration(List profileConfigurations) {
        this.profileConfiguration = profileConfigurations;
    }

    public List getRoleConfiguration() {
        return roleConfiguration;
    }

    public void setRoleConfiguration(List roleConfiguration) {
        this.roleConfiguration = roleConfiguration;
    }

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SecuritySession session = SecuritySession.getInstance(request);
        if (!session.isUserInRole(Aksess.getAdminRole())) {
            throw new NotAuthorizedException("Need admin role", SOURCE);
        }

        return doHandleRequest(request, response);
    }
}
