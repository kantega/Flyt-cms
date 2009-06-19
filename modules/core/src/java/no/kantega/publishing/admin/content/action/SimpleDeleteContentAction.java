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
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.service.ContentManagementService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * User: Anders Skar, Kantega AS
 * Date: Jul 2, 2008
 * Time: 12:52:25 PM
 */
public class SimpleDeleteContentAction implements Controller {
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);
        int associationId = param.getInt("associationId");

        ContentManagementService cms = new ContentManagementService(request);

        int parentId = -1;
        ContentIdentifier cid = new ContentIdentifier();
        cid.setAssociationId(associationId);
        Content current = cms.getContent(cid, false);
        if (current != null) {
            parentId = current.getAssociation().getParentAssociationId();
        }

        cms.deleteAssociationsById(new int[] {associationId}, true);

        String url;
        String redirectUrl = param.getString("redirectUrl");
        if(redirectUrl != null && redirectUrl.length() > 0) {
            url = redirectUrl;
        } else {
            Content parent = null;
            if (parentId != -1) {
                ContentIdentifier parentCid = new ContentIdentifier();
                parentCid.setAssociationId(parentId);
                parent = cms.getContent(parentCid);
            }
            if (parent != null) {
                url = parent.getUrl();
            } else {
                url = Aksess.getContextPath();
            }
        }

        return new ModelAndView(new RedirectView(url));
    }
}
