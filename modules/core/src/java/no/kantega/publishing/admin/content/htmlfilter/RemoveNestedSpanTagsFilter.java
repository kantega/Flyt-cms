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

import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.join;

/**
 * Filter that removes nested spans.
 *
 *  <code><p>
 </p>

 <span>
 <span> - remove
 <p>
 <span> - not remove
 </span> - not remove
 </p>
 </span> - remove
 </span>
 </code>
 */
public class RemoveNestedSpanTagsFilter implements Filter {

    @Override
    public Document runFilter(Document document) {
        final Document clean = Document.createShell(document.baseUri());
        if (document.body() != null) // frameset documents won't have a body. the clean doc will have empty body.
            copySafeNodes(document.body(), clean.body());

        return clean;
    }

    /**
     Iterates the input and copies trusted nodes (tags, attributes, text) into the destination.
     */
    private static final class CleaningVisitor implements NodeVisitor {
        private Element destination; // current element to append nodes to

        private Stack<NodeWrapper> elements = new Stack<>();

        private CleaningVisitor(Element destination) {
            this.destination = destination;
        }

        public void head(Node source, int depth) {
            NodeWrapper node = new NodeWrapper(source);
            NodeWrapper parentNode = elements.isEmpty() ? null : elements.peek();
            elements.push(node);

            if (source instanceof Element) {
                Element sourceEl = (Element) source;

                String tagName = sourceEl.tagName();
                if (tagName.equals("body")) {
                    return;
                }

                if(shouldKeepChild(node, parentNode)) {
                    Element destChild = createSafeElement(sourceEl);
                    destination.appendChild(destChild);
                    destination = destChild;
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

        private boolean shouldKeepChild(NodeWrapper node, NodeWrapper parentNode) {
            if (node.isSpan()) {
                if (nonNull(parentNode) && parentNode.isSpan()) {
                    boolean ignore = sameClassAndStyle(node, parentNode);
                    node.setIgnore(ignore);
                }
                return node.notIgnored();

            }
            return true;
        }

        private boolean sameClassAndStyle(NodeWrapper node, NodeWrapper parentNode) {
            return parentNode.classAttribute.equals(node.classAttribute) &&
                    parentNode.styleAttribute.equals(node.styleAttribute);
        }

        public void tail(Node source, int depth) {
            NodeWrapper pop = elements.pop();
            if (source instanceof Element) {

                if(pop.notIgnored()) {
                    destination = destination.parent(); // would have descended, so pop destination stack
                }
            }
        }
    }

    private void copySafeNodes(Element source, Element dest) {
        CleaningVisitor cleaningVisitor = new CleaningVisitor(dest);
        NodeTraversor traversor = new NodeTraversor(cleaningVisitor);
        traversor.traverse(source);
    }

    private static Element createSafeElement(Element sourceEl) {
        String sourceTag = sourceEl.tagName();
        org.jsoup.nodes.Attributes destAttrs = new org.jsoup.nodes.Attributes();
        Element dest = new Element(Tag.valueOf(sourceTag), sourceEl.baseUri(), destAttrs);

        org.jsoup.nodes.Attributes sourceAttrs = sourceEl.attributes();
        for (Attribute sourceAttr : sourceAttrs) {
                destAttrs.put(sourceAttr);
        }

        return dest;
    }

    private static class NodeWrapper {
        private final Node wrapped;
        private String styleAttribute = "";
        private String classAttribute = "";
        private boolean ignore = false;

        private static final Pattern whitespace = Pattern.compile("\\s+");

        private NodeWrapper(Node wrapped) {
            this.wrapped = wrapped;
            Attributes attributes = wrapped.attributes();
            if (nonNull(attributes)) {
                String value = attributes.get("class");
                if (nonNull(value)) {
                    value = whitespace.matcher(value).replaceAll(" ");
                    String[] split = value.split(" ");
                    Arrays.sort(split);
                    classAttribute = join(split, " ");
                }
                value = attributes.get("style");
                if (nonNull(value)) {
                    value = whitespace.matcher(value).replaceAll(" ");
                    String[] split = value.split(";");
                    Arrays.sort(split);
                    styleAttribute = join(split, ";");
                }
            }
        }

        public void setIgnore(boolean ignore) {
            this.ignore = ignore;
        }


        public boolean isSpan() {
            String tagname = wrapped instanceof Element ? ((Element) wrapped).tagName() : "?";
            return "span".equalsIgnoreCase(tagname);
        }

        public boolean notIgnored() {
            return !ignore;
        }
    }
}


