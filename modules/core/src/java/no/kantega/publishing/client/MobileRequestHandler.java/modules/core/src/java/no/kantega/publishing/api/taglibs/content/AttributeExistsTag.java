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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

/**
 *
 */
public class AttributeExistsTag extends ConditionalTagSupport {
    private static final String SOURCE = "aksess.AttributeExistsTag";

    private String name = null;
    private String contentId = null;
    private String collection = null;
    private boolean negate = false;
    private int attributeType = AttributeDataType.CONTENT_DATA;
    private Content contentObject = null;

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


    protected boolean condition() {
        try {
            if (contentObject == null) {
                contentObject = AttributeTagHelper.getContent(pageContext, collection, contentId);
            }
            GetAttributeCommand cmd = new GetAttributeCommand();
            cmd.setName(name);
            cmd.setProperty(AttributeProperty.VALUE);
            cmd.setAttributeType(attributeType);

            String result = AttributeTagHelper.getAttribute(contentObject, cmd, inheritFromAncestors);
            if (result != null && result.length() > 0) {
                return !negate;
            }
        } catch (Exception e) {
            System.err.println(e);
            Log.error(SOURCE, e, null, null);
        }

        return negate;
    }

    public int doEndTag() throws JspException  {
        contentId = null;
        name = null;
        collection = null;
        attributeType = AttributeDataType.CONTENT_DATA;
        negate = false;
        inheritFromAncestors = false;
        contentObject = null;
        
        return super.doEndTag();
    }
}
