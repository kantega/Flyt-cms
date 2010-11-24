/*
 * Copyright 2010 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.formadmin.presentation.controller;

import no.kantega.publishing.security.SecuritySession;
import no.kantega.security.api.identity.Identity;

import javax.servlet.http.HttpServletRequest;

public abstract class FormAdminBaseController {

    /**
     * Extracts the user's Identity from the servlet request.
     *
     * @param request Current request.
     * @return Current user's Identity or null if not found.
     */
    protected Identity getIdentityFromRequest(HttpServletRequest request) {
        SecuritySession securitySession = getSecuritySession(request);
        return securitySession.getIdentity();
    }

    /**
     * Abstraction in order to enable mocking of the SecuritySession.
     *
     * @param request Current request
     * @return Current SecuritySession
     */
    protected SecuritySession getSecuritySession(HttpServletRequest request) {
        return SecuritySession.getInstance(request);
    }
}
