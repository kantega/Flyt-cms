/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.content.ajax;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.commons.client.util.RequestParameters;

/**
 *
 */
public class ApproveOrRejectAction implements Controller {
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request, "utf-8");

        boolean approve = param.getBoolean("approve", false);
        boolean reject = param.getBoolean("reject", false);

        String note = param.getString("note", 2000);

        int associationid = param.getInt("thisId");

        HttpSession session = request.getSession(true);

        ContentIdentifier cid = new ContentIdentifier();
        cid.setAssociationId(associationid);

        ContentManagementService aksessService = new ContentManagementService(request);

        Content content;
        if (approve || reject) {
            int status = approve ? ContentStatus.PUBLISHED : ContentStatus.REJECTED;
            content = aksessService.setContentStatus(cid, status, note);
            Content currentNavigateContent = (Content)session.getAttribute(AdminSessionAttributes.CURRENT_NAVIGATE_CONTENT);
            if (currentNavigateContent != null && currentNavigateContent.getId() == content.getId()) {
                session.setAttribute(AdminSessionAttributes.CURRENT_NAVIGATE_CONTENT, content);
            }
        }

        return null;
    }
}
