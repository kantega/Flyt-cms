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
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.service.ContentManagementService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteAttachmentAction implements Controller {
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request, "utf-8");
        ContentManagementService aksessService = new ContentManagementService(request);

        int attachmentId = param.getInt("attachmentId");

        aksessService.deleteAttachment(attachmentId);

        HttpSession session = request.getSession(true);
        Content content = (Content)session.getAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT);
        if (content != null) {
            // Delete reference from current object in session
            List attachments = content.getAttachments();
            for (int i = 0; i < attachments.size(); i++) {
                Attachment attachment = (Attachment) attachments.get(i);
                if (attachment.getId() == attachmentId) {
                    attachments.remove(i);
                    break;
                }
            }
        }

        Map<String, Object> model = new HashMap<>();
        model.put("refresh", new Date().getTime());

        return new ModelAndView(new RedirectView("SaveAttachments.action"), model);
    }
}


