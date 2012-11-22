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

package no.kantega.publishing.api.taglibs.mini;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public class IsEditableDraftTag extends ConditionalTagSupport {

    private Content contentObject;

    protected boolean condition() throws JspTagException {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

        if (contentObject == null) {
            contentObject = (Content)request.getAttribute("aksess_this");
        }
        if (contentObject != null) {
            ContentManagementService cms = new ContentManagementService(request);
            SecuritySession securitySession = cms.getSecuritySession();
            ContentIdentifier cid =  ContentIdentifier.fromAssociationId(contentObject.getAssociation().getAssociationId());
            try {
                Content lastVersion = cms.getLastVersionOfContent(cid);
                if (lastVersion != null && lastVersion.getStatus() == ContentStatus.DRAFT && securitySession.isAuthorized(lastVersion, Privilege.UPDATE_CONTENT)) {
                    return true;
                }
            } catch (NotAuthorizedException e) {
                // Do nothing
            }
        }

        return false;
    }

    public void setObj(Content obj) {
        this.contentObject = obj;
    }
}
