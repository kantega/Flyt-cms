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

import no.kantega.publishing.api.taglibs.content.util.AttributeTagHelper;
import no.kantega.publishing.common.data.Content;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public class AttributeIfAliasEqualsTag  extends ConditionalTagSupport {
    private static final Logger log = LoggerFactory.getLogger(AttributeIfAliasEqualsTag.class);

    private String value = null;
    private boolean negate = false;
    private String contentId = null;
    private String collection = null;

    public void setNegate(boolean negate) {
        this.negate = negate;

    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public void setContentid(String contentId) {
        this.contentId = contentId;
    }

    protected boolean condition()  {
        try {
            Content content = AttributeTagHelper.getContent(pageContext, collection, contentId);
            boolean aliasEquals = false;
            if (content!=null && content.getAlias()!=null )
                aliasEquals = content.getAlias().equalsIgnoreCase(value);

            return negate ? !aliasEquals : aliasEquals;

        } catch (Exception e) {
            log.error("Error getting content", e);
        }

        return false;
    }

    public int doEndTag() throws JspException  {
        value = null;
        contentId = null;
        collection = null;

        return super.doEndTag();
    }
}

