package no.kantega.publishing.api.taglibs.util;

import no.kantega.publishing.common.StripHTML;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Created by IntelliJ IDEA.
 * User: hareve
 * Date: Oct 28, 2010
 * Time: 3:46:57 PM
 */
public class StripMarkupTag extends BodyTagSupport {
    private String tags = null;
    private boolean all = false;
    private boolean skipTags = true;

    @Override
    public int doStartTag() throws JspException {
        return EVAL_BODY_BUFFERED;
    }

    @Override
    public int doAfterBody() throws JspException {
        try {
            StripHTML parser = new StripHTML();
            parser.setAll(all);
            
            String newBody = bodyContent.getString();
            JspWriter out = bodyContent.getEnclosingWriter();
            
            if (tags != null) {
                for (String tag : tags.split(",")) {
                    parser.setTag(tag);
                    newBody = parser.convert(newBody);
                    parser.clear();
                }
            }
            out.write(newBody);
        } catch (Exception e) {
            throw new JspException(e);
        } finally {
            bodyContent.clearBody();
        }
        
        return SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
        tags = null;
        all = false;
        return SKIP_BODY;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setAll(String all) {
        this.all = all.equals("true");
    }

    public void setSkipTags(String skip) {
        this.skipTags = skip.equals("false");
    }
}
