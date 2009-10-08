package no.kantega.publishing.modules.forms.tags;

import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.data.attributes.EditableformAttribute;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.commons.log.Log;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.io.IOException;

/**
 *
 */
public class RenderFormTag extends TagSupport {
    @Override
    public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        Content content = (Content)request.getAttribute("aksess_this");
        if (content != null) {
            List attributes = content.getAttributes(AttributeDataType.CONTENT_DATA);
            for (int i = 0; i < attributes.size(); i++) {
                Attribute attr = (Attribute)attributes.get(i);
                if (attr instanceof EditableformAttribute) {
                    try {
                        out.print(attr.getValue());
                    } catch (IOException e) {
                        Log.error(getClass().getName(), e, null, null);
                        throw new JspTagException(getClass().getName() + ":" + e.getMessage());
                    }
                }
            }
        }

        return SKIP_BODY;
    }
}
