package no.kantega.publishing.modules.forms.tags;

import no.kantega.commons.log.Log;
import no.kantega.publishing.common.data.Content;

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Tag used to create forms
 *
 */
public class FormTag extends BodyTagSupport {
    private String action = null;
    private boolean clientvalidation = true;

    @Override
    public int doAfterBody() throws JspException {
        String body = bodyContent.getString();
        JspWriter out = bodyContent.getEnclosingWriter();

        try {
            StringBuffer html = new StringBuffer();

            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            Content content = (Content)request.getAttribute("aksess_this");

            String root = request.getContextPath() + "/";
            if (clientvalidation) {
                html.append("<script type=\"text/javascript\" src=\"");
                html.append(root);
                html.append("aksess/js/aksessforms.js\"></script>\n");
            }
            html.append("<form method=\"post\" action=\"");
            if (action == null && content != null) {
                action = root + "content.ap?thisId=" + content.getAssociation().getId();
            } else {
                action = "";
            }
            html.append(action);
            html.append("\"");
            if (clientvalidation) {
                html.append(" onsubmit=\"return aksessFormValidate()\"");
            }            
            html.append(">");
            out.print(html.toString());
            if(body != null) {
               out.print(body);
            }
            out.print("</form>\n");
        } catch (IOException e) {
            Log.error(getClass().getName(), e, null, null);
            throw new JspTagException(getClass().getName() + ":" + e.getMessage());
        }

        action = null;
        clientvalidation = true;

        return SKIP_BODY;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setClientvalidation(boolean clientvalidation) {
        this.clientvalidation = clientvalidation;
    }
}
