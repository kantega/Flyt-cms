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
import no.kantega.publishing.common.exception.ExceptionHandler;
import no.kantega.commons.exception.InvalidParameterException;
import no.kantega.publishing.admin.content.util.EditContentHelper;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentCreateParameters;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.security.SecuritySession;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.util.Date;


public class SelectTemplateAction extends HttpServlet {
    private static String SOURCE = "aksess.SelectTemplateAction";
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestParameters param = new RequestParameters(request, "utf-8");
        String templateId = param.getString("templateId");
        int mainParentId = param.getInt("mainParentId");
        int[] parentIds = param.getInts("parentIds");
        int category = param.getInt("associationCategory");

        try {

            if (templateId == null || templateId.length() == 0 || mainParentId == -1) {
                throw new InvalidParameterException("templateId == -1 || mainParentId == -1", SOURCE);
            }

            int displayTemplateId = -1;
            int contentTemplateId = -1;

            // Links and files dont have displaytemplate, only contenttemplate
            String type = templateId.substring(0, templateId.indexOf(";"));
            templateId = templateId.substring(templateId.indexOf(";") + 1, templateId.length());
            if (type.equalsIgnoreCase("ct")) {
                contentTemplateId = Integer.parseInt(templateId);
            } else {
                displayTemplateId = Integer.parseInt(templateId);
            }

            ContentCreateParameters createParam = new ContentCreateParameters();
            createParam.setCategoryId(category);
            createParam.setDisplayTemplateId(displayTemplateId);
            createParam.setContentTemplateId(contentTemplateId);
            createParam.setMainParentId(mainParentId);
            createParam.setParentIds(parentIds);

            ContentManagementService cms = new ContentManagementService(request);
            Content content = cms.createNewContent(createParam);

            HttpSession session = request.getSession();
            session.setAttribute("currentContent", content);

            response.sendRedirect("content.jsp?activetab=editcontent&dummy=" + new Date().getTime());

        } catch (Exception e) {
            ExceptionHandler handler = new ExceptionHandler();
            handler.setThrowable(e, SOURCE);
            request.getSession(true).setAttribute("handler", handler);
            request.getRequestDispatcher(Aksess.ERROR_URL).forward(request, response);
        }
    }
}