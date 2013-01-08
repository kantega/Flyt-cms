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
    private Stack<Tag> previousTagStack = new Stack<Tag>();

    @Override
    public void startElement(String string, String tagName, String name, Attributes attributes) throws SAXException {
        boolean hasRemovedElement = false;

        Tag tag = new Tag();
        tag.setTagName(tagName);
        tag.setClz(attributes.getValue("class"));
        tag.setStyle(attributes.getValue("style"));

        if (tagName.equalsIgnoreCase("span") && isSameAsPreviousTag(tag)) {
            tag.setShouldBeRemoved(true);
        } else {
            tag.setShouldBeRemoved(false);
            super.startElement(string, tagName, name, attributes);
        }

        previousTagStack.push(tag);
    }

    @Override
    public void endElement(String string, String tagName, String name) throws SAXException {
        if (previousTagStack.empty() || !previousTagStack.pop().shouldBeRemoved()) {
            super.endElement(string, tagName, name);
        }
    }

    private boolean isSameAsPreviousTag(Tag currentTag) {
        if (previousTagStack.empty()) {
            return false;
        }

        return currentTag.equals(previousTagStack.peek());
    }

    private class Tag {
        String tagName;
        String clz;
        String style;
        boolean shouldBeRemoved;

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

        public void setClz(String clz) {
            this.clz = clz;
        }

        public void setStyle(String style) {
            this.style = style;
        }

        public boolean shouldBeRemoved() {
            return shouldBeRemoved;
        }

        public void setShouldBeRemoved(boolean shouldBeRemoved) {
            this.shouldBeRemoved = shouldBeRemoved;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Tag tag = (Tag) o;

            if (clz != null ? !clz.equals(tag.clz) : tag.clz != null) return false;
            if (style != null ? !style.equals(tag.style) : tag.style != null) return false;
            if (tagName != null ? !tagName.equals(tag.tagName) : tag.tagName != null) return false;

            return true;
        }
    }
}