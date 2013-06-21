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

import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.log.Log;
import no.kantega.commons.util.Base64;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.eventlog.Event;
import no.kantega.publishing.eventlog.EventLog;
import no.kantega.publishing.security.RememberMeHandler;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.LoginRestrictor;
import no.kantega.publishing.spring.RootContext;
import no.kantega.security.api.common.SystemException;
import no.kantega.security.api.identity.DefaultIdentity;
import no.kantega.security.api.identity.DefaultIdentityResolver;
import no.kantega.security.api.identity.IdentityResolver;
import no.kantega.security.api.password.PasswordManager;
import no.kantega.security.api.password.ResetPasswordTokenManager;
import no.kantega.security.api.role.RoleManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginAction extends AbstractLoginAction {

    private LoginRestrictor userLoginRestrictor;
    private LoginRestrictor ipLoginRestrictor;

    @Autowired
    private EventLog eventLog;

    @Autowired
    private RememberMeHandler rememberMeHandler;
    private String loginView = null;

    private boolean rolesExists = false;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = request.getParameter("j_username");
        String domain = request.getParameter("j_domain");
        String password = request.getParameter("j_password");
        String redirect = request.getParameter("redirect");
        String rememberMe = request.getParameter("remember_me");

        if (redirect == null || redirect.length() == 0) {
            redirect = Aksess.getContextPath();
        }

        // If login page is secure, redirect to secure page after logging in
        if (request.isSecure() && redirect.startsWith("http:")) {
            redirect = redirect.replaceFirst("http:", "https:");
        }

        // Checks if no roles exists and redirects to setup page
        if (!rolesExists()) {
            return new ModelAndView(new RedirectView(Aksess.getContextPath() + "/CreateInitialUser.action"));
        }

        PasswordManager passwordManager = getPasswordManager(domain);
        ResetPasswordTokenManager resetPasswordTokenManager = getResetPasswordTokenManager();

        Map model = new HashMap();

        if (Aksess.isSecurityAllowPasswordReset() && resetPasswordTokenManager != null) {
            model.put("allowPasswordReset", true);
        }

        if (passwordManager == null) {
            throw new ConfigurationException("PasswordManager == null");
        }

        if (username != null && password != null)  {
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
                    Log.info(this.getClass().getName(), "Too many attempts. IP-address is blocked from login:" + request.getRemoteAddr(), null, null);
                }
            } else {
                if (passwordManager.verifyPassword(identity, password)) {
                    // Register successful login
                    userLoginRestrictor.registerLoginAttempt(username, true);
                    ipLoginRestrictor.registerLoginAttempt(request.getRemoteAddr(), true);

                    HttpSession session = request.getSession(true);
                    IdentityResolver resolver = SecuritySession.getInstance(request).getRealm().getIdentityResolver();

                    if (rememberMe != null && rememberMe.equals("on")) {
                        response = rememberMeHandler.setRememberMe(response, username, domain);
                    }

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

                    eventLog.log(username, request.getRemoteAddr(), Event.FAILED_LOGIN, username, null);
                    model.put("loginfailed", Boolean.TRUE);
                }
            }
        } else {
            boolean rememberMeEnabled = Aksess.getConfiguration().getBoolean("security.login.rememberme.enabled", false);
            String[] usernameAndDomain = rememberMeHandler.hasRememberMe(request);
            if (rememberMeEnabled && usernameAndDomain != null) {
                username = usernameAndDomain[0];
                domain = usernameAndDomain[1];

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
            }
        }

        model.put("redirect", redirect);
        model.put("username", username);
        model.put("loginLayout", getLoginLayout());

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
