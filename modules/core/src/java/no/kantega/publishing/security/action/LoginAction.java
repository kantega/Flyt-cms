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

package no.kantega.publishing.security.action;

import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.enums.Event;
import no.kantega.publishing.common.service.impl.EventLog;
import no.kantega.publishing.spring.RootContext;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.LoginRestrictor;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.log.Log;
import no.kantega.security.api.identity.DefaultIdentity;
import no.kantega.security.api.identity.DefaultIdentityResolver;
import no.kantega.security.api.identity.IdentityResolver;
import no.kantega.security.api.password.PasswordManager;
import no.kantega.security.api.role.RoleManager;
import no.kantega.security.api.common.SystemException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.HashMap;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.context.ApplicationContext;

public class LoginAction implements Controller {

    private LoginRestrictor userLoginRestrictor;
    private LoginRestrictor ipLoginRestrictor;

    private String loginView = null;

    private boolean rolesExists = false;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = request.getParameter("j_username");
        String domain   = request.getParameter("j_domain");
        String password = request.getParameter("j_password");
        String redirect = request.getParameter("redirect");

        if (redirect == null || redirect.length() == 0) {
            redirect =  Aksess.getContextPath();
        }

        // If login page is secure, redirect to secure page after logging in
        if (request.isSecure() && redirect.startsWith("http:")) {
            redirect = redirect.replaceFirst("http:", "https:");
        }

        // Checks if no roles exists and redirects to setup page
        if (!rolesExists()) {
            return new ModelAndView(new RedirectView(Aksess.getContextPath() + "/CreateInitialUser.action"));
        }

        PasswordManager passwordManager = null;

        Map model = new HashMap();

        ApplicationContext context = RootContext.getInstance();
        Map managers = context.getBeansOfType(PasswordManager.class);
        if (managers != null) {
            for (Object o : managers.values()) {
                passwordManager = (PasswordManager) o;
                if (passwordManager.getDomain().equalsIgnoreCase(domain)) {
                    break;
                }
            }
        }

        if (passwordManager == null) {
            throw new ConfigurationException("PasswordManager == null");
        }

        if (username != null && password != null) {
            DefaultIdentity identity = new DefaultIdentity();
            identity.setUserId(username);
            identity.setDomain(domain);

            boolean blockedUser = userLoginRestrictor.isBlocked(username);
            boolean blockedIp = ipLoginRestrictor.isBlocked(request.getRemoteAddr());

            if (blockedUser || blockedIp) {
                // User or ip should be blocked, to many login attempts
                if (blockedUser) {
                    model.put("blockedUser", Boolean.TRUE);
                    Log.info(this.getClass().getName(), "Too many attempts. User is blocked from login:" + username, null, null);
                } else {
                    model.put("blockedIP", Boolean.TRUE);
                    Log.info(this.getClass().getName(), "Too many attempts. IP-adress is blocked from login:" + request.getRemoteAddr(), null, null);
                }
            } else {
                if (passwordManager.verifyPassword(identity, password)) {
                    // Register successful login
                    userLoginRestrictor.registerLoginAttempt(username, true);
                    ipLoginRestrictor.registerLoginAttempt(request.getRemoteAddr(), true);

                    HttpSession session = request.getSession(true);
                    IdentityResolver resolver = SecuritySession.getInstance(request).getRealm().getIdentityResolver();

                    // Set session logon info
                    session.setAttribute(resolver.getAuthenticationContext() + DefaultIdentityResolver.SESSION_IDENTITY_NAME, username);
                    session.setAttribute(resolver.getAuthenticationContext() + DefaultIdentityResolver.SESSION_IDENTITY_DOMAIN, domain);

                    // Finish login by getting instance
                    SecuritySession.getInstance(request);

                    return new ModelAndView(new RedirectView(redirect));
                } else {
                    // Register failed login
                    userLoginRestrictor.registerLoginAttempt(username, false);
                    ipLoginRestrictor.registerLoginAttempt(request.getRemoteAddr(), false);

                    EventLog.log(username, request.getRemoteAddr(), Event.FAILED_LOGIN, username, null);
                    model.put("loginfailed", Boolean.TRUE);
                }
            }
        }
       
        model.put("redirect", redirect);
        model.put("username", username);

        return new ModelAndView(loginView, model);
    }

    public void setLoginView(String loginView) {
        this.loginView = loginView;
    }

    public void setUserLoginRestrictor(LoginRestrictor userLoginRestrictor) {
        this.userLoginRestrictor = userLoginRestrictor;
    }

    public void setIpLoginRestrictor(LoginRestrictor ipLoginRestrictor) {
        this.ipLoginRestrictor = ipLoginRestrictor;
    }


    private boolean rolesExists() throws SystemException {
        if (rolesExists) {
            return true;
        }
        
        ApplicationContext context = RootContext.getInstance();
        Map managers = context.getBeansOfType(RoleManager.class);
        if (managers != null) {
            for (Object o : managers.values()) {
                RoleManager roleManager = (RoleManager) o;
                if (roleManager.getAllRoles().hasNext()) {
                    rolesExists = true;
                    return true;
                }
            }
        }

        return false;
    }
}
