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

package no.kantega.publishing.api.taglibs.util;

import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.util.RequestHelper;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import no.kantega.commons.log.Log;
import no.kantega.commons.util.HttpHelper;

import javax.servlet.jsp.jstl.core.ConditionalTagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.http.HttpServletRequest;

public class IsEditableByUserTag  extends ConditionalTagSupport {
    private static final String SOURCE = "aksess.IsEditableByUserTag";
    private Content contentObject;

    protected boolean condition() {
        try {
            HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
            ContentManagementService cms = new ContentManagementService(request);
            SecuritySession ss = cms.getSecuritySession();

            if (!ss.isLoggedIn()) {
                return false;
            }

            if (contentObject == null) {
                contentObject = (Content)request.getAttribute("aksess_this");
            }
            if (contentObject == null) {
                // Hent denne siden
                contentObject = cms.getContent(new ContentIdentifier(request), true);
                RequestHelper.setRequestAttributes(request, contentObject);
            }

            if (contentObject != null) {
                if (ss.isUserInRole(Aksess.getAuthorRoles()) || ss.isUserInRole(Aksess.getAdminRole())) {
                    if (ss.isAuthorized(contentObject, Privilege.UPDATE_CONTENT)) {
                        return true;
                    }
                }
            }

            return false;
        } catch (ContentNotFoundException e) {
            // Normalt
        } catch (Exception e) {
            System.err.println(e);
            Log.error(SOURCE, e, null, null);
        }

        return false;
    }

    public void setObj(Content obj) {
        this.contentObject = obj;
    }

}