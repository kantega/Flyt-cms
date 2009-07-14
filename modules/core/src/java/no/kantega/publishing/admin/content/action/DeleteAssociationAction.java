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

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.exception.ExceptionHandler;
import no.kantega.publishing.admin.AdminSessionAttributes;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

public class DeleteAssociationAction extends HttpServlet {
    private static String SOURCE = "aksess.DeleteAssociationAction";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        RequestParameters param = new RequestParameters(request, "utf-8");
        HttpSession session = request.getSession();
        try {
            ContentManagementService aksessService = new ContentManagementService(request);

            int[] ids = param.getInts("associationId");
            boolean confirmMultipleDelete = param.getBoolean("confirmMultipleDelete", false);

            String view = "updatetree.jsp";
            List toBeDeleted = null;
            if (ids.length > 0) {
                toBeDeleted = aksessService.deleteAssociationsById(ids, confirmMultipleDelete);
                if (toBeDeleted != null && toBeDeleted.size() > 1 && !confirmMultipleDelete) {
                    request.setAttribute("toBeDeleted", toBeDeleted);
                    view = "confirmdelete_multiple.jsp";
                } else {
                    Content current = (Content)session.getAttribute(AdminSessionAttributes.CURRENT_NAVIGATE_CONTENT);
                    if (current != null) {
                        ContentIdentifier cid = new ContentIdentifier();
                        cid.setAssociationId(current.getAssociation().getAssociationId());
                        if (aksessService.getContent(cid, false) == null) {
                            // Objektet brukeren kikker på er slettet, hent parent
                            int parentId = current.getAssociation().getParentAssociationId();
                            if (parentId > 0) {
                                ContentIdentifier parentCid = new ContentIdentifier();
                                parentCid.setAssociationId(parentId);
                                current = aksessService.getContent(parentCid, false);
                                session.setAttribute("currentContent", current);
                            }
                        }
                        request.setAttribute("statusmessage", "deletecontent");
                    }
                }
            }
            request.getRequestDispatcher(view).forward(request, response);
        } catch (Exception e) {
            Log.error(SOURCE, e, null, null);

            ExceptionHandler handler = new ExceptionHandler();
            handler.setThrowable(e, SOURCE);
            request.getSession(true).setAttribute("handler", handler);
            request.getRequestDispatcher(Aksess.ERROR_URL).forward(request, response);
        }
    }
}
