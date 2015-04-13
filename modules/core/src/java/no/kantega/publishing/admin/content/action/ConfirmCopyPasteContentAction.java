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
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.admin.model.Clipboard;
import no.kantega.publishing.admin.model.ClipboardStatus;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.cache.TemplateConfigurationCache;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.content.api.ContentIdHelper;
import no.kantega.publishing.event.ContentEvent;
import no.kantega.publishing.event.ContentEventListener;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Action Controller for confirming a copy paste operation
 */
public class ConfirmCopyPasteContentAction implements Controller {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private TemplateConfigurationCache templateConfigurationCache;
    private String errorView;
    private String view;
    private String illegalParentTemplateView;


    @Autowired
    private ContentIdHelper contentIdHelper;

    @Resource(name = "contentListenerNotifier")
    private ContentEventListener contentEventListener;


    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Configuration config = Aksess.getConfiguration();

        RequestParameters param = new RequestParameters(request, "utf-8");

        Map<String, Object> model = new HashMap<>();

        String url = request.getParameter("newParentUrl");
        ContentIdentifier newParentCid = contentIdHelper.fromRequestAndUrl(request, url);



        boolean forbidMoveCrossSite = false;

        Clipboard clipboard = (Clipboard)request.getSession(true).getAttribute(AdminSessionAttributes.CLIPBOARD_CONTENT);
        if (clipboard == null || clipboard.getItems() == null || clipboard.getItems().size() == 0) {
            model.put("error", "aksess.copypaste.emptyclipboard");
            return new ModelAndView(errorView, model);
        }

        SecuritySession securitySession = SecuritySession.getInstance(request);
        ContentManagementService cms = new ContentManagementService(securitySession, request);


        Content selectedContent = (Content)clipboard.getItems().get(0);
        
        int selectedPageAssociationId = selectedContent.getAssociation().getId();

        //check if new Parent page allows copied page template
        if(!copyIsAllowed(selectedPageAssociationId, cms, newParentCid.getAssociationId())){
            clipboard.empty();
            return new ModelAndView(illegalParentTemplateView, Collections.<String,Object>emptyMap());
        }

        String selectedContentTitle = selectedContent.getTitle();
        if (selectedContentTitle.length() > 30) selectedContentTitle = selectedContentTitle.substring(0, 27) + "...";

        Content newParent = cms.getContent(newParentCid);

        String parentTitle = newParent.getTitle();
        if (parentTitle.length() > 30) parentTitle = parentTitle.substring(0, 27) + "...";

        boolean isCopy = clipboard.getStatus() == ClipboardStatus.COPIED;
        boolean isAuthorized = false;
        // User must be authorized with APPROVE_CONTENT at new location
        if (securitySession.isAuthorized(newParent, Privilege.APPROVE_CONTENT)) {
            // Must be copy or user must be authorized with APPROVE_CONTENT for content
            if (isCopy || securitySession.isAuthorized(selectedContent, Privilege.APPROVE_CONTENT)) {
                isAuthorized = true;
            }
        }
        if (!isAuthorized) {
            model.put("error", "aksess.copypaste.notauthorized");
            return new ModelAndView(errorView, model);
        }

        ContentTemplate template = cms.getContentTemplate(newParent.getContentTemplateId());
        List<AssociationCategory> allowedAssociations = getAssociationCategories(template);

        if (!isCopy && selectedContent.getAssociation().getSiteId() != newParent.getAssociation().getSiteId()) {
            // Check if template is allowed for pasted page in new site
            DisplayTemplate displayTemplate = cms.getDisplayTemplate(selectedContent.getDisplayTemplateId());
            if (displayTemplate != null && displayTemplate.getSites().size() > 0) {
                forbidMoveCrossSite = true;
                for (Site s : displayTemplate.getSites()) {
                    if (s.getId() == newParent.getAssociation().getSiteId()) {
                        forbidMoveCrossSite = false;
                    }
                }
            } else {
                log.error("Displaytemplate with id {} was null for content with id ", selectedContent.getDisplayTemplateId(), selectedContent.getId());
            }
        }

        String error = null;
        if (forbidMoveCrossSite) {
            // Template does not exists in site
            error = "aksess.copypaste.crosssite";
        } else if (newParent.getAssociation().getPath().contains("/" + selectedPageAssociationId + "/")) {
            // Will lead to recursion
            error = "aksess.copypaste.recursion";
        } else if (newParent.getAssociation().getId() == selectedContent.getAssociation().getId()) {
            // Do not allow a page to be pasted onto itself.
            error = "aksess.copypaste.recursion";
        } else if (allowedAssociations.size() == 0) {
            // Not allowed to publish here
            error = "aksess.copypaste.notallowed";
        }

        if (error != null) {
            model.put("error", error);
            return new ModelAndView(errorView, model);
        } else {

            model.put("isCopy", isCopy);
            model.put("pasteShortCut", param.getBoolean("pasteShortCut"));
            model.put("uniqueId", selectedPageAssociationId);
            model.put("newParentId", newParentCid.getAssociationId());
            model.put("selectedContent", selectedContent);
            model.put("selectedContentTitle", selectedContentTitle);
            model.put("parentTitle", parentTitle);
            model.put("allowedAssociations", allowedAssociations);
            model.put("allowDuplicate", config.getBoolean("content.duplicate.enabled", true));
            model.put("allowCrossPublish", true);

            // Run plugins
            contentEventListener.beforeConfirmCopyPasteContent(new ContentEvent()
                    .setContent(selectedContent)
                    .setModel(model)
                    .setUser(securitySession.getUser()));

            return new ModelAndView(view, model);
        }
    }

    private boolean copyIsAllowed(int selectedPageAssociationId, ContentManagementService cms, int newParentAssociationId) throws Exception{
        ContentIdentifier cidContentToBeCopied = ContentIdentifier.fromAssociationId(selectedPageAssociationId);
        Content contentToBeCopied = cms.getContent(cidContentToBeCopied);
        ContentIdentifier cidNewParent = ContentIdentifier.fromAssociationId(newParentAssociationId);
        Content newParentContent = cms.getContent(cidNewParent);
        Association parent = cms.getAssociationById(newParentAssociationId);

        List allowedNewParentTemplates = cms.getAllowedChildTemplates(parent.getSiteId(), newParentContent.getContentTemplateId());

        for (Object allowedNewParentTemplate : allowedNewParentTemplates) {
            ContentTemplate allowedContentTemplate = null;
            if (allowedNewParentTemplate instanceof ContentTemplate) {
                allowedContentTemplate = (ContentTemplate) allowedNewParentTemplate;
            } else if (allowedNewParentTemplate instanceof DisplayTemplate) {
                allowedContentTemplate = ((DisplayTemplate) allowedNewParentTemplate).getContentTemplate();
            }
            if (allowedContentTemplate != null && allowedContentTemplate.getId() == contentToBeCopied.getContentTemplateId()) {
                return true;
            }
        }
        return false;

    }

    private List<AssociationCategory> getAssociationCategories(ContentTemplate template) {
        List<AssociationCategory> tmpAllowedAssociations = template.getAssociationCategories();
        if (tmpAllowedAssociations.size() == 0 || template.getContentType() != ContentType.PAGE) {
            return Collections.emptyList();
        }

        // Template only holds id of AssociationCategory, get complete AssociationCategory from cache
        List<AssociationCategory> allAssociations = templateConfigurationCache.getTemplateConfiguration().getAssociationCategories();
        List<AssociationCategory> allowedAssociations = new ArrayList<>();
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

    public void setErrorView(String errorView) {
        this.errorView = errorView;
    }

    public void setView(String view) {
        this.view = view;
    }

    public void setIllegalParentTemplateView(String illegalParentTemplateView) {
        this.illegalParentTemplateView = illegalParentTemplateView;
    }
}
