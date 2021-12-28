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

import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.LoginRestrictor;
import no.kantega.security.api.identity.DefaultIdentityResolver;
import no.kantega.security.api.identity.IdentityResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class LoginHelper {
    static void registerSuccessfulLogin(LoginRestrictor userLoginRestrictor,
                                               LoginRestrictor ipLoginRestrictor,
                                               HttpServletRequest request,
                                               String username,
                                               String domain) {

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
}
