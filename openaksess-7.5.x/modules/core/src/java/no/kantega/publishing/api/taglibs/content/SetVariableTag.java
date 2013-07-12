/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.api.taglibs.content;

import no.kantega.commons.log.Log;
import no.kantega.publishing.api.taglibs.content.util.AttributeTagHelper;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.data.enums.AttributeProperty;
import no.kantega.publishing.security.SecuritySession;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 *
 */
public class SetVariableTag extends TagSupport {
    private static final String SOURCE = "aksess.SetVariableTag";

    private String name = null;
    private String attribute = null;
    private String contentId = null;
    private String collection = null;
    private String repeater = null;
    private String format = null;
    private String property = AttributeProperty.HTML;
    private String defaultValue = null;
    private int height = -1;
    private int width  = -1;
    private int maxlen = -1;
    private Content contentObject = null;
    private int attributeType = AttributeDataType.CONTENT_DATA;

    private boolean inheritFromAncestors = false;

    public void setName(String name) {
        //this.name = name.toLowerCase();
    	this.name = name;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public void setRepeater(String repeater) {
        this.repeater = repeater;
    }

    public void setContentid(String contentId) {
        this.contentId = contentId;
    }

    public void setObj(Content obj) {
        this.contentObject = obj;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setMaxlength(int maxlen) {
        this.maxlen = maxlen;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public void setDefaultvalue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setInheritfromancestors(boolean inheritFromAncestors) {
        this.inheritFromAncestors = inheritFromAncestors;
    }

    public void setAttributetype(String attr) {
        if (attr.equalsIgnoreCase("metadata")) {
            attributeType = AttributeDataType.META_DATA;
        } else {
            attributeType = AttributeDataType.CONTENT_DATA;
        }
    }

    public int doStartTag() throws JspException {
        try {
            if (contentObject == null) {
                contentObject = AttributeTagHelper.getContent(pageContext, collection, contentId, repeater);
            }
            GetAttributeCommand cmd = new GetAttributeCommand();
            cmd.setName(AttributeTagHelper.getAttributeName(pageContext, attribute, repeater));
            cmd.setProperty(property);
            cmd.setMaxLength(maxlen);
            cmd.setAttributeType(attributeType);
            cmd.setFormat(format);
            cmd.setWidth(width);
            cmd.setHeight(height);

            SecuritySession session = SecuritySession.getInstance((HttpServletRequest)pageContext.getRequest());
            String result = AttributeTagHelper.getAttribute(session, contentObject, cmd, inheritFromAncestors);
            ServletRequest request = pageContext.getRequest();
            if (defaultValue != null && (result == null || result.length() == 0)) {
                result = defaultValue;
            }
            request.setAttribute(name, result);
        } catch (Exception e) {
            Log.error(SOURCE, e, null, null);
            throw new JspTagException(SOURCE, e);
        }

        return SKIP_BODY;
    }


    public int doEndTag() throws JspException {
        name = null;
        attribute = null;
        contentId = null;
        collection = null;
        repeater = null;
        format = null;
        defaultValue = null;
        property = AttributeProperty.HTML;
        height = -1;
        width  = -1;
        maxlen = -1;
        inheritFromAncestors = false;
        contentObject = null;
        attributeType = AttributeDataType.CONTENT_DATA;        

        return EVAL_PAGE;
    }
}
