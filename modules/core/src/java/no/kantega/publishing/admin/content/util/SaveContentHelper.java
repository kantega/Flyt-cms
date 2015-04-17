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

package no.kantega.publishing.admin.content.util;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.exception.InvalidFileException;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.admin.content.behaviours.attributes.MapAttributeValueToContentPropertyBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.UpdateAttributeFromRequestBehaviour;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.AttributeHandler;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.security.SecuritySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class SaveContentHelper {
    private static final Logger log = LoggerFactory.getLogger(SaveContentHelper.class);

    private HttpServletRequest request = null;
    private Content content = null;
    private AttributeDataType attributeDataType= AttributeDataType.getDataTypeAsEnum(-1);

    public SaveContentHelper(HttpServletRequest request, Content content, AttributeDataType attributeDataType) throws SystemException, InvalidTemplateException, InvalidFileException {
        this.request = request;
        this.content = content;
        this.attributeDataType= attributeDataType;
    }


    public ValidationErrors getHttpParameters(final ValidationErrors errors){
        final RequestParameters param = new RequestParameters(request, "utf-8");

        content.doForEachAttribute(attributeDataType, new AttributeHandler() {

            public void handleAttribute(Attribute attr) {
                if (attr.isEditable() && !attr.isHidden(content) && roleCanEdit(attr, request)) {
                    UpdateAttributeFromRequestBehaviour updater = attr.getUpdateFromRequestBehaviour();

                    updater.updateAttribute(param, content, attr);

                    // Map values to fixed content properties, such as publish date, title etc
                    MapAttributeValueToContentPropertyBehaviour mapper = attr.getMapAttributeValueToContentPropertyBehaviour();
                    if (mapper != null && attr.getField() != null) {
                        String fieldNames = attr.getField();
                        String[] fields = fieldNames.split(",");
                        for (String field : fields) {
                            mapper.mapAttributeValue(param, content, attr, field, errors);
                        }
                    }
                }
                attr.validate(errors);

            }
        });

        return errors;
    }

    private boolean roleCanEdit(Attribute attr, ServletRequest request) {
        String[] roles = attr.getEditableByRoles();
        return !(roles != null && roles.length > 0)
               || SecuritySession.getInstance((HttpServletRequest) request).isUserInRole(roles);

    }
}
