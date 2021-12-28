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
import no.kantega.publishing.security.data.LoginRestrictor;
import no.kantega.security.api.common.SystemException;
import no.kantega.security.api.identity.DefaultIdentity;
import no.kantega.security.api.identity.Identity;
import no.kantega.security.api.password.PasswordManager;
import no.kantega.security.api.password.ResetPasswordTokenManager;
import no.kantega.security.api.profile.Profile;
import no.kantega.security.api.role.RoleManager;
import no.kantega.security.api.twofactorauth.LoginToken;
import no.kantega.security.api.twofactorauth.LoginTokenManager;
import no.kantega.security.api.twofactorauth.LoginTokenSender;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.kantega.commons.util.URLHelper.getUrlWithHttps;
import static org.apache.commons.lang3.StringUtils.defaultString;

public class LoginAction extends AbstractLoginAction {
    private static final Logger log = LoggerFactory.getLogger(LoginAction.class);

    private LoginRestrictor userLoginRestrictor;
    private LoginRestrictor ipLoginRestrictor;

    private LoginTokenManager loginTokenManager;
    private LoginTokenSender loginTokenSender;

    @Autowired private EventLog eventLog;
    @Autowired private RememberMeHandler rememberMeHandler;

    @Value("${security.login.usessl:false}")
    private boolean loginRequireSsl;

    private String loginView = null;

    private boolean rolesExists = false;
    private String twofactorAuthView;

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
        if (!rolesExists) {
            return new ModelAndView(new RedirectView(Aksess.getContextPath() + "/CreateInitialUser.action"));
        }

        ResetPasswordTokenManager resetPasswordTokenManager = getResetPasswordTokenManager();

        Map<String, Object> model = new HashMap<>();
        model.put("redirect", StringEscapeUtils.escapeHtml4(redirect));
        model.put("username", StringEscapeUtils.escapeHtml4(username));
        model.put("loginLayout", getLoginLayout());

        if (Aksess.isSecurityAllowPasswordReset() && resetPasswordTokenManager != null) {
            model.put("allowPasswordReset", true);
        }

        if (username != null && password != null)  {
            Identity identity = DefaultIdentity.withDomainAndUserId(domain, username);

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
                    if (twoFactorAuthenticationEnabled()) {
                        return handleTwoFactorAuthentication(identity, model);
                    } else {
                        LoginHelper.registerSuccessfulLogin(userLoginRestrictor, ipLoginRestrictor, request, username, domain);

                        boolean rememberMeEnabled = configuration.getBoolean("security.login.rememberme.enabled", false);
                        if (rememberMeEnabled && rememberMe != null && rememberMe.equals("on")) {
                            rememberMeHandler.rememberUser(response, username, domain);
                        }

                        return new ModelAndView(new RedirectView(redirect));
                    }
                } else {
                    // Register failed login
                    userLoginRestrictor.registerLoginAttempt(username, false);
                    ipLoginRestrictor.registerLoginAttempt(request.getRemoteAddr(), false);

                    eventLog.log(username, request.getRemoteAddr(), Event.FAILED_LOGIN, username, null);
                    model.put("loginfailed", Boolean.TRUE);
                }
            }
        }

        return new ModelAndView(loginView, model);
    }

    private ModelAndView handleTwoFactorAuthentication(Identity identity, Map<String, Object> model) throws SystemException {
        Profile profile = getProfileManager().getProfileForUser(identity);
        LoginToken loginToken = loginTokenManager.generateLoginToken(identity);
        try {
            loginTokenSender.sendTokenToUser(profile, loginToken);
        } catch (IllegalArgumentException e) {
            log.error(profile.getIdentity().getDomain() + " " + profile.getIdentity().getUserId() + " does not have required recipient attribute", e);
            model.put("missingrecipientattribute", true);
        } catch (SystemException e) {
            log.error("Error sending logintoken", e);
            model.put("sendtokenfailed", true);
        }
        model.put("profile", profile);
        return new ModelAndView(twofactorAuthView, model);
    }

    private boolean twoFactorAuthenticationEnabled() {
        boolean twoFactorAuthenticationEnabled = configuration.getBoolean("security.twoFactorAuthenticationEnabled", false);
        if(twoFactorAuthenticationEnabled && (loginTokenManager == null || loginTokenSender == null)){
            throw new IllegalStateException("security.twoFactorAuthenticationEnabled = true, but loginTokenManager or loginTokenSender is null!");
        }
        return twoFactorAuthenticationEnabled;
    }

    private ModelAndView redirectToSecure(HttpServletRequest request) {
        return new ModelAndView(new RedirectView(getUrlWithHttps(request)));
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

    public void setTwofactorAuthView(String twofactorAuthView) {
        this.twofactorAuthView = twofactorAuthView;
    }

    @Autowired(required = false)
    public void setLoginTokenManager(LoginTokenManager loginTokenManager) {
        this.loginTokenManager = loginTokenManager;
    }

    @Autowired(required = false)
    public void setLoginTokenSender(LoginTokenSender loginTokenSender) {
        this.loginTokenSender = loginTokenSender;
    }

    @Autowired
    public void setRoleManagers(List<RoleManager> roleManagers) throws SystemException {
        for (RoleManager roleManager : roleManagers) {
            if (roleManager.getAllRoles().hasNext()) {
                rolesExists = true;
            }
        }
    }
}
