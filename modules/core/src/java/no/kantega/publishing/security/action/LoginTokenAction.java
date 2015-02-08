/*
 * Copyright 2014 Kantega AS
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

import no.kantega.publishing.security.data.PauseLoginRestrictor;
import no.kantega.security.api.identity.DefaultIdentity;
import no.kantega.security.api.identity.Identity;
import no.kantega.security.api.twofactorauth.DefaultLoginToken;
import no.kantega.security.api.twofactorauth.LoginTokenManager;
import no.kantega.security.api.twofactorauth.LoginTokenVerification;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginTokenAction {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private LoginTokenManager loginTokenManager;

    @Value("${security.twoFactorAuthenticationEnabled:false}")
    private boolean twoFactorAuthenticationEnabled;
    private String twofactorAuthView;
    private PauseLoginRestrictor userLoginRestrictor;
    private PauseLoginRestrictor ipLoginRestrictor;
    private PauseLoginRestrictor loginTokenRestrictor;

    @PostConstruct
    public void init(){
        if(twoFactorAuthenticationEnabled && (loginTokenManager == null)){
            throw new IllegalStateException("security.twoFactorAuthenticationEnabled = true, but loginTokenManager or loginTokenSender is null!");
        }
    }

    @RequestMapping(value = "/LoginToken.action", method = RequestMethod.POST)
    public ModelAndView handleLoginToken(HttpServletRequest request,
                                         @RequestParam String username,
                                         @RequestParam String domain,
                                         @RequestParam String logintoken,
                                         @RequestParam String redirect){
        Identity identity = DefaultIdentity.withDomainAndUserId(domain, username);
        Map<String, Object> model = new HashMap<>();
        if(loginTokenRestrictor.isBlocked(username)){
            model.put("blockedUser", true);
        } else {
            LoginTokenVerification tokenVerification = loginTokenManager.verifyLoginToken(identity, new DefaultLoginToken(logintoken));

            switch (tokenVerification){
                case VALID:
                    return handleValidToken(request, identity, redirect);
                case INVALID:
                    handleInvalidToken(model, identity);
                    break;
                case EXPIRED:
                    handleExpiredToken(model);
                    break;
            }
        }

        model.put("redirect", StringEscapeUtils.escapeHtml4(redirect));
        model.put("username", StringEscapeUtils.escapeHtml4(username));
        return new ModelAndView(twofactorAuthView, model);
    }

    private ModelAndView handleValidToken(HttpServletRequest request, Identity identity, String redirect) {
        LoginHelper.registerSuccessfulLogin(userLoginRestrictor, ipLoginRestrictor, request, identity.getUserId(), identity.getDomain());
        loginTokenRestrictor.registerLoginAttempt(identity.getUserId(), true);

        return new ModelAndView(new RedirectView(redirect));
    }

    private void handleInvalidToken(Map<String, Object> model, Identity identity) {
        loginTokenRestrictor.registerLoginAttempt(identity.getUserId(), false);
        boolean blocked = loginTokenRestrictor.isBlocked(identity.getUserId());
        if(blocked){
            log.info( "Too many attempts. User is blocked from login:" + identity.getUserId());
        }
        model.put("blockedUser", blocked);
        model.put("loginfailed", true);
    }

    private void handleExpiredToken(Map<String, Object> model) {
        model.put("expiredLoginToken", true);
    }

    @Required
    public void setTwofactorAuthView(String twofactorAuthView) {
        this.twofactorAuthView = twofactorAuthView;
    }

    @Required
    public void setUserLoginRestrictor(PauseLoginRestrictor userLoginRestrictor) {
        this.userLoginRestrictor = userLoginRestrictor;
    }

    @Required
    public void setIpLoginRestrictor(PauseLoginRestrictor ipLoginRestrictor) {
        this.ipLoginRestrictor = ipLoginRestrictor;
    }

    @Required
    public void setLoginTokenRestrictor(PauseLoginRestrictor loginTokenRestrictor) {
        this.loginTokenRestrictor = loginTokenRestrictor;
    }
}
