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
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.common.util.database.SQLHelper;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.exception.MissingTemplateException;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.admin.content.util.EditContentHelper;
import no.kantega.publishing.common.exception.ExceptionHandler;
import no.kantega.commons.exception.InvalidParameterException;
import no.kantega.commons.client.util.RequestParameters;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;

public class EditContentAction  extends HttpServlet {
    private static String SOURCE = "aksess.EditContentAction";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestParameters param = new RequestParameters(request);

        String action = param.getString("action");
        try {
            if (action == null) {
                throw new InvalidParameterException("action parameter missing", SOURCE);
            }

            ContentManagementService aksessService = new ContentManagementService(request);

            ContentIdentifier cid = new ContentIdentifier(request);
            HttpSession session = request.getSession();
            Content content = (Content)session.getAttribute("currentContent");

            if (cid.getAssociationId() == -1 && content == null) {
                // Hvis basen er tom, kan hjemmesida opprettes
                Connection c = null;
                try {
                    c = dbConnectionFactory.getConnection();
                    ResultSet rs = SQLHelper.getResultSet(c, "select * from contentversion where isActive = 1");
                    if (!rs.next()) {
                        // Opprett hjemmesida
                        int templateId = SQLHelper.getInt(c, "select DisplayTemplateId from displaytemplates where urlfullview = '" + Aksess.getStartPage() + "'", "DisplayTemplateId");
                        if (templateId == -1) {
                            throw new MissingTemplateException(Aksess.getStartPage(), SOURCE);
                        }
                        response.sendRedirect("SelectTemplate.action?parentId=0&templateId=dt;" + templateId);
                        return;
                    } else {
                        throw new ContentNotFoundException("-1", SOURCE);
                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }

            String infomessage = "";

            // Vi har funnet innholdet som det skal gjøres noe med...
            if ((content == null) || (cid.getAssociationId() != content.getAssociation().getId()) || (!content.isCheckedOut())) {
                // Innholdet ligger ikke i session eller er ikke riktig innhold
                content = aksessService.checkOutContent(cid);

                // Hvis siste versjon er en kladd eller ikke godkjent side, gi brukeren beskjed om dette!
                if (content.getStatus() == ContentStatus.DRAFT && content.getVersion() > 1) {
                    infomessage = "&infomessage=editdraft";
                } else if (content.getStatus() == ContentStatus.WAITING) {
                    infomessage = "&infomessage=editwaiting";
                }
            }
            session.setAttribute("currentContent", content);

            if (action.indexOf("edit") != -1) {
                response.sendRedirect("content.jsp?activetab=" + action + infomessage + "&dummy=" + new Date().getTime());
            } else {
                response.sendRedirect(action + ".jsp?dummy=" + new Date().getTime());
            }
        } catch (Exception e) {
            ExceptionHandler handler = new ExceptionHandler();
            handler.setThrowable(e, SOURCE);
            request.getSession(true).setAttribute("handler", handler);
            request.getRequestDispatcher(Aksess.ERROR_URL).forward(request, response);
        }

    }
}