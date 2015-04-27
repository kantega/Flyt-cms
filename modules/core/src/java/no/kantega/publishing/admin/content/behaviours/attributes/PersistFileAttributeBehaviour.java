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

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.ao.AttachmentAO;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.FileAttribute;
import no.kantega.publishing.common.data.enums.ContentProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PersistFileAttributeBehaviour implements PersistAttributeBehaviour {

    public void persistAttribute(Connection c, Content content, Attribute attribute) throws SQLException, SystemException {
        if (attribute instanceof FileAttribute) {
            FileAttribute fattr = (FileAttribute)attribute;
            if (fattr.getDeleteAttachment()) {
                String value = fattr.getValue();
                if (value != null && value.length() > 0) {
                    AttachmentAO.deleteAttachment(Integer.parseInt(value));
                }
                attribute.setValue("");
            }

            // Ved opplasting av filer må id'en til fila lagres i location feltet dersom URL er angitt som felt som attributten
            String field = attribute.getField();
            if (ContentProperty.URL.equalsIgnoreCase(field)) {
                content.setLocation(attribute.getValue());

                try(PreparedStatement cst = c.prepareStatement("update content set Location = ? where ContentId = ?")) {
                    cst.setString(1, content.getLocation());
                    cst.setInt(2, content.getId());
                    cst.execute();
                }
            }

        }

        PersistSimpleAttributeBehaviour saveSimple = new PersistSimpleAttributeBehaviour();
        saveSimple.persistAttribute(c, content, attribute);
    }
}
