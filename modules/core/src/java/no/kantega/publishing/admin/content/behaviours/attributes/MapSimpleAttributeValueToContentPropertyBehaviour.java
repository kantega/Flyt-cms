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
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.publishing.admin.content.util.AttributeHelper;
import no.kantega.publishing.admin.content.util.ContentAliasValidator;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.util.PrettyURLEncoder;

public class MapSimpleAttributeValueToContentPropertyBehaviour implements MapAttributeValueToContentPropertyBehaviour {
    public void mapAttributeValue(RequestParameters param, Content content, Attribute attribute, String field, ValidationErrors errors) {

        String value = attribute.getValue();
        String fieldName = field.toLowerCase();

        switch (fieldName){
            case ContentProperty.TITLE:
                setTitle(content, value);
                break;
            case ContentProperty.DESCRIPTION:
                content.setDescription(value);
                break;
            case ContentProperty.IMAGE:
                content.setImage(value);
                break;
            case ContentProperty.ALT_TITLE:
                content.setAltTitle(value);
                break;
            case ContentProperty.URL:
                content.setLocation(value);
                String inputField = AttributeHelper.getInputFieldName(attribute.getNameIncludingPath());
                content.setDoOpenInNewWindow(param.getBoolean(inputField + "_newwindow"));
                break;
            case ContentProperty.OWNER:
                content.setOwner(value);
                break;
            case ContentProperty.OWNERPERSON:
                content.setOwnerPerson(value);
                break;
            case ContentProperty.ALIAS:
                setAlias(content, errors, value);
                break;
            case ContentProperty.PUBLISHER:
                content.setPublisher(value);
                break;

        }
    }

    private void setTitle(Content content, String value) {
        // These characters are not allowed in page title
        value = value.replaceAll("<", "&lt");
        value = value.replaceAll(">", "&gt");
        value = value.replaceAll("\"", "&quot;");
        value = value.replaceAll("\r\n", "");
        value = value.replaceAll("\n", "");
        content.setTitle(value);
    }

    private void setAlias(Content content, ValidationErrors errors, String value) {
        String alias = value;
        if (alias.length() > 0) {
            alias = alias.toLowerCase();

            // Remove illegal chars etc
            alias = PrettyURLEncoder.encode(alias);

        /*
        * Alias always starts and ends with /
        * Alias / is used for front page
        */
            if (alias.charAt(0) != '/') {
                alias = "/" + alias;
            }
            if (alias.length() > 1) {
                if (alias.charAt(alias.length()-1) != '/') {
                    alias = alias + "/";
                }
            }
        }

        content.setAlias(alias);

        ContentAliasValidator.validateAlias(alias, content, errors);
    }
}
