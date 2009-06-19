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

package no.kantega.publishing.admin.content.behaviours.attributes;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.DatetimeAttribute;
import no.kantega.publishing.common.data.attributes.DateAttribute;
import no.kantega.publishing.common.data.enums.ContentProperty;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;

/**
 * Author: Kristian Lier Selnæs, Kantega AS
 * Date: 24.jan.2008
 * Time: 12:15:01
 */
public class MapDateAttributeValueToContentPropertyBehaviour implements MapAttributeValueToContentPropertyBehaviour {

    public void mapAttributeValue(RequestParameters param, Content content, Attribute attribute, String field, ValidationErrors errors) {

        DateAttribute dateAttribute = (DateAttribute)attribute;
        Date value = dateAttribute.getValueAsDate();

        if (field.equalsIgnoreCase(ContentProperty.EXPIRE_DATE)) {
            if (value != null) {
                Calendar tmp = new GregorianCalendar();
                tmp.setTime(value);
                if (attribute instanceof DatetimeAttribute) {
                    tmp.set(Calendar.SECOND, 0);
                } else {
                    tmp.set(Calendar.HOUR, 23);
                    tmp.set(Calendar.MINUTE, 59);
                    tmp.set(Calendar.SECOND, 0);
                }

                content.setExpireDate(tmp.getTime());
            }
        } else if (field.equalsIgnoreCase(ContentProperty.PUBLISH_DATE)) {
            if (value != null) {
                Calendar tmp = new GregorianCalendar();
                tmp.setTime(value);
                if (attribute instanceof DatetimeAttribute) {
                    tmp.set(Calendar.SECOND, 0);
                } else {
                    tmp.set(Calendar.HOUR, 0);
                    tmp.set(Calendar.MINUTE, 0);
                    tmp.set(Calendar.SECOND, 0);
                }
                content.setPublishDate(tmp.getTime());
            }
        } else if (field.equalsIgnoreCase(ContentProperty.REVISION_DATE)) {
            if (value != null) {
                Calendar tmp = new GregorianCalendar();
                tmp.setTime(value);
                if (attribute instanceof DatetimeAttribute) {
                    tmp.set(Calendar.SECOND, 0);                    
                } else {
                    tmp.set(Calendar.HOUR, 23);
                    tmp.set(Calendar.MINUTE, 59);
                    tmp.set(Calendar.SECOND, 0);
                }
                content.setRevisionDate(tmp.getTime());
            } else {
                content.setRevisionDate(null);
            }
        } else if (field.equalsIgnoreCase(ContentProperty.TITLE)) {
            content.setTitle(dateAttribute.getValue("dd.MM.yyyy", null));
        } else if (field.equalsIgnoreCase(ContentProperty.ALT_TITLE)) {
            content.setAltTitle(dateAttribute.getValue("dd.MM.yyyy", null));
        }
    }
}
