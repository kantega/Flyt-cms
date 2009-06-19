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
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class AName2PlaceHolderFilter extends XMLFilterImpl {
    boolean isAnchor = false;

    @Override
    public void startElement(String string, String localName, String name, Attributes attributes) throws SAXException {
        String attrHref = attributes.getValue("href");
        String attrName = attributes.getValue("name");
        if (attrName == null) {
            attrName = attributes.getValue("id");
        }

        if(name.equalsIgnoreCase("a") && attrHref == null) {
            if (attrName != null) {
            	attrName = attrName.replace(" ", "_");   // To achieve valid W3C markup
            	
                AttributesImpl attr = new AttributesImpl();
                attr.addAttribute("", "id", "id", "CDATA", attrName);
                attr.addAttribute("", "name", "name", "CDATA", attrName);
                attr.addAttribute("", "src", "src", "CDATA", "../bitmaps/common/placeholder/anchor.gif");
                super.startElement("", "img", "img", attr);
                super.endElement("", "img", "img");
                isAnchor = true;
            }
        }

        if (!isAnchor) {
            super.startElement(string,  localName, name, attributes);
        }
    }

    @Override
    public void endElement(String string, String localname, String name) throws SAXException {
        if(name.equalsIgnoreCase("a") && isAnchor) {
            isAnchor = false;
        } else {
            super.endElement(string, localname, name);
        }
    }
    
}
