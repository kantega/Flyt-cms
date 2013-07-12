package no.kantega.publishing.modules.forms.tags;

import no.kantega.publishing.api.forms.model.Form;
import no.kantega.commons.log.Log;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 *
 */
public class RenderFormTag extends TagSupport {
    @Override
    public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        Form form = (Form)request.getAttribute("form");
        if (form != null) {
            try {
                out.print(form.getFormDefinition());
            } catch (IOException e) {
                Log.error(getClass().getName(), e, null, null);
                throw new JspTagException(getClass().getName() + ":" + e.getMessage());
            }
        }

        return SKIP_BODY;
    }
}
