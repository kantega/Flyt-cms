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

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.service.lock.LockManager;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.security.SecuritySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LogoutAction extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(LogoutAction.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("adminMode");
            try {
                ContentManagementService cms = new ContentManagementService(request);
                SecuritySession securitySession = cms.getSecuritySession();
                if (securitySession != null && securitySession.getUser() != null) {
                    LockManager lockManager = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext()).getBean(LockManager.class);
                    lockManager.releaseLocksForOwner(securitySession.getUser().getId());
                    securitySession.logout(request, response);
                }
            } catch (SystemException e) {
                log.error("Error logging out");
            }
            session.invalidate();
        }
    }
}



