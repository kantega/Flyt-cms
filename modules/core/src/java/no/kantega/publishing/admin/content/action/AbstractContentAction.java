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

import no.kantega.publishing.admin.viewcontroller.AdminController;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.cache.ContentTemplateCache;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentTemplate;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 *
 */
public abstract class AbstractContentAction  extends AdminController {
    protected void setRequestVariables(HttpServletRequest request, Content current, ContentManagementService aksessService, Map<String, Object> model) {
        SecuritySession securitySession = SecuritySession.getInstance(request);

        if (current.getAssociation().getParentAssociationId() == 0) {
            model.put("isStartPage", Boolean.TRUE);
        }

        if (securitySession.isUserInRole(Aksess.getAdminRole())) {
            model.put("isAdmin", Boolean.TRUE);
        }

        ContentTemplate contentTemplate = ContentTemplateCache.getTemplateById(current.getContentTemplateId());
        if (contentTemplate.isHearingEnabled() && current.getStatus() != ContentStatus.HEARING) {
            model.put("hearingEnabled", Boolean.TRUE);
        }

        int saveStatus = ContentStatus.WAITING_FOR_APPROVAL;
        if (securitySession.isAuthorized(current, Privilege.APPROVE_CONTENT)) {
            // User is authorized to publish page
            saveStatus = ContentStatus.PUBLISHED;
            model.put("canPublish", Boolean.TRUE);

            if (current.getType() == ContentType.PAGE) {
                model.put("canChangeTemplate", Boolean.TRUE);
                model.put("allowedTemplates", aksessService.getAllowedDisplayTemplates(current));
            }
        }

        if (Aksess.isTopicMapsEnabled()) {
            model.put("topicMapsEnabled", Boolean.TRUE);
        }

        if (securitySession.isUserInRole(Aksess.getDeveloperRole())) {
            model.put("isDeveloper", Boolean.TRUE);
        }

        if (current.isModified()) {
            model.put("hasUnsavedChanges", current);
        }

        model.put("saveStatus", saveStatus);
    }
}
