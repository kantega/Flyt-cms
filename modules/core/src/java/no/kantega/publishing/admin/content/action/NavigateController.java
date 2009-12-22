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

package no.kantega.publishing.admin.content.action;

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
import java.util.Map;
import java.util.HashMap;

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

        Content current = (Content)session.getAttribute(AdminSessionAttributes.CURRENT_NAVIGATE_CONTENT);

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

        session.setAttribute(AdminSessionAttributes.CURRENT_NAVIGATE_CONTENT, current);

        Content editedContent = (Content)session.getAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT);


        String currentUrl = current.getUrl();
        Map<String, Object> model = new HashMap<String, Object>();
        if (editedContent != null) {
            currentUrl = "ViewContentPreviewFrame.action?thisId=";
            if (editedContent.getId() == -1) {
                // New page
                currentUrl += editedContent.getAssociation().getParentAssociationId();
            } else {
                currentUrl += editedContent.getAssociation().getId();
            }
        }
        model.put("currentUrl", currentUrl);        

        return new ModelAndView(getView(), model);
    }


}
