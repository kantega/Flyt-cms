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
import no.kantega.publishing.common.data.attributes.DateAttribute;
import no.kantega.commons.exception.SystemException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;

/**
 * User: Anders Skar, Kantega AS
 * Date: May 19, 2009
 * Time: 4:17:52 PM
 */
public class UnPersistDateAttributeBehaviour implements UnPersistAttributeBehaviour{
    public void unpersistAttribute(ResultSet rs, Attribute attribute) throws SQLException, SystemException {
        String value = rs.getString("Value");
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        try {
            DateAttribute dateAttribute = (DateAttribute)attribute;
            if (value != null && value.length() > 0) {
                dateAttribute.setValue(df.parse(value));
            }
        } catch (Exception e) {
            attribute.setValue(value);
        }
    }
}
