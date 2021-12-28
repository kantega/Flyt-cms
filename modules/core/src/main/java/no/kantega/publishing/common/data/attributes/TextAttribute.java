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

package no.kantega.publishing.common.data.attributes;

import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.util.StringHelper;
import no.kantega.publishing.admin.content.behaviours.attributes.UpdateAttributeFromRequestBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.UpdateTextAttributeFromRequestBehaviour;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.enums.AttributeProperty;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Attribute representing plain text.
 */
public class TextAttribute extends Attribute {

    public TextAttribute() {
        super();
    }

    public TextAttribute(String name, String value) {
        super(name, value);
    }

    public String getProperty(String property) {
        String returnValue = value;
        if (value == null || value.length() == 0) {
            return null;
        }
        if (AttributeProperty.HTML.equalsIgnoreCase(property)) {
            // Første linje er pga skrivefeil, kan ikke fjernes før evt databaser oppdateres
            returnValue = StringHelper.replace(returnValue, "\"" + Aksess.VAR_WEB + "\"/", Aksess.getContextPath() + "/");
            returnValue = StringHelper.replace(returnValue, Aksess.VAR_WEB + "/", Aksess.getContextPath() + "/");
            returnValue = StringHelper.makeLinks(returnValue);
            returnValue = StringHelper.replace(returnValue, "\n", "<br>");
        }
        return returnValue;
    }


    public void validate(ValidationErrors errors){
        super.validate(errors);
        if (errors.getLength() > 0) {
            return;
        }

        if (isNotBlank(value) && isNotBlank(regexp)) {
            if (!value.matches(regexp)) {
                Map<String, Object> objects = new HashMap<>();
                objects.put("field", title);
                errors.add(name, "aksess.feil.invalidchar", objects);
            }
        }
    }

    public UpdateAttributeFromRequestBehaviour getUpdateFromRequestBehaviour() {
        return new UpdateTextAttributeFromRequestBehaviour();
    }

    public boolean isSearchable() {
        return true;
    }
}

