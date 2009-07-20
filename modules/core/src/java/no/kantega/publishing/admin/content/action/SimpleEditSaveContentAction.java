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

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import no.kantega.publishing.admin.content.util.SaveContentHelper;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.util.RequestHelper;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.Aksess;
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.client.util.RequestParameters;

public class SimpleEditSaveContentAction implements Controller {
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession();
        RequestParameters param = new RequestParameters(request);

        Content content = (Content)session.getAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT);

        if (content != null && request.getMethod().equalsIgnoreCase("POST")) {
            // Lagre opplysninger
            SaveContentHelper helper = new SaveContentHelper(request, content, AttributeDataType.CONTENT_DATA);

            ValidationErrors errors = new ValidationErrors();
            errors = helper.getHttpParameters(errors);

            if (errors.getLength() == 0) {
                // Ingen feil, lagre
                session.removeAttribute("errors");
                if (errors.getLength() == 0) {
                    ContentManagementService cms = new ContentManagementService(request);
                    content = cms.checkInContent(content, ContentStatus.PUBLISHED);
                }
                session.removeAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT);

                String url;
                String redirectUrl = param.getString("redirectUrl");
                if(redirectUrl != null && redirectUrl.length() > 0) {
                    url = redirectUrl;
                } else {
                    url = content.getUrl();
                }

                session.removeAttribute("adminMode");

                return new ModelAndView(new RedirectView(url));
            } else {
                // Feil på siden, send bruker tilbake for å rette opp feil
                request.setAttribute("errors", errors);

                RequestHelper.setRequestAttributes(request, content);

                request.setAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT, content);
                session.setAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT, content);

                return new ModelAndView("/admin/publish/simpleeditcontent.jsp", null);

            }
        }

        session.removeAttribute("adminMode");

        return new ModelAndView(new RedirectView(Aksess.getContextPath()));
    }
}
