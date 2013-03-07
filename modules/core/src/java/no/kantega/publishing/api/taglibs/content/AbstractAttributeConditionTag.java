package no.kantega.publishing.api.taglibs.content;

import no.kantega.commons.log.Log;
import no.kantega.commons.util.URLHelper;
import no.kantega.publishing.api.taglibs.content.util.AttributeTagHelper;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.enums.AttributeDataType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

/**
 * @author Kristian Lier Selnæs
 */
public abstract class AbstractAttributeConditionTag extends ConditionalTagSupport {
    private String name = null;
    private String contentId = null;
    private String collection = null;
    private boolean negate = false;
    private int attributeType = AttributeDataType.CONTENT_DATA;
    private Content contentObject = null;
    private String repeater;
    private boolean inheritFromAncestors = false;
    private final String CATEGORY = getClass().getName();

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
                Attribute attribute = contentObject.getAttribute(AttributeTagHelper.getAttributeName(pageContext, name, repeater), attributeType);
                result = evaluateCondition(contentObject, attribute);
            } else {
                Log.error(CATEGORY, "Content object was null, URL:"  + URLHelper.getCurrentUrl((HttpServletRequest) pageContext.getRequest()));
            }

        } catch (Exception e) {
            Log.error(CATEGORY, e);
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
     * @param content current content object, containing the attribute to evaluate condition on
     * @param attribute the attribute to evaluate condition on
     * @return true if the condition is met, otherwise false.
     */
    protected abstract boolean evaluateCondition(Content content, Attribute attribute);


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
