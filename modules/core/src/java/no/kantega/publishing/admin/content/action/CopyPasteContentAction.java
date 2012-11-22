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
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.admin.model.Clipboard;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.AssociationCategory;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.enums.AssociationType;
import no.kantega.publishing.common.service.ContentManagementService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CopyPasteContentAction implements Controller {
    private String confirmCopyPasteView;
    private String duplicateAliasesView;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        RequestParameters param = new RequestParameters(request, "utf-8");

        ContentManagementService aksessService = new ContentManagementService(request);

        boolean isCopy = param.getBoolean("isCopy");
        boolean isTextCopy = param.getBoolean("isTextCopy");
        boolean pasteShortCut = param.getBoolean("pasteShortCut");

        int uniqueId = param.getInt("uniqueId");
        int newParentId = param.getInt("newParentId");
        int categoryId = param.getInt("associationCategory");

        AssociationCategory category = new AssociationCategory(categoryId);

        Association parent = aksessService.getAssociationById(newParentId);
        Association source = aksessService.getAssociationById(uniqueId);

        Map model = new HashMap();

        if (isCopy) {
            copyContent(aksessService, isTextCopy, pasteShortCut, uniqueId, newParentId, category, parent, source);
            model.put("message", "aksess.copypaste.copy.ok");
        } else {
            moveContent(aksessService, uniqueId, newParentId, category, parent, source);
            model.put("message", "aksess.copypaste.move.ok");
        }

        Clipboard clipboard = (Clipboard)request.getSession(true).getAttribute(AdminSessionAttributes.CLIPBOARD_CONTENT);
        if (clipboard != null) {
            clipboard.empty();
        }

        // Check for duplicate pages
        List aliases = aksessService.findDuplicateAliases(parent);
        if (aliases.size() == 0) {
            model.put("updateNavigator", Boolean.TRUE);
            return new ModelAndView(confirmCopyPasteView, model);
        } else {
            model.put("aliases", aliases);
            return new ModelAndView(duplicateAliasesView, model);
        }
    }

    private void moveContent(ContentManagementService aksessService, int uniqueId, int newParentId, AssociationCategory category, Association parent, Association source) {
        Association association = new Association();
        association.setParentAssociationId(newParentId);
        association.setCategory(category);
        association.setSiteId(parent.getSiteId());
        association.setContentId(source.getContentId());
        association.setId(uniqueId);
        association.setAssociationId(uniqueId);

        aksessService.modifyAssociation(association);
    }

    private void copyContent(ContentManagementService aksessService, boolean textCopy, boolean pasteShortCut, int uniqueId, int newParentId, AssociationCategory category, Association parent, Association source) throws NotAuthorizedException {
        if (textCopy) {
            // Copy text from one page to a new page
            ContentIdentifier cid =  ContentIdentifier.fromAssociationId(source.getAssociationId());
            Content content = aksessService.getContent(cid);

            aksessService.copyContent(content, parent, category);

        } else if (pasteShortCut) {
            // Create a shortcut
            Association association = new Association();
            association.setParentAssociationId(newParentId);
            association.setCategory(category);
            association.setSiteId(parent.getSiteId());
            association.setContentId(source.getContentId());
            association.setAssociationId(uniqueId);
            association.setAssociationtype(AssociationType.SHORTCUT);
            aksessService.addAssociation(association);
        } else {
            // Copy structure (cross publish)
            aksessService.copyAssociations(source, parent, category, true);
        }
    }

    public void setConfirmCopyPasteView(String confirmCopyPasteView) {
        this.confirmCopyPasteView = confirmCopyPasteView;
    }

    public void setDuplicateAliasesView(String duplicateAliasesView) {
        this.duplicateAliasesView = duplicateAliasesView;
    }
}
