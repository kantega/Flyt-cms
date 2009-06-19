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

public class PlaceHolder2ANameFilter extends XMLFilterImpl {

    boolean isAnchor = false;

    public void startElement(String string, String localName, String string2, Attributes attributes) throws SAXException {
        if(localName.equalsIgnoreCase("img")) {
            AttributesImpl attr = new AttributesImpl();
            String attrSrc  = attributes.getValue("src");
            String attrValue = attributes.getValue("name");
            if (attrValue == null) {
                attrValue = attributes.getValue("id");
            }

            if ((attrSrc != null) && (attrValue != null) && (attrSrc.indexOf("placeholder/anchor.gif") != -1)) {
                attr.addAttribute("", "id", "id", "CDATA", attrValue);
                attr.addAttribute("", "name", "name", "CDATA", attrValue);
                super.startElement("", "a", "a", attr);
                super.endElement("", "a", "a");
                isAnchor = true;
            }
        }

        if (!isAnchor) {
            super.startElement(string, localName, string2, attributes);
        }
    }

    public void endElement(String string, String localname, String string2) throws SAXException {
        if(localname.equalsIgnoreCase("img") && isAnchor) {
            isAnchor = false;
        } else {
            super.endElement(string, localname, string2);
        }
    }
}