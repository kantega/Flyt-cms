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

package no.kantega.publishing.admin.content.action;

import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.service.lock.LockManager;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.exception.ExceptionHandler;
import no.kantega.commons.log.Log;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Date;

public class CancelEditAction  extends HttpServlet {
    private static String SOURCE = "aksess.CancelEditAction";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        HttpSession session = request.getSession();

        try {
            ContentManagementService aksessService = new ContentManagementService(request);

            Content content = (Content)session.getAttribute("currentContent");
            if (content != null) {

                LockManager.releaseLock(content.getId());
                ContentIdentifier cid = new ContentIdentifier();
                if (content.getId() == -1) {
                    // Nytt innhold, vis parent
                    Association a = content.getAssociation();
                    cid.setAssociationId(a.getParentAssociationId());
                    cid.setLanguage(content.getLanguage());
                } else {
                    // Hent siste versjon
                    cid.setAssociationId(content.getAssociation().getId());
                }

                content = aksessService.getContent(cid);

                session.setAttribute("currentContent", content);
            }
            response.sendRedirect("content.jsp?activetab=previewcontent&dummy=" + new Date().getTime());
        } catch (Exception e) {
            Log.error(SOURCE, e, null, null);

            ExceptionHandler handler = new ExceptionHandler();
            handler.setThrowable(e, SOURCE);
            request.getSession(true).setAttribute("handler", handler);
            request.getRequestDispatcher(Aksess.ERROR_URL).forward(request, response);
        }
    }
}
