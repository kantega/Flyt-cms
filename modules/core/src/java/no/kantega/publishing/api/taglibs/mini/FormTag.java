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
import no.kantega.publishing.admin.AdminSessionAttributes;
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

/**
 * User: Kristian Seln�s
 * Date: 23.mar.2010
 * Time: 09:22:16
 */
public class FormTag extends BodyTagSupport {

    private boolean allowDraft = false;

    private String action;

    @Override
    public int doAfterBody() throws JspException {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        Content currentEditContent = (Content) request.getAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT);

        SecuritySession securitySession = SecuritySession.getInstance(request);
        boolean canApprove = securitySession.isAuthorized(currentEditContent, Privilege.APPROVE_CONTENT);
        int contentStatus = (canApprove)? ContentStatus.PUBLISHED : ContentStatus.WAITING_FOR_APPROVAL;

        if (action == null) {
            action = request.getContextPath()+"/admin/publish/SimpleEditContent.action";
        }

        String redirectUrl = request.getParameter("redirectUrl");
        String cancelUrl = request.getParameter("cancelUrl");
        if (cancelUrl == null) {
            cancelUrl = redirectUrl;
        }

        try {
            String body = bodyContent.getString();
            JspWriter out = bodyContent.getEnclosingWriter();

            //TODO: Improvement: Use the page's language if this is one of Aksess' supported admin languages.
            Locale locale = Aksess.getDefaultAdminLocale();

            if (!canApprove) {
                out.write("<div class=\"ui-state-highlight\">"+ LocaleLabels.getLabel("aksess.simpleedit.approvereminder", locale)+"</div>");
            }

            if (currentEditContent.getStatus() == ContentStatus.DRAFT) {
                out.write("<div class=\"ui-state-highlight\">"+ LocaleLabels.getLabel("aksess.simpleedit.editdraft", locale)+"</div>");
            }

            out.write("<form name=\"myform\" id=\"EditContentForm\" action=\"" + action + "\" method=\"post\" enctype=\"multipart/form-data\">");
            out.write("    <input type=\"hidden\" id=\"ContentStatus\" name=\"status\" value=\"" + contentStatus + "\">");
            out.write("    <input type=\"hidden\" name=\"currentId\" value=\"" + currentEditContent.getId() + "\">");
            out.write("    <input type=\"hidden\" id=\"ContentIsModified\" name=\"isModified\" value=\"true\">");
            if (redirectUrl != null && redirectUrl.trim().length() > 0 ) {
                out.write("    <input type=\"hidden\" name=\"redirectUrl\" value=\"" + redirectUrl + "\">");
            }
            if (cancelUrl != null && cancelUrl.trim().length() > 0 ) {
                out.write("    <input type=\"hidden\" name=\"cancelUrl\" value=\"" + cancelUrl + "\">");
            }

            out.write(body);

            String submitButtonLabel = (canApprove)? LocaleLabels.getLabel("aksess.button.publish", locale) : LocaleLabels.getLabel("aksess.button.save", locale);

            out.write("    <input class=\"editContentButton submit\" type=\"submit\" value=\""+submitButtonLabel+"\">");

            if (allowDraft) {
                out.write("    <input class=\"editContentButton draft\" type=\"button\" value=\""+LocaleLabels.getLabel("aksess.button.savedraft", locale)+"\" onclick=\"document.myform.status.value=" + ContentStatus.DRAFT + ";document.myform.submit()\">");
            }

            String cancelAction = request.getContextPath()+"/SimpleEditCancel.action";
            if (cancelUrl != null && cancelUrl.trim().length() > 0 ) {
                cancelAction = cancelAction+"?redirectUrl="+cancelUrl;
            }
            out.write("    <input class=\"editContentButton cancel\" type=\"button\" value=\""+LocaleLabels.getLabel("aksess.button.cancel", locale)+"\" onclick=\"window.location.href ='"+cancelAction+"'\">");
            out.write("</form>");

            allowDraft = false;

        } catch (IOException e) {
            Log.error(this.getClass().getName(), e, null, null);
        }

        return SKIP_BODY;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setAllowdraft(boolean allowDraft) {
        this.allowDraft = allowDraft;
    }
}

