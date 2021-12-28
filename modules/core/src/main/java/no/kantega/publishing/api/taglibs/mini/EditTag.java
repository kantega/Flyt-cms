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
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.URLHelper;
import no.kantega.publishing.api.taglibs.content.util.AttributeTagHelper;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;

public class EditTag extends AbstractSimpleEditTag {
    private static final Logger log = LoggerFactory.getLogger(EditTag.class);

    private String associationId = null;
    private String action;

    public int doAfterBody() throws JspException {
        try {
            HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
            JspWriter out = bodyContent.getEnclosingWriter();
            String body = bodyContent.getString();

            if (content == null) {
                content = AttributeTagHelper.getContent(pageContext, collection, associationId);
            }
            SecuritySession session = SecuritySession.getInstance((HttpServletRequest) pageContext.getRequest());
            if (content != null && session.isAuthorized(content, Privilege.UPDATE_CONTENT)) {

                StringBuilder link = new StringBuilder();
                link.append("<a");
                if (cssclass != null) {
                    link.append(" class=\"").append(cssclass).append("\"");
                }
                if (linkId != null) {
                    link.append(" id=\"").append(linkId).append("\"");
                }
                link.append(" href=\"");
                link.append(URLHelper.getRootURL(request));
                if (action != null) {
                    link.append(action);
                    if (!action.endsWith("?")) {
                        link.append("?");
                    }
                } else {
                	link.append("admin/publish/SimpleEditContent.action?");
                }
                link.append("thisId=");
                link.append(content.getAssociation().getId());
                if (redirectUrl != null) {
                    link.append("&amp;redirectUrl=");
                    link.append(redirectUrl);
                }
                if (cancelUrl != null) {
                    link.append("&amp;redirectUrl=");
                    link.append(cancelUrl);
                }
                link.append("\">");
                link.append(body);
                link.append("</a>");

                out.print(link.toString());
            }
        } catch (IOException | SystemException e) {
            log.error("", e);
            throw new JspTagException(e);
        } catch (NotAuthorizedException e) {
            //
        } finally {
            bodyContent.clearBody();
        }

        resetVars();
        associationId = null;

        return SKIP_BODY;
    }

    public void setAssociationid(String associationid) {
        this.associationId = associationid;
    }

   public void setAction(String action) {
        this.action = action;
    }
}
