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

package no.kantega.publishing.common.ao;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.ContentIdHelper;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.publishing.common.util.database.SQLHelper;
import no.kantega.publishing.common.util.database.dbConnectionFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AttachmentAO {
    private static final String SOURCE = "aksess.AttachmentAO";

    private static final String DB_COLS = "Id, ContentId, Language, Filename, Lastmodified, FileSize";

    public static int setAttachment(Attachment attachment) throws SystemException {
        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            byte[] data = attachment.getData();

            attachment.setLastModified(new Date());

            boolean attachmentExists = doesAttachmentAlreadyExist(attachment, c);

            if (attachmentExists) {
                if (data != null) {
                    PreparedStatement st = c.prepareStatement("update attachments set ContentId = ?, Filename = ?, Data = ?, LastModified = ?, FileSize = ? where Id = ?");
                    st.setInt(1, attachment.getContentId());
                    st.setString(2, attachment.getFilename());
                    st.setBinaryStream(3, new ByteArrayInputStream(data), (int)data.length);
                    st.setTimestamp(4, new java.sql.Timestamp(new Date().getTime()));
                    st.setInt(5, attachment.getSize());
                    st.setInt(6, attachment.getId());
                    st.execute();
                    st.close();
                } else {
                    /*
                     * Content id of attachment can not be set before after content is saved in database.
                     * Update contentid now that content id has been set
                     */
                    PreparedStatement st = c.prepareStatement("update attachments set ContentId = ? where Id = ?");
                    st.setInt(1, attachment.getContentId());
                    st.setInt(2, attachment.getId());
                    st.execute();
                    st.close();
                }
            } else {
                if (data != null) {
                    PreparedStatement st = c.prepareStatement("insert into attachments (ContentId, Language, Filename, Data, LastModified, FileSize) values(?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                    st.setInt(1, attachment.getContentId());
                    st.setInt(2, attachment.getLanguage());
                    st.setString(3, attachment.getFilename());
                    st.setBinaryStream(4, new ByteArrayInputStream(data), data.length);
                    st.setTimestamp(5, new java.sql.Timestamp(new Date().getTime()));
                    st.setInt(6, attachment.getSize());
                    st.execute();

                    ResultSet rs = st.getGeneratedKeys();
                    if (rs.next()) {
                        attachment.setId(rs.getInt(1));
                    }
                    rs.close();
                    st.close();
                }
            }
            return attachment.getId();
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
            }
        }

    }

    private static boolean doesAttachmentAlreadyExist(Attachment attachment, Connection c) throws SQLException {
        boolean attachmentExists = false;
        if (attachment.getId() != -1) {
            PreparedStatement st = c.prepareStatement("select Id from attachments where Id = ?");
            st.setInt(1, attachment.getId());
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                attachmentExists = true;
            }
        }
        return attachmentExists;
    }

    public static void deleteAttachment(int id) throws SystemException {
        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement st = c.prepareStatement("delete from attachments where Id = ?");
            st.setInt(1, id);
            st.execute();
            st.close();

        } catch (SQLException e) {
            throw new SystemException(SOURCE, "SQL feil ved sletting av vedlegg", e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
            }
        }
    }


    public static Attachment getAttachment(int id) throws SystemException {
        Connection c = null;

        String query = "select " + DB_COLS + " from attachments where Id = " + id;

        try {
            c = dbConnectionFactory.getConnection();
            ResultSet rs = SQLHelper.getResultSet(c, query);
            if (!rs.next()) {
                return null;
            }
            Attachment file = getAttachmentFromRS(rs);
            rs.close();
            return file;
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {

            }
        }
    }

    public static void streamAttachmentData(int id, InputStreamHandler ish) throws SystemException {
        Connection c = null;

        String query = "select Data from attachments where Id = " + id;
        try {
            c = dbConnectionFactory.getConnection();
            ResultSet rs = SQLHelper.getResultSet(c, query);
            if (!rs.next()) {
                return;
            }
            Blob blob = rs.getBlob("Data");
            ish.handleInputStream(blob.getBinaryStream());
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", SOURCE, e);
        } catch (IOException e) {
            // Connection to browser was interrupted
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {

            }
        }
    }


    public static List<Attachment> getAttachmentList(ContentIdentifier cid) throws SystemException {
        List<Attachment> list = new ArrayList<Attachment>();

        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            ContentIdHelper.assureContentIdAndAssociationIdSet(cid);
            ResultSet rs = SQLHelper.getResultSet(c, "select " + DB_COLS + " from attachments where ContentId = " + cid.getContentId());
            while(rs.next()) {
                Attachment mm = getAttachmentFromRS(rs);
                list.add(mm);
            }
            rs.close();
            return list;
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
                //
            }
        }
    }

    private static Attachment getAttachmentFromRS(ResultSet rs) throws SQLException {
        Attachment file = new Attachment();

        file.setId(rs.getInt("Id"));
        file.setContentId(rs.getInt("ContentId"));
        file.setLanguage(rs.getInt("Language"));
        file.setFilename(rs.getString("Filename"));
        file.setLastModified(rs.getTimestamp("LastModified"));
        file.setSize(rs.getInt("FileSize"));

        return file;
    }

    /**
     * Copies each attachment with contentId to a new with a new contentId.
     * @param contentId of the old attachment.
     * @param newNontentId, if of the new attachment.
     */
    public static void copyAttachment(int contentId, int newNontentId) {
        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();

            PreparedStatement st = c.prepareStatement("insert into attachments (ContentId, Language, Filename, Lastmodified, FileSize, Data) " +
                    "select " + newNontentId +", Language, Filename, Lastmodified, FileSize, Data from attachments where ContentId = " + contentId);
            st.execute();

            st.close();
        } catch (SQLException e) {
            throw new SystemException(SOURCE, "SQL feil ved kopiering av vedlegg", e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
            }
        }
    }
}
