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
import no.kantega.commons.exception.RegExpSyntaxException;
import no.kantega.commons.util.RegExp;
import no.kantega.commons.client.util.ValidationErrors;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class EmailAttribute extends Attribute {
    protected boolean isSearchable = true;

    public void validate(ValidationErrors errors) throws RegExpSyntaxException {
        super.validate(errors);
        if (errors.getLength() > 0) {
            return;
        }

        if ((value != null) && (value.length() > 0)) {
            if (!RegExp.isEmail(value)) {
                Map<String, Object> objects = new HashMap<String, Object>();
                objects.put("field", title);                
                errors.add(name, "aksess.feil.invalidemail", objects);
            }
        }
    }

    public boolean isSearchable() {
        return true;
    }

    @Override
    public String getRenderer() {
        return "email";
    }
}
