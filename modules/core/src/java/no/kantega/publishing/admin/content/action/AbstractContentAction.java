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

import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.api.content.ContentTemplateAO;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentTemplate;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 *
 */
public abstract class AbstractContentAction extends AbstractController {

    @Autowired
    private ContentTemplateAO contentTemplateAO;

    protected void setRequestVariables(HttpServletRequest request, Content current, ContentManagementService aksessService, Map<String, Object> model) {
        SecuritySession securitySession = SecuritySession.getInstance(request);

        model.put("isStartPage", current.getAssociation().getParentAssociationId() == 0);

        model.put("isAdmin", securitySession.isUserInRole(Aksess.getAdminRole()));

        ContentTemplate contentTemplate = contentTemplateAO.getTemplateById(current.getContentTemplateId());
        model.put("hearingEnabled", contentTemplate.isHearingEnabled() && current.getStatus() != ContentStatus.HEARING);

        model.put("toggleSearchableEnabled", contentTemplate.isSearchable());

        ContentStatus saveStatus = ContentStatus.WAITING_FOR_APPROVAL;
        if (securitySession.isAuthorized(current, Privilege.APPROVE_CONTENT)) {
            // User is authorized to publish page
            saveStatus = ContentStatus.PUBLISHED;
            model.put("canPublish", Boolean.TRUE);

            if (securitySession.isAuthorized(current, Privilege.FULL_CONTROL)) {
                if (current.getType() == ContentType.PAGE) {
                    model.put("canChangeTemplate", Boolean.TRUE);
                    model.put("allowedTemplates", aksessService.getAllowedDisplayTemplates(current));
                }
            }
        }

        model.put("canEditContentMetadata", canEditContentMetadata(securitySession));
        model.put("canEditContentAlias", canEditContentAlias(securitySession));
        model.put("canEditContentTopics", canEditContentTopics(securitySession));

        model.put("topicMapsEnabled", Aksess.isTopicMapsEnabled());

        model.put("isDeveloper", securitySession.isUserInRole(Aksess.getDeveloperRole()));

        model.put("hasUnsavedChanges", current.isModified());

        model.put("saveStatus", saveStatus);
    }

    /**
     * Editing Metadata can be restricted to certain roles
     * @return true if editing is allowed
     */
    private boolean canEditContentMetadata(SecuritySession securitySession){
        String[] restrictRoles =  Aksess.getConfiguration().getStrings("restrict.editing.content.metadata");
        return noRestriction(restrictRoles) || securitySession.isUserInRole(restrictRoles);
    }

    /**
     * Editing Alias can be restricted to certain roles
     * @return true if editing is allowed
     */
    private boolean canEditContentAlias(SecuritySession securitySession){
        String[] restrictRoles =  Aksess.getConfiguration().getStrings("restrict.editing.content.alias");
        return noRestriction(restrictRoles) || securitySession.isUserInRole(restrictRoles);
    }

    /**
     * Editing Topics can be restricted to certain roles
     * @return true if editing is allowed
     */
    private boolean canEditContentTopics(SecuritySession securitySession){
        String[] restrictRoles =  Aksess.getConfiguration().getStrings("restrict.editing.content.topics");
        return noRestriction(restrictRoles) || securitySession.isUserInRole(restrictRoles);
    }

    private boolean noRestriction(String[] restrictMetadataRoles) {
        return restrictMetadataRoles == null || restrictMetadataRoles.length == 0;
    }
}
