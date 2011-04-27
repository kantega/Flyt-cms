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
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.admin.content.behaviours.attributes.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class DateAttribute extends Attribute {
    protected int size = 10;

    private String dateFormat;

    Date dateValue = null;

    public DateAttribute() {
         setFormat(Aksess.getDefaultDateFormat());
    }

    public DateAttribute(String name, String value) {
        this.name  = name;
        setValue(value);
    }

    protected void setFormat(String dateFormat) {
        setFormat(dateFormat, null);
    }

    protected void setFormat(String dateFormat, Locale locale) {
        this.dateFormat = dateFormat;

        if (dateValue != null) {
            DateFormat df;

            // Oppdater verdi basert p� nytt format
            if (locale != null) {
                df = new SimpleDateFormat(dateFormat, locale);
            } else {
                df = new SimpleDateFormat(dateFormat);
            }
            value = df.format(dateValue);
        }

    }


    public Date getValueAsDate() {
        return dateValue;
    }

    public String getValue(String dateFormat, Locale locale) {
        DateFormat df;

        if (dateValue == null) {
            return "";
        }

        if (locale != null) {
            df = new SimpleDateFormat(dateFormat, locale);
        } else {
            df = new SimpleDateFormat(dateFormat);
        }

        return df.format(dateValue);
    }

    @Override
    public String getRenderer() {
        return "date";
    }

    @Override
    public void validate(ValidationErrors errors) throws RegExpSyntaxException {
        super.validate(errors);

        if (value == null || value.length() == 0) {
            return;
        }

        value = value.replace('/', '.');
        value = value.replace('-', '.');

        Map<String, Object> objects = new HashMap<String, Object>();
        objects.put("field", title);

        DateFormat df = new SimpleDateFormat(dateFormat);

        String[] d = value.split("\\.");
        if (d.length != 3) {
            errors.add(name, "aksess.feil.invaliddate", objects);
            return;
        }

        if (d[2].length() < 4) {
            errors.add(name, "aksess.feil.invaliddate.year", objects);
            return;
        }

        try {
            if(!value.equals(df.format(df.parse(value)))) {
                errors.add(name, "aksess.feil.invaliddate", objects);
            }

        } catch (ParseException e) {
            errors.add(name, "aksess.feil.invaliddate", objects);
        }
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(Date date) {
        dateValue = date;
        if (date != null) {
            DateFormat df = new SimpleDateFormat(dateFormat);
            value = df.format(date);
        } else {
            value = "";
        }
    }

    @Override
    public void setValue(String value) {
        if (value == null) {
            value = "";
        }

        if (value.length() == dateFormat.length()) {
            DateFormat df = new SimpleDateFormat(dateFormat);
            try {
                dateValue = df.parse(value);
                if(!value.equals(df.format(df.parse(value)))) {
                    dateValue = null;
                }
            } catch (ParseException e) {
                dateValue = null;
            }
        } else {
            dateValue = null;
        }

        this.value = value;
    }

    @Override
    public void cloneValue(Attribute attribute) {
        if (attribute instanceof DateAttribute) {
            dateValue = ((DateAttribute)attribute).getValueAsDate();
            if (dateValue != null) {
                DateFormat df = new SimpleDateFormat(dateFormat);
                value = df.format(dateValue);
            } else {
                value = attribute.getValue();
            }
        } else {
            setValue(attribute.getValue());
        }
    }

    public boolean isSearchable() {
        return true;
    }

    @Override
    public PersistAttributeBehaviour getSaveBehaviour() {
        return new PersistDateAttributeBehaviour();
    }

    @Override
    public UnPersistAttributeBehaviour getFetchBehaviour() {
        return new UnPersistDateAttributeBehaviour();
    }

    @Override
    public MapAttributeValueToContentPropertyBehaviour getMapAttributeValueToContentPropertyBehaviour() {
        return new MapDateAttributeValueToContentPropertyBehaviour();
    }

}

