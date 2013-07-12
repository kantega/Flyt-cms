package no.kantega.publishing.api.taglibs.content;

import no.kantega.commons.log.Log;
import no.kantega.publishing.admin.content.util.EditContentHelper;
import no.kantega.publishing.api.taglibs.content.util.AttributeTagHelper;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.enums.AttributeDataType;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public class IsHiddenTag extends ConditionalTagSupport {
    private static final String SOURCE = "aksess.IsHiddenTag";

    private String attribute = null;
    private String contentId = null;
    private String collection = null;
    private boolean negate = false;
    private int attributeType = AttributeDataType.CONTENT_DATA;
    private Content contentObject = null;
    private String repeater;

    public void setAttribute(String attribute) {
        this.attribute = attribute.toLowerCase();
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

    public void setRepeater(String repeater) {
        this.repeater = repeater;
    }

    protected boolean condition() {
        try {
            if (contentObject == null) {
                contentObject = AttributeTagHelper.getContent(pageContext, collection, contentId, repeater);
            }

            if (contentObject != null) {
                if (!contentObject.attributesAreUpdatedFromTemplate()) {
                    EditContentHelper.updateAttributesFromTemplate(contentObject);
                }

                String attributeName = AttributeTagHelper.getAttributeName(pageContext, attribute, repeater);

                Attribute attribute = contentObject.getAttribute(attributeName, attributeType);

                if (attribute.isHidden(contentObject)) {
                    return !negate;
                }
            }
        } catch (Exception e) {
            Log.error(SOURCE, e);
        }

        return (negate);
    }

    public int doEndTag() throws JspException {
        contentId = null;
        attribute = null;
        collection = null;
        attributeType = AttributeDataType.CONTENT_DATA;
        negate = false;
        contentObject = null;
        repeater = null;

        return super.doEndTag();
    }
}
