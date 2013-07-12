/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.content.htmlfilter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import java.util.Stack;

public class RemoveNestedSpanTagsFilter extends XMLFilterImpl {
    private Stack<Boolean> spanTagsStack = new Stack<Boolean>();

    private String parentTag = null;
    private String parentStyle = null;
    private String parentClz = null;

    @Override
    public void startElement(String string, String tagName, String name, Attributes attributes) throws SAXException {
        boolean hasRemovedElement = false;

        if (tagName.equalsIgnoreCase("span") && isSameAsParent(tagName, attributes)) {
            hasRemovedElement = true;
        } else {
            super.startElement(string, tagName, name, attributes);
        }

        parentTag = tagName;
        parentClz = attributes.getValue("class");
        parentStyle = attributes.getValue("style");

        if (tagName.equalsIgnoreCase("span")) {
            spanTagsStack.push(hasRemovedElement);
        }
    }

    @Override
    public void endElement(String string, String tagName, String name) throws SAXException {
        boolean hasRemovedElement = false;

        if (tagName.equalsIgnoreCase("span")) {
            hasRemovedElement = spanTagsStack.pop();
        }

        if(!hasRemovedElement) {
            super.endElement(string, tagName, name);
        }
    }

    private boolean isSameAsParent(String tagName, Attributes attributes) {
        if (!tagName.equalsIgnoreCase(parentTag)) {
            return false;
        }

        String clz = attributes.getValue("class");
        if ((clz == null && parentClz != null) || clz != null && !clz.equalsIgnoreCase(parentClz)) {
            return false;
        }

        String style = attributes.getValue("style");
        if ((style == null && parentStyle != null) || style != null && !style.equalsIgnoreCase(parentStyle)) {
            return false;
        }

        return true;
    }

    /*

      <p>
      </p>

      <span>
      <span> - remove
      <p>
      <span> - not remove
      </span> - note remove
      </p>
      </span> - remove
      </span>




     */
}


