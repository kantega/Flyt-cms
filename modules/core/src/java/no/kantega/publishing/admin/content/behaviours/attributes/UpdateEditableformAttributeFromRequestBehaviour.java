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

package no.kantega.publishing.admin.content.behaviours.attributes;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.log.Log;
import no.kantega.commons.util.StringHelper;
import no.kantega.commons.xmlfilter.FilterPipeline;
import no.kantega.publishing.admin.content.htmlfilter.CleanupFormHtmlFilter;
import no.kantega.publishing.admin.content.util.AttributeHelper;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.EditableformAttribute;

import java.io.StringReader;
import java.io.StringWriter;

/**
 * Date: Apr 14, 2010
 * Time: 11:53:33 AM
 */
public class UpdateEditableformAttributeFromRequestBehaviour implements UpdateAttributeFromRequestBehaviour {

    private static String BODY_START = "<BODY>";
    private static String BODY_END   = "</BODY>";


    @Override
    public void updateAttribute(RequestParameters param, Content content, Attribute attribute) {
        String inputField = AttributeHelper.getInputFieldName(attribute.getName());

        String value = param.getString(inputField);
        if (value == null) {
            value = "";
        } else {
            value = applyPostEditFilters(value);
        }

        attribute.setValue(value);
    }

    private String applyPostEditFilters(String value) {
        FilterPipeline pipe = new FilterPipeline();

        // Cleanup HTML
        pipe.addFilter(new CleanupFormHtmlFilter());

        String origVal = value;

        try {
            // Filter expects complete document
            value = "<html><body>" + value + "</body></html>";

            StringWriter sw = new StringWriter();
            pipe.filter(new StringReader(value), sw);
            value = sw.getBuffer().toString();

            int start = value.indexOf(BODY_START.toLowerCase());
            if (start == -1) {
                start = value.indexOf(BODY_START.toUpperCase());
            }

            int end = value.indexOf(BODY_END.toLowerCase());
            if (end == -1) {
                end = value.indexOf(BODY_END.toUpperCase());
            }

            value = value.substring(start + BODY_START.length(), end);
        } catch (Exception e) {
            value = origVal;
            Log.error("", e, null, null);
        }

        // Some versions of Xerces creates XHTML tags
        value = StringHelper.replace(value, "</BR>", "");
        
        return value;
    }

}
