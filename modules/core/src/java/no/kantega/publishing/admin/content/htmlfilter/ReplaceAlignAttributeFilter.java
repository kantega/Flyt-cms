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
import org.jsoup.select.Elements;

/**
 *
 * The editor will put the align attribute on p and div elements when aligning text.
 *
 * This class will replace this invalid attribute to a valid inline style.
 *
 */
public class ReplaceAlignAttributeFilter implements Filter {

    @Override
    public Document runFilter(Document document) {
        Elements elementsWithAlign = document.getElementsByAttribute("align");
        for (Element element : elementsWithAlign) {
            String align = element.attr("align");
            if ("right".equalsIgnoreCase(align)) {
                element.attr("style", "text-align: right;");
            } else if ("left".equalsIgnoreCase(align)) {
                element.attr("style", "text-align: left;");
            } else if ("center".equalsIgnoreCase(align)) {
                element.attr("style", "text-align: center;");
            }
            element.removeAttr("align");
        }
        return document;
    }
}