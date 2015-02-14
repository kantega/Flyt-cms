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

import no.kantega.commons.util.StringHelper;
import no.kantega.commons.xmlfilter.Filter;
import no.kantega.publishing.common.util.PrettyURLEncoder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * The A element: http://www.w3.org/TR/html401/struct/links.html#h-12.2
 * <p>
 * http://www.w3.org/TR/html401/types.html#h-6.2:<br>
 * "ID and NAME tokens must begin with a letter ([A-Za-z]) and may be followed by any number of letters, digits ([0-9]),
 * hyphens ("-"), underscores ("_"), colons (":"), and periods (".")."
 *
 * @author andska, jogri
 * @see HTMLEditorHelper#postEditFilter(String)
 */

public class IdAndNameFilter implements Filter{

    @Override
    public Document runFilter(Document document) {
        List<String> tags = asList("a", "img");
        List<String> attributes = asList("id", "name");
        for (String tag : tags) {
            Elements elementsByTag = document.getElementsByTag(tag);
            for (Element element : elementsByTag) {
                for (String attribute : attributes) {
                    String value = element.attr(attribute);
                    if(isNotBlank(value)){
                        element.attr(attribute, validateAndCorrectAttributeValue(value));
                    }
                }

                String href = element.attr("href");
                if (href.startsWith("#")) {
                    href = href.substring(1, href.length());
                    href = validateAndCorrectAttributeValue(href);
                    element.attr("href", href);
                }
            }
        }
        return document;
    }


    /**
     * This method replaces invalid characters in "id" and "name" attribute values according to W3C rules.
     * <p>
     * This is basically the same code as in the rtInsertAnchor() JavaScript function in webapp/admin/js/richtext.jsp
     *
     * @param attributeValue The original attribute value
     * @return A valid attribute value
     */
    private final Pattern charPattern = Pattern.compile("[A-Za-z]");
    private final Pattern illegalPattern = Pattern.compile("[A-Za-z0-9\\-_:.]");

    private String validateAndCorrectAttributeValue(String attributeValue) {
        attributeValue = attributeValue.trim();
        attributeValue = attributeValue.replaceAll(" ", "_");

        attributeValue = PrettyURLEncoder.encode(attributeValue);

        // "id" and "name" attributes must begin with a letter
        String firstCharacter = attributeValue.substring(0, 1);
        if (!charPattern.matcher(firstCharacter).matches()) {
            attributeValue = "b_" + attributeValue;
        }

        for (int i = 0; i < attributeValue.length(); i++) {
            // Replace illegal characters with hyphens ("-"). http://www.w3.org/TR/html401/types.html#h-6.2
            String character = attributeValue.substring(i, i + 1);
            if (!illegalPattern.matcher(character).matches()) {
                attributeValue = StringHelper.replace(attributeValue, character, "-");
            }
        }

        return attributeValue;
    }


}
