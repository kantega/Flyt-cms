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
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.service.lock.LockManager;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.service.ContentManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 */
public class SimpleEditCancelAction implements Controller {
    @Autowired private LockManager lockManager;
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        RequestParameters param = new RequestParameters(request);

        ContentManagementService cms = new ContentManagementService(request);

        Content content = (Content)session.getAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT);
        if (content == null) {
            return new ModelAndView(new RedirectView(Aksess.getContextPath()));
        }

        lockManager.releaseLock(content.getId());

        ContentIdentifier cid = new ContentIdentifier();
        if (content.isNew() || !content.hasDisplayTemplate()) {
            // For new content and content without display template
            Association a = content.getAssociation();
            cid.setAssociationId(a.getParentAssociationId());
            cid.setLanguage(content.getLanguage());
        } else {
            // Get updated content
            cid.setAssociationId(content.getAssociation().getId());
        }


        String url;
        String redirectUrl = param.getString("redirectUrl");
        if(redirectUrl != null && redirectUrl.length() > 0) {
            url = redirectUrl;
        } else {
            content = cms.getContent(cid);
            if (content != null) {
                url = content.getUrl();
            } else {
                url = Aksess.getContextPath();
            }
        }

        session.removeAttribute("adminMode");

        return new ModelAndView(new RedirectView(url));
    }
}
