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

import java.util.Arrays;
import java.util.Stack;

import static java.util.Objects.nonNull;

public class RemoveNestedSpanTagsFilter extends XMLFilterImpl {

    private Stack<Element> elements = new Stack<Element>();

    @Override
    public void startElement(String uri, String localName, String qualifiedName, Attributes attributes) throws SAXException {
        Element startElement = new Element(uri, localName, qualifiedName, attributes);
        Element parentElement = elements.isEmpty() ? null : elements.peek();
        elements.push(startElement);
        if ("span".equalsIgnoreCase(startElement.getLocalName())) {
            if (nonNull(parentElement) && "span".equalsIgnoreCase(parentElement.getLocalName())) {
                if (    parentElement.getClassAttribute().equals(startElement.getClassAttribute()) &&
                        parentElement.getStyleAttribute().equals(startElement.getStyleAttribute())) {
                    startElement.setIgonore(true);
                }
            }
            if (!startElement.isIgonore()) {
                startElement(startElement);
            }
        } else {
            startElement(startElement);
        }
    }

    private void startElement(Element startElement) throws SAXException {
        super.startElement(startElement.getUri(), startElement.getLocalName(), startElement.getQualifiedName(), startElement.getAttributes());
    }

    @Override
    public void endElement(String uri, String localName, String qualifiedName) throws SAXException {
        Element endElement = new Element(uri, localName, qualifiedName);
        if (!elements.isEmpty()) {
            Element startElement = elements.pop();
            if ("span".equalsIgnoreCase(endElement.getLocalName())) {
                if (nonNull(startElement)) {
                    if (!startElement.isIgonore()) {
                        endElement(endElement);
                    }
                } else {
                    endElement(endElement);
                }

            } else {
                endElement(endElement);
            }
        }

    }

    private void endElement(Element endElement) throws SAXException {
        super.endElement(endElement.getUri(), endElement.getLocalName(), endElement.getQualifiedName());
    }

    private String join(String[] classes, String separator) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int index = 0; index < classes.length; index++) {
            if (index > 0) {
                stringBuffer.append(separator);
            }
            stringBuffer.append(classes[index]);
        }
        return stringBuffer.toString();
    }

    private class Element {

        private String uri;
        private String localName;
        private String qualifiedName;
        private Attributes attributes;
        private String classAttribute = "";
        private String styleAttribute = "";
        private boolean igonore = false;

        public Element(String uri, String localName, String qualifiedName) {
            this(uri, localName, qualifiedName, null);
        }

        public Element(String uri, String localName, String qualifiedName, Attributes attributes) {
            this.uri = uri;
            this.localName = localName;
            this.qualifiedName = qualifiedName;
            this.attributes = attributes;
            if (nonNull(attributes)) {
                String value = attributes.getValue("class");
                if (nonNull(value)) {
                    value = value.replace("\\s+", " ");
                    String[] split = value.split(" ");
                    Arrays.sort(split);
                    classAttribute = join(split, " ");
                }
                value = attributes.getValue("style");
                if (nonNull(value)) {
                    value = value.replace("\\s+", " ");
                    String[] split = value.split(";");
                    Arrays.sort(split);
                    styleAttribute = join(split, ";");
                }
            }
        }

        public String getUri() {
            return uri;
        }

        public String getLocalName() {
            return localName;
        }

        public String getQualifiedName() {
            return qualifiedName;
        }

        public Attributes getAttributes() {
            return attributes;
        }

        public String getClassAttribute() {
            return classAttribute;
        }

        public String getStyleAttribute() {
            return styleAttribute;
        }

        public boolean isIgonore() {
            return igonore;
        }

        public void setIgonore(boolean igonore) {
            this.igonore = igonore;
        }
    }
}


