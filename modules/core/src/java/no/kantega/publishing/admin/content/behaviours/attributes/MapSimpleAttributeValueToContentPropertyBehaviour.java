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
import no.kantega.publishing.admin.content.util.ValidatorHelper;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.util.PrettyURLEncoder;

/**
 * User: Anders Skar, Kantega AS
 * Date: May 3, 2007
 * Time: 11:02:43 AM
 */
public class MapSimpleAttributeValueToContentPropertyBehaviour implements MapAttributeValueToContentPropertyBehaviour {
    public void mapAttributeValue(RequestParameters param, Content content, Attribute attribute, String field, ValidationErrors errors) {

        // Map value to specified fields
        String value = attribute.getValue();
        if (field.equalsIgnoreCase(ContentProperty.TITLE)) {
            // These characters are not allowed in page title
            value = value.replaceAll("<", "&lt");
            value = value.replaceAll(">", "&gt");
            value = value.replaceAll("\"", "&quot;");
            value = value.replaceAll("\r\n", "");
            value = value.replaceAll("\n", "");
            content.setTitle(value);
        } else if (field.equalsIgnoreCase(ContentProperty.DESCRIPTION)) {
            content.setDescription(value);
        } else if (field.equalsIgnoreCase(ContentProperty.IMAGE)) {
            content.setImage(value);
        } else if (field.equalsIgnoreCase(ContentProperty.ALT_TITLE)) {
            content.setAltTitle(value);
        } else if (field.equalsIgnoreCase(ContentProperty.URL)) {
            content.setLocation(value);
            String inputField = AttributeHelper.getInputFieldName(attribute.getName());
            content.setDoOpenInNewWindow(param.getBoolean(inputField + "_newwindow"));
        } else if (field.equalsIgnoreCase(ContentProperty.OWNER)) {
            content.setOwner(value);
        } else if (field.equalsIgnoreCase(ContentProperty.OWNERPERSON)) {
            content.setOwnerPerson(value);
        } else if (field.equalsIgnoreCase(ContentProperty.ALIAS)) {
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

            ValidatorHelper.validateAlias(alias, content, errors);
        } else if (field.equalsIgnoreCase(ContentProperty.PUBLISHER)) {
            content.setPublisher(value);
        } else if(field.equalsIgnoreCase(ContentProperty.DOCUMENTTYPEID)){
            int documenttypeValue = 0;
            try {
                documenttypeValue = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                //do nothing, documenttype will be 0
            }
            content.setDocumentTypeId(documenttypeValue);
        }
    }
}
