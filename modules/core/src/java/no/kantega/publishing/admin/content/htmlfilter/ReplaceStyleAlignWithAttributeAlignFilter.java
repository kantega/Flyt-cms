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

import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 *
 * The editor aligns text by using the align attribute.
 * This class will replace inline style attribute on text blocks with the align attribute.
 *
 */
public class ReplaceStyleAlignWithAttributeAlignFilter implements Filter {

    private final List<String> tags = asList("p", "div");

    @Override
    public Document runFilter(Document document) {
        for (String tag : tags) {
            for (Element element : document.getElementsByTag(tag)) {
                String style = element.attr("style");
                if (isNotBlank(style)) {
                    if(style.contains("right")){
                        element.attr("align", "right");
                    }else if(style.contains("left")){
                        element.attr("align", "left");
                    }else if(style.contains("center")){
                        element.attr("align", "center");
                    }
                    element.removeAttr("style");
                }
            }
        }
        return document;
    }
}