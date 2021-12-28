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

import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * The filter ContextPathFilter replaces the contextpath in links and images with the text "<@WEB@>"
 * to prevent the context path being saved in text, causing problems when moving a site from a contextpath to another
 */
public class ContextPathFilter implements Filter {
    private final List<String> attributes = asList("href", "src", "movie");
    private String contextPath = "";
    private String rootUrlToken = "<@WEB@>";

    @Override
    public Document runFilter(Document document) {
        for (String attribute : attributes) {
            Elements withHref = document.getElementsByAttribute(attribute);
            fixContextPathForAttribute(withHref, attribute);
        }
        return document;
    }

    private void fixContextPathForAttribute(Elements elements, String attribute) {
        for (Element element : elements) {
            String attributeValue = element.attr(attribute);
            if (isNotBlank(attributeValue)) {
                if (attributeValue.startsWith("../")) {
                    attributeValue = rootUrlToken + "/" + attributeValue.substring(attributeValue.lastIndexOf("../") + 3, attributeValue.length());
                    element.attr(attribute, attributeValue);
                }

                if (contextPath.length() > 0) {
                    if (attributeValue.startsWith(contextPath + "/")) {
                        attributeValue = rootUrlToken + attributeValue.substring(contextPath.length(), attributeValue.length());
                        element.attr(attribute, attributeValue);
                    }
                }
            }
        }
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public void setRootUrlToken(String rootUrlToken) {
        this.rootUrlToken = rootUrlToken;
    }


}