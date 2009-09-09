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

import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.DatetimeAttribute;
import no.kantega.commons.exception.SystemException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;

public class UnPersistDateTimeAttributeBehaviour implements UnPersistAttributeBehaviour{
    public void unpersistAttribute(ResultSet rs, Attribute attribute) throws SQLException, SystemException {
        attribute.setName(rs.getString("Name"));
        String value = rs.getString("Value");
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        try {
            DatetimeAttribute datetimeAttribute = (DatetimeAttribute)attribute;
            datetimeAttribute.setValue(df.parse(value));
        } catch (ParseException e) {
            attribute.setValue(value);
        }
    }
}