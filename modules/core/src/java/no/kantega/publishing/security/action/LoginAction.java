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
import no.kantega.publishing.api.security.RememberMeHandler;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.eventlog.Event;
import no.kantega.publishing.eventlog.EventLog;
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
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static no.kantega.commons.util.URLHelper.getUrlWithHttps;
import static org.apache.commons.lang3.StringUtils.defaultString;

public class LoginAction extends AbstractLoginAction {
    private static final Logger log = LoggerFactory.getLogger(LoginAction.class);

    private LoginRestrictor userLoginRestrictor;
    private LoginRestrictor ipLoginRestrictor;

    @Autowired private EventLog eventLog;
    @Autowired private RememberMeHandler rememberMeHandler;

    @Value("${security.login.usessl:false}")
    private boolean loginRequireSsl;

    private String loginView = null;

    private boolean rolesExists = false;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = request.getParameter("j_username");
        String domain = request.getParameter("j_domain");
        String password = request.getParameter("j_password");
        String redirect = defaultString(request.getParameter("redirect"), Aksess.getContextPath());
        String rememberMe = request.getParameter("remember_me");

        if(loginRequireSsl && !request.isSecure()){
            return redirectToSecure(request);
        }

        // If login page is secure, redirect to secure page after logging in
        if (request.isSecure() && redirect.startsWith("http:")) {
            redirect = redirect.replaceFirst("http:", "https:");
        }

        // Checks if no roles exists and redirects to setup page
        if (!rolesExists()) {
            return new ModelAndView(new RedirectView(Aksess.getContextPath() + "/CreateInitialUser.action"));
        }

        ResetPasswordTokenManager resetPasswordTokenManager = getResetPasswordTokenManager();

        Map<String, Object> model = new HashMap<>();

        if (Aksess.isSecurityAllowPasswordReset() && resetPasswordTokenManager != null) {
            model.put("allowPasswordReset", true);
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
                    log.info( "Too many attempts. User is blocked from login:" + username);
                } else {
                    model.put("blockedIP", Boolean.TRUE);
                    log.info( "Too many attempts. IP-adress is blocked from login:" + request.getRemoteAddr());
                }
            } else {
                PasswordManager passwordManager = getPasswordManager(domain);

                if (passwordManager == null) {
                    throw new ConfigurationException("PasswordManager == null for domain «" + domain + "»");
                }
                if (passwordManager.verifyPassword(identity, password)) {
                    log.info("Verified password for " + identity.getUserId());
                    registerSuccessfulLogin(request, username, domain);

                    boolean rememberMeEnabled = configuration.getBoolean("security.login.rememberme.enabled", false);
                    if (rememberMeEnabled && rememberMe != null && rememberMe.equals("on")) {
                        rememberMeHandler.rememberUser(response, username, domain);
                    }

                    return new ModelAndView(new RedirectView(redirect));
                } else {
                    // Register failed login
                    userLoginRestrictor.registerLoginAttempt(username, false);
                    ipLoginRestrictor.registerLoginAttempt(request.getRemoteAddr(), false);

                    eventLog.log(username, request.getRemoteAddr(), Event.FAILED_LOGIN, username, null);
                    model.put("loginfailed", Boolean.TRUE);
                }
            }
        }

        model.put("redirect", StringEscapeUtils.escapeHtml(redirect));
        model.put("username", StringEscapeUtils.escapeHtml(username));
        model.put("loginLayout", getLoginLayout());

        return new ModelAndView(loginView, model);
    }

    private ModelAndView redirectToSecure(HttpServletRequest request) {
        return new ModelAndView(new RedirectView(getUrlWithHttps(request)));
    }

    private void registerSuccessfulLogin(HttpServletRequest request, String username, String domain) {

        userLoginRestrictor.registerLoginAttempt(username, true);
        ipLoginRestrictor.registerLoginAttempt(request.getRemoteAddr(), true);

        HttpSession session = request.getSession(true);
        IdentityResolver resolver = SecuritySession.getInstance(request).getRealm().getIdentityResolver();

        // Set session logon info
        session.setAttribute(resolver.getAuthenticationContext() + DefaultIdentityResolver.SESSION_IDENTITY_NAME, username);
        session.setAttribute(resolver.getAuthenticationContext() + DefaultIdentityResolver.SESSION_IDENTITY_DOMAIN, domain);

        // Finish login by getting instance
        SecuritySession.getInstance(request);
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
        Map<String, RoleManager> managers = context.getBeansOfType(RoleManager.class);
        if (managers != null) {
            for (RoleManager roleManager : managers.values()) {
                if (roleManager.getAllRoles().hasNext()) {
                    rolesExists = true;
                    return true;
                }
            }
        }

        return false;
    }
}
