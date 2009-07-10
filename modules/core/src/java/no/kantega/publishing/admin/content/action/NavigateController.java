/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package no.kantega.publishing.admin.content.action;

import no.kantega.publishing.admin.viewcontroller.AdminController;
import no.kantega.publishing.admin.viewcontroller.SimpleAdminController;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.commons.client.util.RequestParameters;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Kristian Lier Selnæs, Kantega AS
 * Date: 01.jul.2009
 * Time: 15:04:08
 */
public class NavigateController extends SimpleAdminController{


    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession(true);
         ContentManagementService aksessService = new ContentManagementService(request);
         RequestParameters param = new RequestParameters(request);

         String url = param.getString(AdminRequestParameters.URL);

         Content current = (Content)session.getAttribute(AdminSessionAttributes.CURRENT_CONTENT);

         if (url != null || request.getParameter(AdminRequestParameters.THIS_ID) != null || request.getParameter(AdminRequestParameters.CONTENT_ID) != null) {
             ContentIdentifier cid = null;
             if (url != null) {
                 cid = new ContentIdentifier(request, url);
             } else {
                 cid = new ContentIdentifier(request);
             }
             current = aksessService.getContent(cid);
         }

         if (current == null ) {
             // No current object, go to start page
             ContentIdentifier cid = new ContentIdentifier(request, "/");
             current = aksessService.getContent(cid);
         }

        //TODO: Use showcontet instead of current content
         // Updating the session with the current content object
         session.setAttribute(AdminSessionAttributes.CURRENT_CONTENT, current);
         session.setAttribute(AdminSessionAttributes.SHOW_CONTENT, current);


        return new ModelAndView(getView());
    }


}
