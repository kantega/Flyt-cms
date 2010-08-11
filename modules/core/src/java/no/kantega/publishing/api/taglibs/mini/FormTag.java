/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.api.taglibs.mini;

import no.kantega.commons.log.Log;
import no.kantega.commons.util.LocaleLabels;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.util.Locale;

public class FormTag extends BodyTagSupport {

    private String action;

    @Override
    public int doAfterBody() throws JspException {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        Content currentEditContent = (Content) request.getAttribute("currentContent");
        //TODO: Handle content == null

        SecuritySession securitySession = SecuritySession.getInstance(request);
        boolean canApprove = securitySession.isAuthorized(currentEditContent, Privilege.APPROVE_CONTENT);
        int contentStatus = (canApprove)? ContentStatus.PUBLISHED : ContentStatus.WAITING;

        if (action == null) {
            action = request.getContextPath()+"/admin/publish/SimpleEditSaveContent.action";
        }

        try {
            String body = bodyContent.getString();
            JspWriter out = bodyContent.getEnclosingWriter();

            //TODO: Use the page's language if this is one of Aksess' supported admin languages.
            Locale locale = Aksess.getDefaultAdminLocale();

            if (!canApprove) {

                out.write("<div class=\"ui-state-highlight\">"+ LocaleLabels.getLabel("aksess.simpleedit.approvereminder", locale)+"</div>");
            }
            out.write("<form name=\"myform\" id=\"EditContentForm\" action=\""+action+"\" method=\"post\" enctype=\"multipart/form-data\">");
            out.write("    <input type=\"hidden\" id=\"ContentStatus\" name=\"status\" value=\""+contentStatus+"\">");
            out.write("    <input type=\"hidden\" name=\"currentId\" value=\""+currentEditContent.getId()+"\">");
            out.write("    <input type=\"hidden\" id=\"ContentIsModified\" name=\"isModified\" value=\"true\">");

            out.write(body);

            String submitButtonLabel = (canApprove)? LocaleLabels.getLabel("aksess.button.publiser", locale) : LocaleLabels.getLabel("aksess.button.lagre", locale);
            out.write("    <input class=\"editContentButton submit\" type=\"submit\" value=\""+submitButtonLabel+"\">");
            String cancelAction = request.getContextPath()+"/SimpleEditCancel.action";
            String redirectUrl = request.getParameter("redirectUrl");
            if (redirectUrl != null && redirectUrl.trim().length() > 0 ) {
                cancelAction = cancelAction+"?redirectUrl="+redirectUrl;
            }
            out.write("    <input class=\"editContentButton cancel\" type=\"button\" value=\""+LocaleLabels.getLabel("aksess.button.avbryt", locale)+"\" onclick=\"window.location.href ='"+cancelAction+"'\">");
            out.write("</form>");

        } catch (IOException e) {
            Log.error(this.getClass().getName(), e, null, null);
        }

        return SKIP_BODY;
    }

    public void setAction(String action) {
        this.action = action;
    }
}

