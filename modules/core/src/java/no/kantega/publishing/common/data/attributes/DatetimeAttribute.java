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
import no.kantega.publishing.admin.content.behaviours.attributes.MapAttributeValueToContentPropertyBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.MapDateAttributeValueToContentPropertyBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.UpdateAttributeFromRequestBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.UpdateDatetimeAttributeFromRequestBehaviour;
import no.kantega.publishing.common.Aksess;

import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Author: Kristian Lier Selnæs, Kantega AS
 * Date: 24.jan.2008
 * Time: 12:16:04
 */
public class DatetimeAttribute extends DateAttribute {


    public DatetimeAttribute() {
        setFormat(Aksess.getDefaultDatetimeFormat());
    }

    public String getRenderer() {
        return "datetime";
    }

    public String getDateValue() {
        if (value == null || value.trim().length() == 0) {
            return "";
        }

        int end = Aksess.getDefaultDateFormat().length();
        if (value.indexOf(Aksess.getDefaultDatetimeSeparator()) != -1) {
            end = Math.min(end, value.indexOf(Aksess.getDefaultDatetimeSeparator()));
        }
        return value.substring(0, end);
    }

    public String getTimeValue() {
        if (value == null || value.trim().length() == 0) {
            return "";
        }

        if (value.indexOf(Aksess.getDefaultDatetimeSeparator()) != -1) {
            int start = value.indexOf(Aksess.getDefaultDatetimeSeparator()) + Aksess.getDefaultDatetimeSeparator().length();
            return value.substring(start, value.length());
        }
        
        return "";
    }

    public void validate(ValidationErrors errors) throws RegExpSyntaxException {
        if (mandatory && (value == null || value.trim().length() == 0)) {
            errors.add(name, "Feltet " + title + " kan ikke være blankt!");
        }
        if (value != null && value.trim().length() > 0) {
            String date = getDateValue();
            if (date.length() == Aksess.getDefaultDateFormat().length()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(Aksess.getDefaultDateFormat());
                    sdf.setLenient(false);
                    sdf.parse(date);
                } catch (ParseException e) {
                    errors.add(name, "Feltet " + title + " inneholder en ugyldig dato.");
                }
            } else {
                errors.add(name, "Feltet " + title + " inneholder en ugyldig dato.  Årstall må ha 4 siffer.");
            }

            String time = getTimeValue();
            if (time.length() == Aksess.getDefaultTimeFormat().length()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(Aksess.getDefaultTimeFormat());
                    sdf.setLenient(false);
                    sdf.parse(getTimeValue());
                } catch (ParseException e) {
                    errors.add(name, "Feltet " + title + " inneholder et ugyldig klokkeslett.");
                }
            } else {
                errors.add(name, "Feltet " + title + " inneholder et ugyldig klokkeslett.");
            }
        }
    }

    @Override
    public UpdateAttributeFromRequestBehaviour getUpdateFromRequestBehaviour() {
        return new UpdateDatetimeAttributeFromRequestBehaviour();
    }
}
