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

import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.*;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.admin.content.behaviours.attributes.UpdateAttributeFromRequestBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.MapAttributeValueToContentPropertyBehaviour;
import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.exception.RegExpSyntaxException;
import no.kantega.commons.exception.InvalidFileException;
import no.kantega.publishing.security.SecuritySession;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class SaveContentHelper {

    private HttpServletRequest request = null;
    private Content content = null;
    private int attributeType = -1;

    public SaveContentHelper(HttpServletRequest request, Content content, int attributeType) throws SystemException, InvalidTemplateException, InvalidFileException {
        this.request = request;
        this.content = content;
        this.attributeType = attributeType;
    }


    public ValidationErrors getHttpParameters(ValidationErrors errors) throws RegExpSyntaxException {
        RequestParameters param = new RequestParameters(request, "utf-8");

        List attrlist = content.getAttributes(attributeType);

        for (int i = 0; i < attrlist.size(); i++) {
            Attribute attr = (Attribute)attrlist.get(i);
            if (attr.isEditable() && !attr.isHidden(content) && roleCanEdit(attr, request)) {
                UpdateAttributeFromRequestBehaviour updater = attr.getUpdateFromRequestBehaviour();

                // Oppdaterer attribut objektet med riktige verdier fra requesten
                updater.updateAttribute(param, content, attr);

                // Mapper attributter til bestemte contentproperties
                MapAttributeValueToContentPropertyBehaviour mapper = attr.getMapAttributeValueToContentPropertyBehaviour();
                if (mapper != null && attr.getField() != null) {
                    String fieldNames = attr.getField();
                    String[] fields = fieldNames.split(",");
                    for (int j = 0; j < fields.length; j++) {
                        String field = fields[j];
                        mapper.mapAttributeValue(param, content, attr, field, errors);
                    }
                }
            }
            attr.validate(errors);
        }

        return errors;
    }

    private boolean roleCanEdit(Attribute attr, ServletRequest request) {
        String[] roles = attr.getEditableByRoles();
        if (roles != null && roles.length > 0) {
            return SecuritySession.getInstance((HttpServletRequest) request).isUserInRole(roles);
        }

        return true;
    }
}
