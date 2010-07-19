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

import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.enums.AttributeProperty;
import no.kantega.commons.util.StringHelper;

/**
 *
 */
public class UrlAttribute extends Attribute {

    public String getProperty(String property) {
        String returnValue = value;
        if (value == null || value.length() == 0) {
            return null;
        }

        if (AttributeProperty.HTML.equalsIgnoreCase(property) || AttributeProperty.URL.equalsIgnoreCase(property)) {
            if (value.indexOf(":") == -1 || value.startsWith("/")) {
                return Aksess.getContextPath() + value;
            }
        }
        return returnValue;
    }

    public String getRenderer() {
        return "url";
    }

    public boolean isSearchable() {
        return true;
    }

    @Override
    public void setValue(String value) {
        if (value != null && value.startsWith("www.")) {
            value = "http://" + value;
        }
        super.setValue(value);
    }
}
