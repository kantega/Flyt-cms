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
 *
 * The editor will put the align attribute on p and div elements when aligning text.
 *
 * This class will replace this invalid attribute to a valid inline style.
 *
 */
public class ReplaceAlignAttributeFilter extends XMLFilterImpl {

    /**
     * Replaces the align attribute on p and div elements with inline style attribute.
     * @param string
     * @param localName
     * @param name
     * @param attributes
     * @throws SAXException
     */
    public void startElement(String string, String localName, String name, Attributes attributes) throws SAXException {

        if(name.equalsIgnoreCase("p") || name.equalsIgnoreCase("div")) {
            String align = attributes.getValue("align");
            if (align != null) {
                if("right".equalsIgnoreCase(align)){
                    attributes = HtmlFilterHelper.setAttribute("style", "text-align: right;", attributes);
                } else if("left".equalsIgnoreCase(align)){
                    attributes = HtmlFilterHelper.setAttribute("style", "text-align: left;", attributes);
                } else if("center".equalsIgnoreCase(align)){
                    attributes = HtmlFilterHelper.setAttribute("style", "text-align: center;", attributes);
                }
                attributes = HtmlFilterHelper.removeAttribute("align", attributes);
            }
        }

        super.startElement(string,  localName, name, attributes);
    }
}