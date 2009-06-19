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
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Site;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.cache.SiteCache;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.util.RequestHelper;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import no.kantega.publishing.api.taglibs.content.util.AttributeTagHelper;
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.log.Log;
import no.kantega.commons.util.URLHelper;
import no.kantega.commons.util.HttpHelper;

import javax.servlet.jsp.jstl.core.ConditionalTagSupport;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

public class EditLinkTag  extends BodyTagSupport {
    private static final String SOURCE = "aksess.EditLinkTag";

    private String cssStyle = null;
    private String cssClass = null;

    public void setStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    public void setCssclass(String cssClass) {
        this.cssClass = cssClass;
    }

    public int doStartTag()  throws JspException {
        return EVAL_BODY_TAG;
    }

    public int doAfterBody() throws JspException {
        try {
            HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
            ContentManagementService cms = new ContentManagementService(request);

            HttpSession session = request.getSession(false);
            if (HttpHelper.isAdminMode(request)) {
                return SKIP_BODY;
            }

            String body = bodyContent.getString();
            JspWriter out = bodyContent.getEnclosingWriter();

            Content current = (Content)request.getAttribute("aksess_this");
            if (current == null) {
                // Hent denne siden
                current = new ContentManagementService(request).getContent(new ContentIdentifier(request), true);
                RequestHelper.setRequestAttributes(request, current);
            }

            if (current != null) {
                String root = URLHelper.getRootURL(request);
                out.print("<a href=\"" + root + "admin/?thisId=" + current.getAssociation().getId() + "\"");
                if (cssStyle != null) {
                    out.print(" style=\"" + cssStyle + "\"");
                }
                if (cssClass != null) {
                    out.print(" class=\"" + cssClass + "\"");
                }
                out.print(">");

                if(body != null) {
                    out.print(body);
                }
                out.print("</a>\n");
            }
        } catch (ContentNotFoundException e) {
            // Gjør ingenting her nei, siden er ikke redigerbar
        } catch (Exception e) {
            System.err.println(e);
            Log.error(SOURCE, e, null, null);
            throw new JspTagException(SOURCE + ":" + e.getMessage());
        } finally {
            bodyContent.clearBody();
        }

        cssStyle = null;
        cssClass = null;

        return SKIP_BODY;
     }
}
