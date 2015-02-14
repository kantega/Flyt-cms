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

import no.kantega.commons.xmlfilter.Filter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * This filter class converts valid html code for underline elements (<span style="text-decoration:underline;">)
 * to the editor specific <u> tag.
 */
public class ConvertUnderlineToEditorStyleFilter implements Filter {

    @Override
    public Document runFilter(Document document) {
        for (Element span : document.getElementsByTag("span")) {
            String style = span.attr("style");
            if (isNotBlank(style)) {
                String textDecoration = getSubAttributeValue(style, "text-decoration");
                if ("underline".equalsIgnoreCase(textDecoration)) {
                    span.removeAttr("style");
                    span.tagName("u");
                }
            }
        }
        return document;
    }

    private String getSubAttributeValue(String attr, String subattr) {
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