package no.kantega.publishing.api.taglibs.content;

import no.kantega.publishing.api.content.attribute.AttributeDataType;
import no.kantega.publishing.api.taglibs.content.util.AttributeTagHelper;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.AttributeProperty;
import no.kantega.publishing.security.SecuritySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

/**
 * @author Kristian Lier Selnæs
 */
public abstract class AbstractAttributeConditionTag extends ConditionalTagSupport {
    private static final Logger log = LoggerFactory.getLogger(AbstractAttributeConditionTag.class);
    private String name = null;
    private String contentId = null;
    private String collection = null;
    private boolean negate = false;
    private AttributeDataType attributeType = AttributeDataType.CONTENT_DATA;
    private Content contentObject = null;
    private String repeater;
    private boolean inheritFromAncestors = false;

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

    public void setNegate(boolean negate) {
        this.negate = negate;
    }

    public void setAttributetype(String attr) {
        if (attr.equalsIgnoreCase("metadata")) {
            attributeType = AttributeDataType.META_DATA;
        } else {
            attributeType = AttributeDataType.CONTENT_DATA;
        }
    }

    public void setInheritfromancestors(boolean inheritFromAncestors) {
        this.inheritFromAncestors = inheritFromAncestors;
    }

    public void setRepeater(String repeater) {
        this.repeater = repeater;
    }

    protected boolean condition() {
        boolean result = false;
        try {
            if (contentObject == null) {
                contentObject = AttributeTagHelper.getContent(pageContext, collection, contentId, repeater);
            }

            if (contentObject != null) {

                GetAttributeCommand cmd = new GetAttributeCommand();
                cmd.setName(AttributeTagHelper.getAttributeName(pageContext, name, repeater));
                cmd.setProperty(AttributeProperty.VALUE);
                cmd.setAttributeType(attributeType);

                SecuritySession session = SecuritySession.getInstance((HttpServletRequest) pageContext.getRequest());
                String attributeValue = AttributeTagHelper.getAttribute(session, contentObject, cmd, inheritFromAncestors);

                result = evaluateCondition(attributeValue);
            } else {
                result = getDefaultConditionIfNoContent();
                log.debug( "Content object was null. ContentId: {}", contentId);
            }
        } catch (Exception e) {
            log.error("", e);
        }

        if (negate) {
            return !result;
        } else {
            return result;
        }
    }

    /**
     * Method to be implemented by subclasses. Performs the actual condition evaluation for the concrete subclass tag
     * based on the current content and attribute.
     * @param attributeValue the attributeValue to evaluate condition on
     * @return true if the condition is met, otherwise false.
     */
    protected abstract boolean evaluateCondition(String attributeValue);

    protected boolean getDefaultConditionIfNoContent() {
        return false;
    }

    public int doEndTag() throws JspException {
        contentId = null;
        name = null;
        collection = null;
        attributeType = AttributeDataType.CONTENT_DATA;
        negate = false;
        inheritFromAncestors = false;
        contentObject = null;
        repeater = null;

        return super.doEndTag();
    }
}
