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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Attribute representing an number.
 */
public class NumberAttribute extends Attribute {

    protected String numberRegExp = "^\\-?[\\d]{1,}$";

    public NumberAttribute() {
        super();
    }

    public NumberAttribute(String name, Number value) {
        super(name, value.toString());
    }

    public void validate(ValidationErrors errors) {
        super.validate(errors);
        if (errors.getLength() > 0) {
            return;
        }

        if (isBlank(regexp)) regexp = numberRegExp;
        if (isNotBlank(value)) {
            if (!value.matches(regexp) && !value.matches(numberRegExp)) {
                errors.add(name, "aksess.feil.invalidnumber", Collections.<String, Object>singletonMap("field", title));
            }
        }
    }

    public void setValue(Number value) {
        super.setValue(value.toString());
    }

    public String getValue(String format, Locale locale) {

        if (value == null) {
            return "";
        }
        if (format == null || format.length() == 0) {
            return getValue();
        }

        NumberFormat formatter;

        if (locale != null) {
            formatter = new DecimalFormat(format, new DecimalFormatSymbols(locale));
        } else {
            formatter = new DecimalFormat(format);
        }

        return formatter.format(Double.parseDouble(value));
    }


    @Override
    public String getRenderer() {
        return "number";
    }

    public String getValue(String format) {
        return getValue(format, null);
    }
}
