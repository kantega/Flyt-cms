package no.kantega.publishing.modules.forms.tags;

import no.kantega.commons.log.Log;
import no.kantega.publishing.common.data.Content;

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import no.kantega.commons.util.LocaleLabels;
import no.kantega.publishing.modules.forms.model.FormSubmission;
import no.kantega.publishing.modules.forms.validate.FormError;

/**
 * Tag used to create forms
 *
 */
public class FormTag extends BodyTagSupport {

    private String action = null;
    private String errortext = null;
    private boolean clientvalidation = false;

    @Override
    public int doAfterBody() throws JspException {
        String body = bodyContent.getString();
        JspWriter out = bodyContent.getEnclosingWriter();
        boolean hasErrors = false;

        try {
            StringBuffer html = new StringBuffer();

            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            Content content = (Content) request.getAttribute("aksess_this");
            Locale locale = (Locale) request.getAttribute("aksess_locale");

            hasErrors = (request.getAttribute("hasErrors") != null) ? (Boolean) (request.getAttribute("hasErrors")) : false;

            String root = request.getContextPath() + "/";

            FormSubmission formSubmission = (FormSubmission)request.getAttribute("formSubmission");
            if (hasErrors && formSubmission != null) {
                if (errortext == null || errortext.length() == 0) {
                    errortext = LocaleLabels.getLabel("aksess.formerror.header", locale);
                }
                List<FormError> errors = formSubmission.getErrors();
                if (errors != null && errors.size() > 0) {
                    html.append("<div id=\"form_Error\" class=\"formErrors\">");
                    html.append(errortext);
                    html.append("<ul>");
                    // Display error messages
                    for (FormError error : errors) {
                        html.append("<li>" + error.getField() + " " + LocaleLabels.getLabel(error.getMessage(), locale) + "</li>");
                    }
                    html.append("</ul></div>");
                }
            }

            html.append("<form method=\"post\" action=\"");
            if (action == null && content != null) {
                action = root + "content.ap?thisId=" + content.getAssociation().getId();
            } else {
                action = "";
            }
            html.append(action);
            html.append("\">");
            out.print(html.toString());
            if (body != null) {
                out.print(body);
            }
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
        this.clientvalidation = clientvalidation;
    }
}
