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

import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.ListAttribute;
import no.kantega.publishing.admin.content.behaviours.attributes.PersistAttributeBehaviour;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PersistSimpleAttributeBehaviour implements PersistAttributeBehaviour {
    public void persistAttribute(Connection c, Content content, Attribute attribute) throws SQLException {
        PreparedStatement st = c.prepareStatement("insert into contentattributes (ContentVersionId, AttributeType, DataType, Name, Value) values (?,?,?,?,?)");

        st.setInt(1, content.getVersionId());

        // Klassenavn angir navnet som brukes i databasen som attributttype
        String clsName = attribute.getClass().getName().toLowerCase();
        clsName = clsName.substring(clsName.lastIndexOf(".") + 1, clsName.lastIndexOf("attribute"));

        st.setString(2, clsName);
        st.setInt(3, attribute.getType());
        st.setString(4, attribute.getName());

        String value = attribute.getValue();
        if (attribute instanceof ListAttribute) {
            // Legg til , foran og bak for å gjøre mere søkbart
            if (value != null && value.length() > 0) {
                if (value.charAt(0) != ',') {
                    value = "," + value;
                }
                if (value.charAt(value.length()-1) != ',') {
                    value = value + ",";
                }
            }
        }
        st.setString(5, value);

        st.execute();

    }
}
