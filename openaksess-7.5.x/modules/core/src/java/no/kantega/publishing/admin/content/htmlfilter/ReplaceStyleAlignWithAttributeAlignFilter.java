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
 * The editor aligns text by using the align attribute.
 * This class will replace inline style attribute on text blocks with the align attribute.
 *
 */
public class ReplaceStyleAlignWithAttributeAlignFilter extends XMLFilterImpl {

    public void startElement(String string, String localName, String name, Attributes attributes) throws SAXException {

        if(name.equalsIgnoreCase("p") || name.equalsIgnoreCase("div")) {            
            String style = attributes.getValue("style");
            if (style != null) {
                if(style.indexOf("right") != -1){
                    attributes = HtmlFilterHelper.setAttribute("align", "right", attributes);
                }else if(style.indexOf("left") != -1){
                    attributes = HtmlFilterHelper.setAttribute("align", "left", attributes);
                }else if(style.indexOf("center") != -1){
                    attributes = HtmlFilterHelper.setAttribute("align", "center", attributes);
                }
                attributes = HtmlFilterHelper.removeAttribute("style", attributes);
            }
        }

        super.startElement(string,  localName, name, attributes);
    }
}