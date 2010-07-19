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
import no.kantega.publishing.admin.content.htmlfilter.util.HtmlFilterHelper;

/**
 * This filter class converts valid html code for underline elements (<span style="text-decoration:underline;">)
 * to the editor specific <u> tag.
 */
public class ConvertUnderlineToEditorStyleFilter extends XMLFilterImpl {

    /**
     * Flag indicating if the current span end element should be converted. 
     */
    private boolean elementConverted = false;

    /**
     * Converts the valid tag (<span style="text-decoration: underline;">) to the editor specific <u> tag.
     * @param string
     * @param localName name of the tag.
     * @param name name of the tag.
     * @param attributes attributes of the current tag.
     * @throws SAXException thrown when an error occurs during the parsing.
     */
    public void startElement(String string, String localName, String name, Attributes attributes) throws SAXException {
        if(localName.equalsIgnoreCase("span")){
            String style = attributes.getValue("style");
            if(style != null){
                String textDecoration = HtmlFilterHelper.getSubAttributeValue(style, "text-decoration");
                if("underline".equalsIgnoreCase(textDecoration)){
                    attributes = HtmlFilterHelper.removeAttribute("style", attributes);
                    name = "u";
                    localName = "u";
                    elementConverted = true;
                }
            }
        }
        super.startElement(string, localName, name, attributes);
    }

    /**
     * Checks if the current element is a span tag that needs to be converted to </u>.
     * @param uri
     * @param localName
     * @param qName
     * @throws SAXException
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if(elementConverted){
            elementConverted = false;
            localName = "u";
            qName = "u";
        }
        super.endElement(uri, localName, qName);
    }

}