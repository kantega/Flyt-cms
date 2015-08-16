package no.kantega.publishing.api.taglibs.content;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.publishing.api.content.attribute.AttributeDataType;
import no.kantega.publishing.api.taglibs.content.util.AttributeTagHelper;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.RepeaterAttribute;
import no.kantega.publishing.security.SecuritySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;

public class RepeatAttributesTag extends BodyTagSupport {
    private static final Logger log = LoggerFactory.getLogger(RepeatAttributesTag.class);

    private String name = null;
    private String contentId = null;
    private String collection = null;
    private AttributeDataType attributeDataType= AttributeDataType.CONTENT_DATA;
    private Content contentObject = null;

    private int offset = 0;
    private int numberOfRows = 0;

    public int doStartTag() throws JspException {

        try {
            if (contentObject == null) {
                try {
                    contentObject = AttributeTagHelper.getContent(pageContext, collection, contentId);
                } catch (NotAuthorizedException e) {
                    HttpServletRequest request  = (HttpServletRequest)pageContext.getRequest();
                    HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();

                    SecuritySession session = SecuritySession.getInstance(request);
                    if (session.isLoggedIn()) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    } else {
                        // Gå til loginside
                        session.initiateLogin(request, response);
                    }
                }

                if (contentObject != null) {
                    pageContext.setAttribute(AttributeTagHelper.REPEATER_CONTENT_OBJ_PAGE_VAR + name, contentObject);

                    Attribute attribute = contentObject.getAttribute(name, attributeDataType);
                    if (attribute instanceof RepeaterAttribute) {
                        RepeaterAttribute repeater = (RepeaterAttribute)attribute;
                        numberOfRows = repeater.getNumberOfRows();

                    } else if(attribute != null){
                        log.error( "Attribute:" + name + " is not a RepeaterAttribute");
                    }
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }

        return doIter();
    }

    private int doIter() {
        if (offset < numberOfRows) {
            pageContext.setAttribute(AttributeTagHelper.REPEATER_OFFSET_PAGE_VAR + name, offset);

            return EVAL_BODY_BUFFERED;
        } else {
            return SKIP_BODY;
        }
    }


    /**
     * Writes content of tag, returns status to iterate
     * @return
     * @throws JspException
     */
    public int doAfterBody() throws JspException {
        try {
            bodyContent.writeOut(getPreviousOut());
        } catch (IOException e) {
            throw new JspTagException("GetCollectionTag: " + e.getMessage());
        } finally {
            bodyContent.clearBody();
        }

        offset++;
        return doIter();
    }


    /**
     * Cleanup after tag is finished
     * @return EVAL_PAGE
     */
    public int doEndTag() throws JspException {
        offset = 0;
        numberOfRows = 0;
        name = null;
        collection = null;
        contentId = null;
        contentObject = null;

        pageContext.removeAttribute(AttributeTagHelper.REPEATER_OFFSET_PAGE_VAR + name);
        pageContext.removeAttribute(AttributeTagHelper.REPEATER_CONTENT_OBJ_PAGE_VAR + name);

        return super.doEndTag();
    }

    public void setName(String name) {
        this.name = name.toLowerCase();
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public void setContentid(String contentId) {
        if (contentId != null && contentId.length() == 0) {
            contentId = null;
        }
        this.contentId = contentId;
    }

    public void setObj(Content obj) {
        this.contentObject = obj;
    }

    public void setAttributetype(String attr) {
        if (attr.equalsIgnoreCase("metadata")) {
            attributeDataType = AttributeDataType.META_DATA;
        } else {
            attributeDataType = AttributeDataType.CONTENT_DATA;
        }
    }
}
