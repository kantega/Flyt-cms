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
import no.kantega.commons.util.LocaleLabels;
import no.kantega.commons.util.URLHelper;
import no.kantega.publishing.api.taglibs.content.util.AttributeTagHelper;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.Locale;

/**
 * Author: Kristian Lier Seln�s, Kantega AS
 * Date: 30.mai.2008
 * Time: 12:20:25
 */
public class DeleteTag extends AbstractSimpleEditTag {

    private static final String SOURCE = "no.kantega.publishing.api.taglibs.mini.CreateTag";
    private String associationId = null;

    public int doAfterBody() throws JspException {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        JspWriter out = bodyContent.getEnclosingWriter();
        String body = bodyContent.getString();

        try {
            if (content == null) {
                content = AttributeTagHelper.getContent(pageContext, collection, associationId);
            }
            SecuritySession securitySession = SecuritySession.getInstance(request);
            if (content != null && securitySession.isAuthorized(content, Privilege.APPROVE_CONTENT)) {
                StringBuffer link = new StringBuffer();
                link.append("<a");
                if (cssclass != null) {
                    link.append(" class=\"").append(cssclass).append("\"");
                }
                if (linkId != null) {
                    link.append(" id=\"").append(linkId).append("\"");
                }

                Locale locale = (Locale)request.getAttribute("aksess_locale");
                if (locale == null) {
                    locale = new Locale("no", "NO");
                }

                String txt = LocaleLabels.getLabel("aksess.confirmdelete.mini", locale);

                link.append(" href=\"Javascript:if (confirm('" + txt + "')) location.href='");
                link.append(URLHelper.getRootURL(request));
                link.append("admin/publish/SimpleDeleteContent.action?associationId=");
                link.append(content.getAssociation().getId());
                if (redirectUrl != null) {
                    link.append("&amp;redirectUrl=");
                    link.append(redirectUrl);
                }
                link.append("'");
                link.append("\">");
                link.append(body);
                link.append("</a>");

                out.print(link.toString());
            }

        } catch (SystemException e) {
            throw new JspException(e);
        } catch (NotAuthorizedException e) {
            //
        } catch (IOException e) {
            throw new JspException(e);
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

    public void setConfirmmultipledelete(boolean tmp) {
        
    }
}
