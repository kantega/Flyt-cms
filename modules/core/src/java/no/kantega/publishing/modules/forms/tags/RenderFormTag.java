package no.kantega.publishing.modules.forms.tags;

import no.kantega.publishing.api.forms.model.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

/**
 *
 */
public class RenderFormTag extends TagSupport {
    private static final Logger log = LoggerFactory.getLogger(RenderFormTag.class);
    @Override
    public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        Form form = (Form)request.getAttribute("form");
        if (form != null) {
            try {
                out.print(form.getFormDefinition());
            } catch (IOException e) {
                log.error("", e);
                throw new JspTagException(getClass().getName() + ":" + e.getMessage());
            }
        }

        return SKIP_BODY;
    }
}
