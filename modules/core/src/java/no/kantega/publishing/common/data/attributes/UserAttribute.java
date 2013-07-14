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

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.admin.content.behaviours.attributes.MapAttributeValueToContentPropertyBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.MapUserAttributeValueToContentPropertyBehaviour;
import no.kantega.publishing.common.data.enums.AttributeProperty;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.security.realm.SecurityRealmFactory;
import org.w3c.dom.Element;

import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;

public class UserAttribute extends Attribute {

    private boolean multiple = false;

    private User user = null;

    private boolean moveable = true;

    @Override
    public void setConfig(Element config, Map<String, String> model) throws InvalidTemplateException, SystemException {
        super.setConfig(config, model);

        if (config != null) {
            multiple = "true".equals(config.getAttribute("multiple"));
        }
    }
    public String getRenderer() {
        return multiple ? "user_multiple" : "user";
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public String getProperty(String property) {
        if (isBlank(value)) {
            return "";
        }
        if (!value.contains(",") && (AttributeProperty.NAME.equalsIgnoreCase(property) || AttributeProperty.EMAIL.equalsIgnoreCase(property))) {
            if (user == null) {
                try {
                    user = SecurityRealmFactory.getInstance().lookupUser(value);
                } catch (SystemException e) {
                    return value;
                }
            }
            if (user != null) {
                if (AttributeProperty.NAME.equalsIgnoreCase(property)) {
                    return user.getName();
                } else if (AttributeProperty.EMAIL.equalsIgnoreCase(property)) {
                    return user.getEmail();
                }
            }
        }
        return getValue();
    }

    public boolean isMoveable() {
        return moveable;
    }

    public void setMoveable(boolean moveable) {
        this.moveable = moveable;
    }

    public MapAttributeValueToContentPropertyBehaviour getMapAttributeValueToContentPropertyBehaviour() {
        return new MapUserAttributeValueToContentPropertyBehaviour();
    }

    public boolean isSearchable() {
        return true;
    }    
}
