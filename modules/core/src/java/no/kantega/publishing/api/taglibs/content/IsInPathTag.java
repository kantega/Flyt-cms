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

package no.kantega.publishing.api.taglibs.content;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.util.RequestHelper;
import no.kantega.publishing.content.api.ContentIdHelper;
import no.kantega.publishing.security.SecuritySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public class IsInPathTag extends ConditionalTagSupport {
    private static final Logger log = LoggerFactory.getLogger(IsInPathTag.class);
    private static ContentIdHelper contentIdHelper;

    private String contentId = null;
    private Content contentObject = null;
    private boolean negate = false;

    public void setContentid(String contentId) {
        this.contentId = contentId;
    }

    public void setNegate(boolean negate) {
        this.negate = negate;
    }

    public void setObj(Content contentObject) {
        this.contentObject = contentObject;
    }

    protected boolean condition() {
        try {
            HttpServletRequest  request  = (HttpServletRequest)pageContext.getRequest();
            HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();

            if (contentId != null) {
                try {

                    Content content = contentObject != null ? contentObject :  (Content)request.getAttribute("aksess_this");

                    if (content == null) {
                        // Ikke hentet side
                        ContentManagementService cs = new ContentManagementService(request);
                        if(contentIdHelper == null){
                            contentIdHelper = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext()).getBean(ContentIdHelper.class);
                        }
                        ContentIdentifier contentIdentifier = contentIdHelper.fromRequest(request);
                        content = cs.getContent(contentIdentifier, true);
                        RequestHelper.setRequestAttributes(request, content);
                    }

                    if (content == null) {
                        return negate;
                    }

                    Association association = content.getAssociation();

                    ContentIdentifier cid;
                    try {
                        int aId = Integer.parseInt(contentId);
                        cid = ContentIdentifier.fromAssociationId(aId);
                    } catch (NumberFormatException e) {
                        cid = contentIdHelper.fromSiteIdAndUrl(content.getAssociation().getSiteId(), contentId);
                    }


                    String path = association.getPath();
                    int associationId = cid.getAssociationId();
                    if (content.getAssociation().getId() == associationId
                            || path.contains("/" + associationId + "/")) {
                        return !negate;
                    }

                } catch (NotAuthorizedException e) {
                    SecuritySession session = SecuritySession.getInstance(request);
                    if (session.isLoggedIn()) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    } else {
                        // Gå til loginside
                        session.initiateLogin(request, response);
                    }
                } catch (ContentNotFoundException e) {
                    // Ikke nødvendig
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }

        return negate;
    }
}
