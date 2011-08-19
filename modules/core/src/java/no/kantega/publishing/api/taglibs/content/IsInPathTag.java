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

import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.util.RequestHelper;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.commons.log.Log;
import no.kantega.commons.exception.NotAuthorizedException;
import sun.rmi.rmic.iiop.IDLNames;

import javax.servlet.jsp.jstl.core.ConditionalTagSupport;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IsInPathTag extends ConditionalTagSupport {
    private static final String SOURCE = "aksess.IsInPathTag";

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
                        content = cs.getContent(new ContentIdentifier(request), true);
                        RequestHelper.setRequestAttributes(request, content);
                    }

                    if (content == null) {
                        return negate;
                    }

                    Association association = content.getAssociation();

                    ContentIdentifier cid;
                    try {
                        int aId = Integer.parseInt(contentId);
                        cid = new ContentIdentifier();
                        cid.setAssociationId(aId);
                    } catch (NumberFormatException e) {
                        cid = new ContentIdentifier(content.getAssociation().getSiteId(), contentId);
                    }


                    String path = association.getPath();
                    if (content.getAssociation().getId() == cid.getAssociationId() || path.indexOf("/" + cid.getAssociationId() + "/") != -1) {
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
            System.err.println(e);
            Log.error(SOURCE, e, null, null);
        }

        return negate;
    }
}
