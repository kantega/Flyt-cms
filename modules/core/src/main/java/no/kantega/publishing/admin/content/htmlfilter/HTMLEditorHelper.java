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
import no.kantega.commons.xmlfilter.FilterPipeline;
import no.kantega.publishing.common.Aksess;
import org.apache.tools.ant.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class HTMLEditorHelper {
    private static final Logger log = LoggerFactory.getLogger(HTMLEditorHelper.class);
    private static final Pattern emptyTagsPattern = Pattern.compile("<(i|I|b|B|em|EM|b|B|span|SPAN)>(\\s|&nbsp;)*</\\1>");

    /**
     * Cleanup / replacement done after editing content
     * @param value - HTML text
     * @return - cleaned HTML
     */
    public static String postEditFilter(String value) {
        FilterPipeline pipe = new FilterPipeline();

        value = value.trim();

        // If value is just a <br> or <p>
        if(value.equalsIgnoreCase("<br>") || value.equalsIgnoreCase("<br/>") || value.equalsIgnoreCase("<br />")) {
            value = "";
        }

        if(value.equalsIgnoreCase("&nbsp;") || value.equalsIgnoreCase("<p>&nbsp;</p>")) {
            value = "";
        }

        // Replace html tags the editor generates with more semantic and valid tags.
        // These tags will be converted back again when editing a page.
        value = StringUtils.replace(value,"<b>", "<strong>");
        value = StringUtils.replace(value,"</b>", "</strong>");
        value = StringUtils.replace(value,"<B>", "<strong>");
        value = StringUtils.replace(value,"</B>", "</strong>");
        value = StringUtils.replace(value,"<i>", "<em>");
        value = StringUtils.replace(value,"</i>", "</em>");
        value = StringUtils.replace(value,"<I>", "<em>");
        value = StringUtils.replace(value,"</I>", "</em>");
        value = StringUtils.replace(value,"<u>", "<span style=\"text-decoration: underline;\">");
        value = StringUtils.replace(value,"</u>", "</span>");
        value = StringUtils.replace(value,"<U>", "<span style=\"text-decoration: underline;\">");
        value = StringUtils.replace(value,"</U>", "</span>");

        // Replace the align attribute from p elements with inline style
        pipe.addFilter(new ReplaceAlignAttributeFilter());

        // Fix image width and height, shrink images automatically
        pipe.addFilter(new ImgHeightAndWidthFilter());

        // Remove nested span tags, typically caused by TinyMCE
        pipe.addFilter(new RemoveNestedSpanTagsFilter());


        // Replace context path with <@WEB@>
        ContextPathFilter contextPathFilter = new ContextPathFilter();
        contextPathFilter.setContextPath(Aksess.getContextPath());
        contextPathFilter.setRootUrlToken(Aksess.VAR_WEB);
        pipe.addFilter(contextPathFilter);

        String origVal = value;

        try {
            // Filter expects complete document
            value = "<html><body>" + value + "</body></html>";

            value = pipe.filter(value);
        } catch (Exception e) {
            value = origVal;
            log.error("", e);
        }

        // Remove empty B, SPAN etc tags
        value = emptyTagsPattern.matcher(value).replaceAll("");

        // Some versions of Xerces creates XHTML tags
        value = StringUtils.replace(value, "</HR>", "");
        value = StringUtils.replace(value, "</BR>", "");
        value = StringUtils.replace(value, "</IMG>", "");
        value = StringUtils.replace(value, "&lt;@WEB@&gt;", "<@WEB@>");

        return value;
    }


    /**
     * Replacements done before editing content
     * @param value - HTML text
     * @param contextPath - context path
     * @return - cleaned HTML
     */
    public static String preEditFilter(String value, String contextPath) {

        value = StringHelper.replace(value, "\"" + Aksess.VAR_WEB + "\"/", contextPath);
        value = StringHelper.replace(value, Aksess.VAR_WEB + "/", contextPath);

        // Convert strong and em tags back to b and i tags to enable edit of text with these tags.
        value = StringUtils.replace(value,"<strong>", "<b>");
        value = StringUtils.replace(value,"</strong>", "</b>");
        value = StringUtils.replace(value,"<em>", "<i>");
        value = StringUtils.replace(value,"</em>", "</i>");

        FilterPipeline pipe = new FilterPipeline();

        // Replace inline style text alignment with align attribute.
        pipe.addFilter(new ReplaceStyleAlignWithAttributeAlignFilter());

        // Convert inline style for underlining text with the <u> tag.
        pipe.addFilter(new ConvertUnderlineToEditorStyleFilter());

        String origVal = value;

        try {
            // Filter expects complete document
            value = "<html><body>" + value + "</body></html>";


            value = pipe.filter(value);

        } catch (Exception e) {
            value = origVal;
            log.error("", e);
        }

        // Some versions of Xerces creates XHTML tags
        value = StringUtils.replace(value, "</HR>", "");
        value = StringUtils.replace(value, "</BR>", "");
        value = StringUtils.replace(value, "</IMG>", "");

        // Tinymce decodes entities, so if there are code fragments, like &lt;/title&gt;, these was decoded and removed.
        value = StringUtils.replace(value, "&lt;", "&amp;lt;");
        value = StringUtils.replace(value, "&gt;", "&amp;gt;");

        return value;
    }
}
