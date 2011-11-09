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

package no.kantega.publishing.admin.content.htmlfilter.util;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class HtmlFilterHelper {
    public static Attributes removeAttribute(String name, Attributes attributes) {
        AttributesImpl attr = new AttributesImpl(attributes);
        int inx = attr.getIndex(name);
        if (inx != -1) {
            attr.removeAttribute(inx);
            attributes = attr;
        }
        return attributes;
    }

    public static Attributes setAttribute(String name, String value, Attributes attributes) {
        AttributesImpl attr = new AttributesImpl(attributes);
        int inx = attr.getIndex(name);
        if (inx != -1) {
            attr.setAttribute(inx, "", name, name, "CDATA", value);
        } else {
            attr.addAttribute("", name, name, "CDATA", value);
        }
        attributes = attr;
        return attributes;
    }

    public static String getSubAttributeValue(String attr, String subattr) {
        String subAttributeValue = null;

        attr = attr.toLowerCase();
        subattr = subattr.toLowerCase();

        int start = attr.indexOf(subattr);
        if (start != -1) {
            subAttributeValue = attr.substring(start + subattr.length() + 1, attr.length());
            subAttributeValue = subAttributeValue.trim();

            int end = subAttributeValue.indexOf(";");
            if (end != -1) {
                subAttributeValue = subAttributeValue.substring(0, end);
            }
        }

        return subAttributeValue;
    }
}
