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
import no.kantega.publishing.admin.AdminSessionAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;
import java.util.HashMap;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

public class EditContentAction implements Controller {
    private static String SOURCE = "aksess.EditContentAction";

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContentManagementService aksessService = new ContentManagementService(request);

        ContentIdentifier cid = new ContentIdentifier(request);
        HttpSession session = request.getSession();
        Content content = (Content)session.getAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT);

        if (cid.getAssociationId() == -1 && content == null) {
            Connection c = null;
            try {
                c = dbConnectionFactory.getConnection();
                ResultSet rs = SQLHelper.getResultSet(c, "select * from contentversion where isActive = 1");
                if (!rs.next()) {
                    // Database is empty, create homepage
                    int templateId = SQLHelper.getInt(c, "select DisplayTemplateId from displaytemplates where urlfullview = '" + Aksess.getStartPage() + "'", "DisplayTemplateId");
                    if (templateId == -1) {
                        throw new MissingTemplateException(Aksess.getStartPage(), SOURCE);
                    }

                    return new ModelAndView(new RedirectView("SelectTemplate.action?parentId=0&templateId=dt;" + templateId));
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

        if ((content == null) || (cid.getAssociationId() != content.getAssociation().getId()) || (!content.isCheckedOut())) {
            // Content is not in session or not correct content
            content = aksessService.checkOutContent(cid);

            if (content.getStatus() == ContentStatus.DRAFT && content.getVersion() > 1) {
                // Tell user this is a draft
                infomessage = "editdraft";
            } else if (content.getStatus() == ContentStatus.WAITING_FOR_APPROVAL) {
                // Tell user this page is waiting for approval
                infomessage = "editwaiting";
            }
        }

        Map model = new HashMap();
        if (infomessage.length() > 0) {
            model.put("infomessage", infomessage);            
        }

        session.setAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT, content);

        return new ModelAndView(new RedirectView("SaveContent.action"), model);
    }
}