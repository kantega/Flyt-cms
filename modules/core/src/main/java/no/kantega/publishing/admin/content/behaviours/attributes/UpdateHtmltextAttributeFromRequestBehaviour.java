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
import no.kantega.commons.util.StringHelper;
import no.kantega.commons.util.URLHelper;
import no.kantega.publishing.admin.content.htmlfilter.HTMLEditorHelper;
import no.kantega.publishing.admin.content.util.AttributeHelper;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;

import javax.servlet.http.HttpServletRequest;

public class UpdateHtmltextAttributeFromRequestBehaviour implements UpdateAttributeFromRequestBehaviour {
    public void updateAttribute(RequestParameters param, Content content, Attribute attribute) {
        HttpServletRequest request = param.getRequest();
        String rootUrl = URLHelper.getRootURL(request);

        String inputField = AttributeHelper.getInputFieldName(attribute.getNameIncludingPath());

        String value = param.getString(inputField);
        if (value == null) {
            value = "";
        } else {
            value = HTMLEditorHelper.postEditFilter(value);
            value = StringHelper.replace(value, rootUrl, Aksess.VAR_WEB + "/");
        }

        attribute.setValue(value);
    }
}
