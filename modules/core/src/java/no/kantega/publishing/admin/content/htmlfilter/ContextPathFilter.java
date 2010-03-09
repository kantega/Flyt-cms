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

package no.kantega.publishing.admin.content.htmlfilter;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.XMLFilterImpl;
import no.kantega.publishing.admin.content.htmlfilter.util.HtmlFilterHelper;
import no.kantega.publishing.common.Aksess;

/**
 * The filter ContextPathFilter replaces the contextpath in links and images with the text "<@WEB@>"
 * to prevent the context path being saved in text, causing problems when moving a site from a contextpath to another
 */
public class ContextPathFilter extends XMLFilterImpl {
    private String contextPath = "/";
    private String rootUrlToken = "<@WEB@>";

    public void startElement(String string, String localName, String string2, Attributes attributes) throws SAXException {
        attributes = fixContextPathForAttribute(attributes, "href");
        attributes = fixContextPathForAttribute(attributes, "src");
        attributes = fixContextPathForAttribute(attributes, "movie");

        super.startElement(string, localName, string2, attributes);
    }

    private Attributes fixContextPathForAttribute(Attributes attributes, String name) {
        String attributeValue = attributes.getValue(name);
        if (attributeValue != null) {
            if (attributeValue.startsWith("../")) {
                attributeValue = rootUrlToken + "/" + attributeValue.substring(attributeValue.lastIndexOf("../") + 3, attributeValue.length());
                attributes = HtmlFilterHelper.setAttribute(name, attributeValue, attributes);
            }

            if (contextPath.length() > 0) {
                if (attributeValue.startsWith(contextPath + "/")) {
                    attributeValue = rootUrlToken + attributeValue.substring(contextPath.length(), attributeValue.length());
                    attributes = HtmlFilterHelper.setAttribute(name, attributeValue, attributes);
                }
            }
        }
        return attributes;
    }

    public void endElement(String string, String localname, String string2) throws SAXException {
        super.endElement(string, localname, string2);
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public void setRootUrlToken(String rootUrlToken) {
        this.rootUrlToken = rootUrlToken;
    }
}