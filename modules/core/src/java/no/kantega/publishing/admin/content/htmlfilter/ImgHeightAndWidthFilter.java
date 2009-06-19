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

/**
 * User: Anders Skar, Kantega AS
 * Date: Apr 28, 2009
 * Time: 1:26:12 PM
 */
public class ImgHeightAndWidthFilter extends XMLFilterImpl {
    
    @Override
    public void startElement(String string, String localName, String name, Attributes attributes) throws SAXException {
        if(localName.equalsIgnoreCase("img")) {
            // Remove style and replace with width= and height=
            String style = attributes.getValue("style");
            if (style != null) {
                // Replace style="width: xx" with width
                String width = HtmlFilterHelper.getSubAttributeValue(style, "width");
                if (width != null) {
                    width = width.replaceAll("px", "");
                    attributes = HtmlFilterHelper.setAttribute("width", width, attributes);
                }

                // Replace style="height: xx" with height
                String height = HtmlFilterHelper.getSubAttributeValue(style, "height");
                if (height != null) {
                    height = height.replaceAll("px", "");
                    attributes = HtmlFilterHelper.setAttribute("height", height, attributes);
                }

                attributes = HtmlFilterHelper.removeAttribute("style", attributes);
            }

        }
        super.startElement(string, localName, name, attributes);
    }

    @Override
    public void endElement(String string, String localname, String name) throws SAXException {
        super.endElement(string, localname, name);
    }
}


