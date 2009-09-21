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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;
import no.kantega.publishing.admin.content.util.HTMLEditorHelper;
import no.kantega.publishing.admin.content.htmlfilter.util.HtmlFilterHelper;

/**
 * The filter {@code AmpersandFilter} is used to replace "&" with "&amp;" in URL's (unless it has already been done).
 *
 * @jogri
 * @see HTMLEditorHelper#postEditFilter(String)
 */
public class AmpersandFilter extends XMLFilterImpl {

    /**
     * This method replaces "&" with "&amp;" in "href" and "src" attribute values. If the value already contains "&amp;"
     * entities, it remains unchanged.
     *
     * @param string
     * @param localName
     * @param name
     * @param attributes The SAX attributes in which the "href" or "src" attribute value can be found.
     * @throws SAXException
     */
    public void startElement(String string, String localName, String name, Attributes attributes) throws SAXException {
        String href = attributes.getValue("href");
        if (href != null && href.indexOf("&") != -1) {
            href = href.replaceAll("&amp;", "&");   // To avoid getting "&amp;amp;" when performing the replacements in the next line.
            href = href.replaceAll("&", "&amp;");
            attributes = HtmlFilterHelper.setAttribute("href", href, attributes);
        }
        String src = attributes.getValue("src");
        if (src != null && src.indexOf("&") != -1) {
            src = src.replaceAll("&amp;", "&");   // To avoid getting "&amp;amp;" when performing the replacements in the next line.
            src = src.replaceAll("&", "&amp;");
            attributes = HtmlFilterHelper.setAttribute("src", src, attributes);
        }
        super.startElement(string, localName, name, attributes);
    }
}