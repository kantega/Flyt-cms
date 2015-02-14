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

import no.kantega.commons.xmlfilter.Filter;
import org.jsoup.nodes.*;
import org.jsoup.parser.Tag;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

import java.util.Stack;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class RemoveNestedSpanTagsFilter implements Filter {

    @Override
    public Document runFilter(Document document) {
        final Document clean = Document.createShell(document.baseUri());
        if (document.body() != null) // frameset documents won't have a body. the clean doc will have empty body.
            copySafeNodes(document.body(), clean.body());

        return clean;
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
    /**
     Iterates the input and copies trusted nodes (tags, attributes, text) into the destination.
     */
    private final class CleaningVisitor implements NodeVisitor {
        private Element destination; // current element to append nodes to

        private Stack<Boolean> spanTagsStack = new Stack<>();

        private String parentTag = null;
        private String parentStyle = null;
        private String parentClz = null;

        private CleaningVisitor(Element destination) {
            this.destination = destination;
        }

        public void head(Node source, int depth) {
            if (source instanceof Element) {
                Element sourceEl = (Element) source;

                String tagName = sourceEl.tagName();
                if(tagName.equals("body")){
                    return;
                }
                org.jsoup.nodes.Attributes attributes = source.attributes();
                boolean hasRemovedElement = tagName.equalsIgnoreCase("span") && isSameAsParent(tagName, attributes);
                if (!hasRemovedElement) { // safe, clone and copy safe attrs
                    Element destChild = createSafeElement(sourceEl);
                    destination.appendChild(destChild);

                    destination = destChild;
                }
                parentTag = tagName;
                parentClz = attributes.get("class");
                parentStyle = attributes.get("style");

                if (tagName.equalsIgnoreCase("span")) {
                    spanTagsStack.push(hasRemovedElement);
                }
            } else if (source instanceof TextNode) {
                TextNode sourceText = (TextNode) source;
                TextNode destText = new TextNode(sourceText.getWholeText(), source.baseUri());
                destination.appendChild(destText);
            } else if (source instanceof DataNode) {
                DataNode sourceData = (DataNode) source;
                DataNode destData = new DataNode(sourceData.getWholeData(), source.baseUri());
                destination.appendChild(destData);
            }
        }

        public void tail(Node source, int depth) {
            if (source instanceof Element) {
                Element sourceEl = (Element) source;
                boolean hasRemovedElement = false;

                if (sourceEl.tagName().equalsIgnoreCase("span")) {
                    hasRemovedElement = spanTagsStack.pop();
                }

                if(!hasRemovedElement) {
                    destination = destination.parent(); // would have descended, so pop destination stack
                }
            }
        }

        private boolean isSameAsParent(String tagName, Attributes attributes) {
            if (!tagName.equalsIgnoreCase(parentTag)) {
                return false;
            }

            String clz = attributes.get("class");
            if ((isBlank(clz) && isNotBlank(parentClz)) || clz != null && !clz.equalsIgnoreCase(parentClz)) {
                return false;
            }

            String style = attributes.get("style");
            if ((isBlank(style) && isNotBlank(parentStyle)) || style != null && !style.equalsIgnoreCase(parentStyle)) {
                return false;
            }

            return true;
        }
    }

    private void copySafeNodes(Element source, Element dest) {
        CleaningVisitor cleaningVisitor = new CleaningVisitor(dest);
        NodeTraversor traversor = new NodeTraversor(cleaningVisitor);
        traversor.traverse(source);
    }

    private Element createSafeElement(Element sourceEl) {
        String sourceTag = sourceEl.tagName();
        org.jsoup.nodes.Attributes destAttrs = new org.jsoup.nodes.Attributes();
        Element dest = new Element(Tag.valueOf(sourceTag), sourceEl.baseUri(), destAttrs);

        org.jsoup.nodes.Attributes sourceAttrs = sourceEl.attributes();
        for (Attribute sourceAttr : sourceAttrs) {
                destAttrs.put(sourceAttr);
        }

        return dest;
    }
}


