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
 * limitations under the License.
 */

package no.kantega.publishing.admin.content.ajax;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.admin.model.Clipboard;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.service.lock.ContentLock;
import no.kantega.publishing.common.service.lock.LockManager;
import no.kantega.publishing.security.data.enums.Privilege;
import no.kantega.publishing.security.SecuritySession;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.HashMap;

/**
 * Controller which enables/disables buttons in the tools menu.
 * Receives the url of the currently selected content.
 * Returns a view of the tools menu.
 *
 * Author: Kristian Lier Selnæs, Kantega AS
 * Date: 07.jul.2009
 * Time: 08:59:27
 */
public class ToolsMenuAction implements Controller {

    private String viewName;

    /**
     * Responsible for rendering the content tools menu.
     *
     * Uses the url of the 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();

        SecuritySession securitySession = SecuritySession.getInstance(request);
        ContentManagementService cms = new ContentManagementService(request);
        RequestParameters params = new RequestParameters(request);
        HttpSession session = request.getSession(true);

        String url = params.getString(AdminRequestParameters.URL);

        //Extracting currently selected content from it's url
        Content currentContent = null;
        int currentId = -1;
        if (!"".equals(url)) {
            ContentIdentifier cid = new ContentIdentifier(request, url);
            currentContent = cms.getContent(cid);
            if (currentContent != null) {
                currentId = currentContent.getAssociation().getId();

                boolean canUpdate = false;
                boolean canDelete = false;
                boolean canCreateSubPage = true;

                if (currentContent.getType() != ContentType.PAGE) {
                    canCreateSubPage = false;
                }
                canUpdate = securitySession.isAuthorized(currentContent, Privilege.UPDATE_CONTENT);
                if (currentContent.getVersion() > 1 || currentContent.getStatus() == ContentStatus.PUBLISHED) {
                    canDelete = securitySession.isAuthorized(currentContent, Privilege.APPROVE_CONTENT);
                } else {
                    // Let the user delete the page if this is the first version and it's not yet published.
                    canDelete = canUpdate;
                }

                if (currentContent.getId() == -1) {
                    // Cannot create a subpage for a page not yet created.
                    canCreateSubPage = false;
                }
                model.put(AdminRequestParameters.PERMISSONS_CAN_UPDATE, canUpdate);
                model.put(AdminRequestParameters.PERMISSONS_CAN_DELETE, canDelete);
                model.put(AdminRequestParameters.PERMISSONS_CAN_CREATE_SUBPAGE, canCreateSubPage);

            }
            String lockedBy  = null;
            ContentLock lock = LockManager.peekAtLock(currentId);
            if(lock != null && !lock.getOwner().equals(securitySession.getUser().getId())) {
                lockedBy = lock.getOwner();
                model.put(AdminRequestParameters.PERMISSONS_LOCKED_BY, lockedBy);
            }

            Clipboard clipboard = (Clipboard) session.getAttribute(AdminSessionAttributes.CLIPBOARD_CONTENT) ;
            if (clipboard != null && !clipboard.isEmpty()) {
                model.put(AdminRequestParameters.CLIPBOARD, clipboard);
            }

        }


        return new ModelAndView(viewName, model);
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
}
