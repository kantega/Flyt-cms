/*
 * Copyright 2010 Kantega AS
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

package no.kantega.publishing.modules.forms.tags;

import no.kantega.commons.log.Log;
import no.kantega.commons.util.LocaleLabels;
import no.kantega.publishing.api.forms.model.FormSubmission;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.modules.forms.validate.FormError;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Tag used to create forms
 *
 */
public class FormTag extends BodyTagSupport {

    private String action = null;
    private String errortext = null;

    @Override
    public int doAfterBody() throws JspException {
        String body = bodyContent.getString();
        JspWriter out = bodyContent.getEnclosingWriter();
        boolean hasErrors = false;

        try {
            StringBuilder html = new StringBuilder();

            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            Content content = (Content) request.getAttribute("aksess_this");
            Locale locale = (Locale) request.getAttribute("aksess_locale");

            hasErrors = (request.getAttribute("hasErrors") != null) ? (Boolean) (request.getAttribute("hasErrors")) : false;

            FormSubmission formSubmission = (FormSubmission)request.getAttribute("formSubmission");
            if (hasErrors && formSubmission != null) {
                if (errortext == null || errortext.length() == 0) {
                    errortext = LocaleLabels.getLabel("aksess.formerror.header", locale);
                }


                List<FormError> errors = (List<FormError>)request.getAttribute("formErrors");
                if (errors != null && errors.size() > 0) {
                    html.append("<div id=\"form_Error\" class=\"formErrors\">");
                    html.append(errortext);
                    html.append("<ul>");
                    // Display error messages
                    for (FormError error : errors) {
                        html.append("<li>").append(error.getField()).append(" ").append(LocaleLabels.getLabel(error.getMessage(), locale)).append("</li>");
                    }
                    html.append("</ul></div>");
                }
            }

            html.append("<form method=\"post\" action=\"");
            if (action == null && content != null) {
                action = content.getUrl();
            } else {
                action = "";
            }
            html.append(action);
            html.append("\">");
            out.print(html.toString());
            if (body != null) {
                out.print(body);
            }
            out.print("<input type=\"hidden\" name=\"isAksessFormSubmit\" value=\"true\">");
            out.print("</form>\n");
        } catch (IOException e) {
            Log.error(getClass().getName(), e, null, null);
            throw new JspTagException(getClass().getName() + ":" + e.getMessage());
        }
        action = null;
        errortext = null;
        return SKIP_BODY;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setErrortext(String errortext) {
        this.errortext = errortext;
    }

    @Deprecated
    public void setClientvalidation(boolean clientvalidation) {

    }
}
