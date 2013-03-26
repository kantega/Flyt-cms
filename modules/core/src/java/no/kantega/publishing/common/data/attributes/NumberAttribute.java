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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;

/**
 * Attribute representing an number.
 */
public class NumberAttribute extends Attribute {
    protected String regexp = "^\\-?[\\d]{1,}$";

    public NumberAttribute() {
        super();
    }

    public NumberAttribute(String name, Number value) {
        super(name, value.toString());
    }

    public void validate(ValidationErrors errors) throws RegExpSyntaxException {
        super.validate(errors);
        if (errors.getLength() > 0) {
            return;
        }

        if ((value != null) && (value.length() > 0) && (regexp != null) && (regexp.length() > 0)) {
            if (!RegExp.matches(regexp, value)) {
                errors.add(name, "aksess.feil.invalidnumber", Collections.<String, Object>singletonMap("field", title));
            }
        }
    }

    public void setValue(Number value) {
        super.setValue(value.toString());
    }

    @Override
    public String getRenderer() {
        return "number";
    }

    public String getValue(String format) {
        if (value == null) {
            return "";
        }
        if (format == null || format.length() == 0) {
            return getValue();
        }
        
        NumberFormat formatter = new DecimalFormat(format);
        return formatter.format(Integer.parseInt(value));
    }
}
