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

import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import no.kantega.publishing.common.util.PrettyURLEncoder;
import no.kantega.publishing.admin.content.htmlfilter.util.HtmlFilterHelper;
import no.kantega.commons.util.StringHelper;

/**
 * The A element: http://www.w3.org/TR/html401/struct/links.html#h-12.2
 * <p>
 * http://www.w3.org/TR/html401/types.html#h-6.2:<br>
 * "ID and NAME tokens must begin with a letter ([A-Za-z]) and may be followed by any number of letters, digits ([0-9]),
 * hyphens ("-"), underscores ("_"), colons (":"), and periods (".")."
 *
 * @author andska, jogri
 * @see HTMLEditorHelper#postEditFilter(String)
 */

public class IdAndNameFilter extends XMLFilterImpl {

    boolean isAnchor = false;

    public void startElement(String string, String localName, String string2, Attributes attributes) throws SAXException {
        if(localName.equalsIgnoreCase("a") || localName.equalsIgnoreCase("img")) {
            String id = attributes.getValue("id");
            if (id != null) {
                id = validateAndCorrectAttributeValue(id);
                attributes = HtmlFilterHelper.setAttribute("id", id, attributes);
            }

            String name = attributes.getValue("name");
            if (name != null) {
                name = validateAndCorrectAttributeValue(name);
                attributes = HtmlFilterHelper.setAttribute("name", name, attributes);
            }

            String href = attributes.getValue("href");
            if (href != null && href.startsWith("#")) {
                href = href.substring(1, href.length());
                href = validateAndCorrectAttributeValue(href);
                attributes = HtmlFilterHelper.setAttribute("href", "#" + href, attributes);
            }

        }

        super.startElement(string, localName, string2, attributes);
    }

    public void endElement(String string, String localname, String string2) throws SAXException {
        super.endElement(string, localname, string2);
    }

    /**
     * This method replaces invalid characters in "id" and "name" attribute values according to W3C rules.
     * <p>
     * This is basically the same code as in the rtInsertAnchor() JavaScript function in webapp/admin/js/richtext.jsp
     *
     * @param attributeValue The original attribute value
     * @return A valid attribute value
     */
    private String validateAndCorrectAttributeValue(String attributeValue) {
        attributeValue = attributeValue.trim();
        attributeValue = attributeValue.replaceAll(" ", "_");

        attributeValue = PrettyURLEncoder.encode(attributeValue);

        // "id" and "name" attributes must begin with a letter
        String firstCharacter = attributeValue.substring(0, 1);
        if (!firstCharacter.matches("[A-Za-z]")) {
            attributeValue = "b_" + attributeValue;
        }

        for (int i = 0; i < attributeValue.length(); i++) {
            // Replace illegal characters with hyphens ("-"). http://www.w3.org/TR/html401/types.html#h-6.2
            String character = attributeValue.substring(i, i + 1);
            if (!character.matches("[A-Za-z0-9\\-_:.]")) {
                attributeValue = StringHelper.replace(attributeValue, character, "-");
            }
        }

        return attributeValue;
    }
}
