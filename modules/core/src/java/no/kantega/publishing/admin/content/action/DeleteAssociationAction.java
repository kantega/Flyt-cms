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
import no.kantega.publishing.api.content.ContentIdHelper;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteAssociationAction implements Controller {
    private static final Logger log = LoggerFactory.getLogger(DeleteAssociationAction.class);
    private String errorView;
    private String selectAssociationView;
    private String confirmDeleteSubPagesView;
    private String confirmDeleteView;

    @Autowired
    private ContentIdHelper contentIdHelper;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse httpServletResponse) throws Exception {
        ContentManagementService aksessService = new ContentManagementService(request);
        SecuritySession securitySession = aksessService.getSecuritySession();

        RequestParameters param = new RequestParameters(request, "utf-8");
        HttpSession session = request.getSession();

        Map<String, Object> model = new HashMap<>();

        if (!request.getMethod().equalsIgnoreCase("POST")) {
            // Not post, user should confirm delete of page

            // Get association
            String url = request.getParameter("url");
            ContentIdentifier cid = contentIdHelper.fromRequestAndUrl(request, url);

            // Get content (page) that association points to
            Content content = aksessService.getContent(cid);

            if(content == null){
                log.error( "Tried to delete non-existing content");
                model.put("error", "aksess.confirmdelete.doesnotexist");
                return new ModelAndView(errorView, model);
            }

            String contentTitle = "";
            if (content.getTitle() != null) {
                contentTitle = content.getTitle();
            }
            if (contentTitle.length() > 30) contentTitle = contentTitle.substring(0, 27) + "...";
            model.put("contentTitle", contentTitle);


            if (!securitySession.isAuthorized(content, Privilege.APPROVE_CONTENT)) {
                model.put("error", "aksess.confirmdelete.notauthorized");
                return new ModelAndView(errorView, model);
            }

            if (content.isLocked()) {
                model.put("error", "aksess.confirmdelete.locked");
                return new ModelAndView(errorView, model);
            }

            boolean isCrossPublished = false;
            List<Association> associations = content.getAssociations();
            if (associations != null) {
                if (associations.size() > 1) {
                    isCrossPublished = true;
                }
                model.put("associationId", cid.getAssociationId());
            }

            model.put("content", content);
            model.put("isCrossPublished", isCrossPublished);

            return new ModelAndView(selectAssociationView, model);

        } else {
            // User has confirmed deletion
            int[] ids = param.getInts("id");
            boolean confirmMultipleDelete = param.getBoolean("confirmMultipleDelete", false);

            if (ids != null && ids.length > 0) {
                List<Content> toBeDeleted = aksessService.deleteAssociationsById(ids, confirmMultipleDelete);
                if (toBeDeleted != null && toBeDeleted.size() > 1 && !confirmMultipleDelete) {
                    // User must confirm deletion of subpages
                    model.put("associationIds", ids);
                    model.put("toBeDeleted", toBeDeleted);
                    return new ModelAndView(confirmDeleteSubPagesView, model);
                } else {
                    Content current = (Content)session.getAttribute(AdminSessionAttributes.CURRENT_NAVIGATE_CONTENT);
                    if (current != null) {
                        ContentIdentifier cid = new ContentIdentifier();
                        cid.setAssociationId(current.getAssociation().getAssociationId());
                        if (aksessService.getContent(cid, false) == null) {
                            // The page the user is watching is deleted, show parent
                            int parentId = current.getAssociation().getParentAssociationId();
                            if (parentId > 0) {
                                ContentIdentifier parentCid = new ContentIdentifier();
                                parentCid.setAssociationId(parentId);
                                current = aksessService.getContent(parentCid, false);
                                session.setAttribute(AdminSessionAttributes.CURRENT_NAVIGATE_CONTENT, current);
                                model.put("currentPage", current);
                            }
                        } else {
                            model.put("updateNavigator", Boolean.TRUE);
                        }
                    }
                }
            }
            model.put("message", "aksess.confirmdelete.finished");
            return new ModelAndView(confirmDeleteView, model);
        }
    }

    public void setErrorView(String errorView) {
        this.errorView = errorView;
    }

    public void setSelectAssociationView(String selectAssociationView) {
        this.selectAssociationView = selectAssociationView;
    }

    public void setConfirmDeleteSubPagesView(String confirmDeleteSubPagesView) {
        this.confirmDeleteSubPagesView = confirmDeleteSubPagesView;
    }

    public void setConfirmDeleteView(String confirmDeleteView) {
        this.confirmDeleteView = confirmDeleteView;
    }
}
