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

package no.kantega.publishing.admin.mypage.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.service.ContentManagementService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RestoreDeletedItemAction  implements Controller {
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);
        int id = param.getInt("id");

        ContentManagementService service = new ContentManagementService(request);

        int associationId = -1;
        if (id != -1) {
            associationId = service.restoreDeletedItem(id);
        }

        if (associationId != -1) {
            ContentIdentifier cid =  ContentIdentifier.fromAssociationId(associationId);
            Content content = service.getContent(cid);            
            request.getSession().setAttribute(AdminSessionAttributes.CURRENT_NAVIGATE_CONTENT, content);
            return new ModelAndView(new RedirectView("../publish/Navigate.action"));
        } else {
            return new ModelAndView(new RedirectView("ViewMyPage.action"));
        }
    }
}
