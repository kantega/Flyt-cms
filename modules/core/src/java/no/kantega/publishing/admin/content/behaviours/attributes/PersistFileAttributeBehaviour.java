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
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.FileAttribute;
import no.kantega.publishing.common.data.enums.ContentProperty;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PersistFileAttributeBehaviour implements PersistAttributeBehaviour {
    private static final String SOURCE = "aksess.PersistFileAttributeBehaviour";

    public void persistAttribute(Connection c, Content content, Attribute attribute) throws SQLException, SystemException {
        if (attribute instanceof FileAttribute) {
            FileAttribute fattr = (FileAttribute)attribute;
            MultipartFile importFile = fattr.getImportFile();
            try {
                if (importFile != null) {
                    int oldId = -1;
                    try {
                        oldId = Integer.parseInt(fattr.getValue());
                    } catch (NumberFormatException e) {

                    }



                    // Brukeren har lastet opp en fil, legg inn basen
                    Attachment attachment = new Attachment();
                    attachment.setContentId(content.getId());
                    attachment.setLanguage(content.getLanguage());
                    if (!fattr.isKeepOldVersions() && oldId != -1) {
                        // Delete old version
                        attachment.setId(oldId);
                    }

                    byte[] data = importFile.getBytes();

                    String filename = importFile.getOriginalFilename();
                    if (filename.length() > 255) {
                        filename = filename.substring(filename.length() - 255, filename.length());
                    }

                    attachment.setFilename(filename);
                    attachment.setData(data);
                    attachment.setSize(data.length);                   

                    attribute.setValue("" + AttachmentAO.setAttachment(attachment));

                    fattr.setImportFile(null);
                } else if (fattr.getDeleteAttachment()) {
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

                    PreparedStatement cst = c.prepareStatement("update content set Location = ? where ContentId = ?");
                    cst.setString(1, content.getLocation());
                    cst.setInt(2, content.getId());
                    cst.execute();
                }

            } catch (FileNotFoundException e) {
                throw new SystemException("Feil ved filvedlegg", SOURCE, e);
            } catch (IOException e) {
                throw new SystemException("Feil ved filvedlegg", SOURCE, e);
            }
        }

        PersistSimpleAttributeBehaviour saveSimple = new PersistSimpleAttributeBehaviour();
        saveSimple.persistAttribute(c, content, attribute);
    }
}
