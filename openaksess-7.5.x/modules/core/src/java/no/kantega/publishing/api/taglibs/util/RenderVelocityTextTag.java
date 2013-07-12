package no.kantega.publishing.api.taglibs.util;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class RenderVelocityTextTag extends BodyTagSupport {
    public int doEndTag() throws JspException {

        try {
            String text = bodyContent.getString();
            if (text == null) {
                text = "";
            } else {
                text = renderText(text);
            }

            JspWriter out = pageContext.getOut();
            out.print(text);
        } catch (IOException ioe) {
            throw new JspException(ioe.toString());
        }
        return EVAL_PAGE;
    }

    private String renderText(String text) throws IOException {
        Map<String, Object> parameters = new HashMap<String, Object>();

        Enumeration paramNames = pageContext.getRequest().getAttributeNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String)paramNames.nextElement();
            parameters.put(paramName, pageContext.getRequest().getAttribute(paramName));
        }

        VelocityContext context = new VelocityContext(parameters);

        StringWriter textWriter = new StringWriter();
        Velocity.evaluate(context, textWriter, "RenderVelocityTextTag", text);

        return textWriter.toString();
    }
}
