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
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.Locale;

public class DeleteTag extends AbstractSimpleEditTag {
    private static WebApplicationContext webApplicationContext;

    private String associationId = null;
    private String confirmationlabel = null;
    private String bundle = null;

    public int doAfterBody() throws JspException {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        JspWriter out = bodyContent.getEnclosingWriter();
        String body = bodyContent.getString();

        try {
            if (content == null) {
                content = AttributeTagHelper.getContent(pageContext, collection, associationId);
            }
            if (webApplicationContext == null) {
                webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
            }
            SecuritySession session = webApplicationContext.getBean(SecuritySession.class);
            if (content != null && session.isAuthorized(content, Privilege.APPROVE_CONTENT)) {
                StringBuilder link = new StringBuilder();
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

                String txt = null;
                if(confirmationlabel != null && bundle != null){
                    txt = LocaleLabels.getLabel(confirmationlabel, bundle, locale);
                }else{
                    txt =  LocaleLabels.getLabel("aksess.confirmdelete.mini", locale);
                }

                link.append(" href=\"Javascript:if (confirm('").append(txt).append("')) location.href='");
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

        } catch (SystemException | IOException e) {
            throw new JspException(e);
        } catch (NotAuthorizedException e) {
            //
        } finally {
            bodyContent.clearBody();
        }

        resetVars();
        associationId = null;
        confirmationlabel = null;
        bundle = null;
        return SKIP_BODY;
    }

    public void setAssociationid(String associationid) {
        this.associationId = associationid;
    }

    public void setConfirmationlabel(String confirmationlabel) {
        this.confirmationlabel = confirmationlabel;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public void setConfirmmultipledelete(boolean tmp) {

    }
}
