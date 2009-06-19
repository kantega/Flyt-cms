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
import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.cache.TemplateConfigurationCache;
import no.kantega.publishing.common.exception.ChildContentNotAllowedException;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import no.kantega.publishing.event.ContentListenerUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * User: Anders Skar, Kantega AS
 * Date: Oct 1, 2007
 * Time: 11:06:08 AM
 */
public class ConfirmCopyPasteContentAction implements Controller {
    private TemplateConfigurationCache templateConfigurationCache;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Configuration config = Aksess.getConfiguration();

        RequestParameters param = new RequestParameters(request, "utf-8");

        Map model = new HashMap();

        int newParentId = param.getInt("newParentId");
        boolean isCopy  = param.getBoolean("isCopy");
        boolean pasteShortCut  = param.getBoolean("pasteShortCut");

        ContentManagementService cms = new ContentManagementService(request);
        SecuritySession securitySession = SecuritySession.getInstance(request);

        int uniqueId = param.getInt("clipboard");

        Content selectedContent = getContent(request, uniqueId);
        String selectedContentTitle = selectedContent.getTitle();
        if (selectedContentTitle.length() > 30) selectedContentTitle = selectedContentTitle.substring(0, 27) + "...";

        Content newParent = getContent(request, newParentId);
        String parentTitle = newParent.getTitle();
        if (parentTitle.length() > 30) parentTitle = parentTitle.substring(0, 27) + "...";

        ContentTemplate template = cms.getContentTemplate(newParent.getContentTemplateId());
        List allowedAssociations = getAssociationCategories(template);

        boolean isAuthorized = false;
        if (securitySession.isAuthorized(newParent, Privilege.APPROVE_CONTENT)) {
            if (isCopy || securitySession.isAuthorized(selectedContent, Privilege.APPROVE_CONTENT)) {
                isAuthorized = true;
            }
        }

        boolean forbidMoveCrossSite = false;
        if (!isCopy && selectedContent.getAssociation().getSiteId() != newParent.getAssociation().getSiteId()) {
            // Vi gjør en sjekk på om malen finnes i det nye nettstedet for å være grei med brukeren :)
            DisplayTemplate displayTemplate = cms.getDisplayTemplate(selectedContent.getDisplayTemplateId());
            if (displayTemplate.getSites().size() > 0) {
                forbidMoveCrossSite = true;
                for (Site s : displayTemplate.getSites()) {
                    if (s.getId() == newParent.getAssociation().getSiteId()) {
                        forbidMoveCrossSite = false;
                    }
                }
            }
        }

        String error = null;

        if (!isAuthorized) {
            // User is not authorized
            error = "aksess.copypaste.notauthorized";
        } else if (forbidMoveCrossSite) {
            // Template does not exists in site
            error = "aksess.copypaste.crosssite";
        } else if ((!isCopy) && (newParent.getAssociation().getPath().indexOf("/" + uniqueId + "/") != -1)) {
            // Will lead to recursion
            error = "aksess.copypaste.recursion";
        } else if (allowedAssociations == null || allowedAssociations.size() == 0) {
            // Not allowed to publish here
            error = "aksess.copypaste.notallowed";
        }

        if (error != null) {
            model.put("error", error);

            return new ModelAndView("/admin/popups/error.jsp", model);
        } else {
            
            model.put("isCopy", Boolean.valueOf(isCopy));
            model.put("pasteShortCut", Boolean.valueOf(pasteShortCut));
            model.put("uniqueId", new Integer(uniqueId));
            model.put("newParentId", new Integer(newParentId));
            model.put("selectedContent", selectedContent);
            model.put("selectedContentTitle", selectedContentTitle);
            model.put("parentTitle", parentTitle);
            model.put("allowedAssociations", allowedAssociations);
            model.put("allowDuplicate", Boolean.valueOf(config.getBoolean("content.duplicate.enabled", true)));
            model.put("allowCrossPublish", Boolean.valueOf(true));

            // Run plugins
            ContentListenerUtil.getContentNotifier().beforeConfirmCopyPasteContent(selectedContent, model);

            return new ModelAndView("/admin/publish/copypaste.jsp", model);
        }
    }

    private Content getContent(HttpServletRequest request, int uniqueId) throws NotAuthorizedException {
        ContentManagementService cms = new ContentManagementService(request);

        ContentIdentifier cid = new ContentIdentifier();
        cid.setAssociationId(uniqueId);
        Content selectedContent = cms.getContent(cid);
        return selectedContent;
    }

    private List getAssociationCategories(ContentTemplate template) {
        List<AssociationCategory> tmpAllowedAssociations = template.getAssociationCategories();
        if (tmpAllowedAssociations == null || tmpAllowedAssociations.size() == 0) {
            return null;
        } else if (template.getContentType() != ContentType.PAGE) {
            return null;
        }

        // Template only holds id of AssociationCategory, get complete AssociationCategory from cache
        List<AssociationCategory> allAssociations = templateConfigurationCache.getTemplateConfiguration().getAssociationCategories();
        List<AssociationCategory> allowedAssociations = new ArrayList<AssociationCategory>();
        for (AssociationCategory allowedAssociation : tmpAllowedAssociations) {
            for (AssociationCategory allAssociation : allAssociations) {
                if (allAssociation.getId() == allowedAssociation.getId()) {
                    allowedAssociations.add(allAssociation);
                }
            }
        }
        return allowedAssociations;

    }

    public void setTemplateConfigurationCache(TemplateConfigurationCache templateConfigurationCache) {
        this.templateConfigurationCache = templateConfigurationCache;
    }    
}
