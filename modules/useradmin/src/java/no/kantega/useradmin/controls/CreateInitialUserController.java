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
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.publishing.admin.viewcontroller.AdminController;
import no.kantega.publishing.common.Aksess;
import no.kantega.security.api.common.SystemException;
import no.kantega.security.api.identity.DefaultIdentity;
import no.kantega.security.api.password.PasswordManager;
import no.kantega.security.api.profile.DefaultProfile;
import no.kantega.security.api.profile.ProfileUpdateManager;
import no.kantega.security.api.role.DefaultRole;
import no.kantega.security.api.role.RoleUpdateManager;
import no.kantega.useradmin.model.ProfileManagementConfiguration;
import no.kantega.useradmin.model.RoleManagementConfiguration;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: Anders Skar, Kantega AS
 * Date: May 27, 2009
 * Time: 12:38:50 PM
 */
public class CreateInitialUserController extends AdminController {
    public static String FORM_VIEW = "/WEB-INF/jsp/useradmin/setup/initialuser.jsp";
    public static String CONFIRM_VIEW = "/WEB-INF/jsp/useradmin/setup/initialuserconfirm.jsp";
    public static String EXISTS_VIEW = "/WEB-INF/jsp/useradmin/setup/initialuserexists.jsp";
    public static String CANT_CREATE_VIEW = "/WEB-INF/jsp/useradmin/setup/initialusercantcreate.jsp";
    public static String NOT_AUTH_VIEW = "/WEB-INF/jsp/useradmin/setup/initialusernotauthorized.jsp";

    private String defaultDomain;
    private List profileConfiguration;
    private List roleConfiguration;
    private File tokenFile;

    private SecureRandom random = new SecureRandom();
    private Logger log = Logger.getLogger(getClass());

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();

        if (rolesExists()) {
            // Roles already exists - go to error page
            return new ModelAndView(EXISTS_VIEW, model);
        }

        // Check if is possible to create roles
        RoleManagementConfiguration rmc = getRoleConfiguration(defaultDomain);
        RoleUpdateManager roleUpdateManager = rmc.getRoleUpdateManager();
        if (roleUpdateManager == null) {
            // Roles already exists - go to error page
            return new ModelAndView(CANT_CREATE_VIEW, model);
        }


        boolean createUserAccount = false;
        ProfileManagementConfiguration pmc = getProfileConfiguration(defaultDomain);
        ProfileUpdateManager profileUpdateManager = pmc.getProfileUpdateManager();
        if (profileUpdateManager != null) {
            createUserAccount = true;
        }
        model.put("createUserAccount", createUserAccount);

        // No roles exists
        if (request.getMethod().equalsIgnoreCase("POST")) {
            RequestParameters param = new RequestParameters(request);

            String username = param.getString("username");
            String password = param.getString("password");
            String password2 = param.getString("password2");

            model.put("username", username);
            model.put("password", password); // Needed by CONFIRM_VIEW for automatic login


            boolean error = false;

            if (!isLocalhost(request)) {
                model.put("needsToken", Boolean.TRUE);
                String token = request.getParameter("token");
                if(!getInitialUserToken().equals(token)) {
                    model.put("errorToken", Boolean.TRUE);

                    error  = true;
                } else {
                    model.put("token", token);
                }

            }

            if (username == null || username.length() < 3) {
                model.put("errorUsername", Boolean.TRUE);
                error = true;

            }



            if (createUserAccount) {
                if (password == null || password.length() < 6 || !password.equalsIgnoreCase(password2)) {
                    model.put("errorPassword", Boolean.TRUE);
                    error=true;
                }
            }
            if(error) {
                return new ModelAndView(FORM_VIEW, model);
            }  else {

                if (!isLocalhost(request)) {
                    deleteInitialUserToken();
                }
                // Create admin role (and user if neccessary)
                createAdminRoleAndUser(username, password);

                // Show confirmation
                return new ModelAndView(CONFIRM_VIEW, model);
            }
        } else {
            if (!isLocalhost(request)) {
                createInitialUserToken();
                model.put("needsToken", Boolean.TRUE);
            }
            return new ModelAndView(FORM_VIEW, model);
        }
    }

    private synchronized void deleteInitialUserToken() {
        tokenFile.delete();
        if(tokenFile.getParentFile().list().length == 0) {
            tokenFile.getParentFile().delete();
        }
    }

    private synchronized String getInitialUserToken() {
        if(!tokenFile.exists()) {
            throw new IllegalStateException("Expected tokenFile to exist at this point: " + tokenFile);
        } else {
            try {
                return IOUtils.toString(new FileInputStream(tokenFile));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private synchronized void createInitialUserToken() {
        if(!this.tokenFile.exists()) {
            byte[] bytes = new byte[16];
            random.nextBytes(bytes);

            FileOutputStream out = null;
            try {
                tokenFile.getParentFile().mkdirs();
                out = new FileOutputStream(tokenFile);
                IOUtils.write(Base64.encodeBase64(bytes), out);
            } catch (IOException e) {
                throw new RuntimeException("Error writing token to file", e);
            } finally {
                try {
                    if(out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    log.error("Error closing file output stream", e);
                }
            }
        }
    }

    private void createAdminRoleAndUser(String username, String password) throws SystemException {
        // Create default roles and users
        RoleManagementConfiguration rmc = getRoleConfiguration(defaultDomain);
        RoleUpdateManager roleUpdateManager = rmc.getRoleUpdateManager();
        if (roleUpdateManager != null) {
            // Create role
            DefaultRole role = new DefaultRole();
            role.setDomain(defaultDomain);
            role.setId(Aksess.getAdminRole());
            role.setName(Aksess.getAdminRole());
            roleUpdateManager.saveOrUpdateRole(role);


            DefaultIdentity identity = new DefaultIdentity();
            identity.setDomain(defaultDomain);
            identity.setUserId(username);

            ProfileManagementConfiguration pmc = getProfileConfiguration(defaultDomain);
            ProfileUpdateManager profileUpdateManager = pmc.getProfileUpdateManager();
            if (profileUpdateManager != null) {
                // Create user
                DefaultProfile profile = new DefaultProfile();
                profile.setSurname(username);
                profile.setIdentity(identity);

                profileUpdateManager.saveOrUpdateProfile(profile);

                PasswordManager passwordManager = pmc.getPasswordManager();
                passwordManager.setPassword(identity, password, password);
            }

            // Adds user to role
            roleUpdateManager.addUserToRole(identity, role);

        }
    }


    private boolean rolesExists() throws SystemException, ConfigurationException {
        RoleManagementConfiguration rmc = getRoleConfiguration(defaultDomain);
        if (rmc == null) {
            throw new ConfigurationException("Default domain specified does not exists in security configuration file");
        }
        Iterator roles = rmc.getRoleManager().getAllRoles();
        return roles.hasNext();
    }

    private ProfileManagementConfiguration getProfileConfiguration(String domain) {
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

    private RoleManagementConfiguration getRoleConfiguration(String domain) {
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

    private boolean isLocalhost(HttpServletRequest request) {
        if (request.getRemoteAddr().equals("127.0.0.1") || request.getRemoteAddr().equals("0:0:0:0:0:0:0:1") || request.getRemoteAddr().equals("localhost")) {
            return true;
        }

        return false;
    }

    public void setProfileConfiguration(List profileConfigurations) {
        this.profileConfiguration = profileConfigurations;
    }

    public void setRoleConfiguration(List roleConfiguration) {
        this.roleConfiguration = roleConfiguration;
    }

    public void setDefaultDomain(String defaultDomain) {
        this.defaultDomain = defaultDomain;
    }

    public void setTokenFile(File tokenFile) {
        this.tokenFile = tokenFile;
    }
}
