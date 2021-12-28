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

import com.google.common.collect.Lists;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.attachment.ao.AttachmentAO;
import no.kantega.publishing.api.content.ContentIdHelper;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.event.ContentEvent;
import no.kantega.publishing.event.ContentEventListener;
import no.kantega.publishing.spring.RootContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class AttachmentAOImpl implements AttachmentAO {

    private final String DB_COLS = "Id, ContentId, Language, Filename, Lastmodified, FileSize, IsSearchable";
    private ContentIdHelper contentIdHelper;
    private ContentEventListener contentNotifier;

    @Override
    public int setAttachment(Attachment attachment) throws SystemException {
        if(contentNotifier == null){
            contentNotifier = RootContext.getInstance().getBean("contentListenerNotifier", ContentEventListener.class);
        }
        try (Connection c = dbConnectionFactory.getConnection()){
            byte[] data = attachment.getData();

            attachment.setLastModified(new Date());

            boolean attachmentExists = doesAttachmentAlreadyExist(attachment, c);

            if (attachmentExists) {
                if (data != null) {
                    try(PreparedStatement st = c.prepareStatement("update attachments set ContentId = ?, Filename = ?, Data = ?, LastModified = ?, FileSize = ?, IsSearchable = ? where Id = ?")) {
                        st.setInt(1, attachment.getContentId());
                        st.setString(2, attachment.getFilename());
                        st.setBinaryStream(3, new ByteArrayInputStream(data), (int) data.length);
                        st.setTimestamp(4, new java.sql.Timestamp(new Date().getTime()));
                        st.setInt(5, attachment.getSize());
                        st.setBoolean(6, attachment.isSearchable());
                        st.setInt(7, attachment.getId());
                        st.execute();
                    }
                } else {
                    /*
                     * Content id of attachment can not be set before after content is saved in database.
                     * Update contentid now that content id has been set
                     */
                    try(PreparedStatement st = c.prepareStatement("update attachments set ContentId = ?, IsSearchable = ? where Id = ?")) {
                        st.setInt(1, attachment.getContentId());
                        st.setBoolean(2, attachment.isSearchable());
                        st.setInt(3, attachment.getId());
                        st.execute();
                    }
                }
            } else {
                if (data != null) {
                    try(PreparedStatement st = c.prepareStatement("insert into attachments (ContentId, Language, Filename, Data, LastModified, FileSize, IsSearchable) values(?,?,?,?,?,?,?)", new String[] {"ID"})) {
                        st.setInt(1, attachment.getContentId());
                        st.setInt(2, attachment.getLanguage());
                        st.setString(3, attachment.getFilename());
                        st.setBinaryStream(4, new ByteArrayInputStream(data), data.length);
                        st.setTimestamp(5, new java.sql.Timestamp(new Date().getTime()));
                        st.setInt(6, attachment.getSize());
                        st.setBoolean(7, attachment.isSearchable());

                        st.execute();

                        try(ResultSet rs = st.getGeneratedKeys()) {
                            if (rs.next()) {
                                attachment.setId(rs.getInt(1));
                            }
                        }
                    }
                }
            }

            indexAttachmentIfContentIdSet(attachment);

            return attachment.getId();
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", e);
        }

    }

    private void indexAttachmentIfContentIdSet(Attachment attachment) {
        if(attachment.getContentId() > 0){
            contentNotifier.attachmentUpdated(new ContentEvent().setAttachment(attachment));
        }
    }

    private boolean doesAttachmentAlreadyExist(Attachment attachment, Connection c) throws SQLException {
        boolean attachmentExists = false;
        if (attachment.getId() != -1) {
            try(PreparedStatement st = c.prepareStatement("select Id from attachments where Id = ?")){
                st.setInt(1, attachment.getId());
                try(ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        attachmentExists = true;
                    }
                }
            }
        }
        return attachmentExists;
    }

    @Override
    public void deleteAttachment(int id) throws SystemException {
        Attachment attachment = getAttachment(id);
        try (Connection c = dbConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement("delete from attachments where Id = ?")) {
            st.setInt(1, id);
            st.execute();
        } catch (SQLException e) {
            throw new SystemException("SQL feil ved sletting av vedlegg", e);
        }
        contentNotifier.attachmentDeleted(new ContentEvent().setAttachment(attachment));
    }


    @Override
    public Attachment getAttachment(int id) throws SystemException {

        String query = "select " + DB_COLS + " from attachments where Id = ?";

        try (Connection c = dbConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(query)) {
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return getAttachmentFromRS(rs);
            }
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", e);
        }
    }

    @Override
    public void streamAttachmentData(int id, InputStreamHandler ish) throws SystemException {
        String query = "select Data from attachments where Id = ?";
        try (Connection c = dbConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(query)) {
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return;
                }
                Blob blob = rs.getBlob("Data");
                ish.handleInputStream(blob.getBinaryStream());
            }
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", e);
        } catch (IOException e) {
            // Connection to browser was interrupted
        }
    }


    @Override
    public List<Attachment> getAttachmentList(ContentIdentifier cid) throws SystemException {
        List<Attachment> list = new ArrayList<>();
        try (Connection c = dbConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("select " + DB_COLS + " from attachments where ContentId = ?")) {
            if(contentIdHelper == null){
                contentIdHelper = RootContext.getInstance().getBean(ContentIdHelper.class);
            }
            contentIdHelper.assureContentIdAndAssociationIdSet(cid);
            ps.setInt(1, cid.getContentId());
            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Attachment mm = getAttachmentFromRS(rs);
                    list.add(mm);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", e);
        }
    }

    private Attachment getAttachmentFromRS(ResultSet rs) throws SQLException {
        Attachment file = new Attachment();

        file.setId(rs.getInt("Id"));
        file.setContentId(rs.getInt("ContentId"));
        file.setLanguage(rs.getInt("Language"));
        file.setFilename(rs.getString("Filename"));
        file.setLastModified(rs.getTimestamp("LastModified"));
        file.setSize(rs.getInt("FileSize"));
        file.setSearchable(rs.getBoolean("IsSearchable"));
        return file;
    }

    /**
     * Copies each attachment with contentId to a new with a new contentId.
     * @param contentId of the old attachment.
     * @param newContentId, if of the new attachment.
     */
    @Override
    public void copyAttachment(int contentId, int newContentId) {
        try {
            dbConnectionFactory.getJdbcTemplate().update("insert into attachments (ContentId, Language, Filename, Lastmodified, FileSize, IsSearchable, Data) " +
                    "select ?, Language, Filename, Lastmodified, FileSize, IsSearchable, Data from attachments where ContentId = ?", newContentId, contentId);
        } catch (Exception e) {
            throw new SystemException("SQL feil ved kopiering av vedlegg", e);
        }
    }

    @Override
    public List<Integer> getAllAttachmentIds() {
        return dbConnectionFactory.getJdbcTemplate().queryForList("select Id from attachments", Integer.class);
    }


    @Override
    public List<Attachment> getAttachments(List<Integer> allAttachmentIds) {
        List<Attachment> attachments = new LinkedList<>();

        if (!allAttachmentIds.isEmpty()) {
            List<List<Integer>> partition = Lists.partition(allAttachmentIds, 500);
            for (List<Integer> ids : partition) {
                String query = "select " + DB_COLS + " from attachments where Id in " + getParamsString(ids);

                try (Connection c = dbConnectionFactory.getConnection();
                     PreparedStatement ps = c.prepareStatement(query)) {

                    for(int i = 1; i < ids.size() + 1; i++) {
                        ps.setInt(i, ids.get(i - 1));
                    }
                    try(ResultSet rs = ps.executeQuery()) {
                        while(rs.next()) {
                            attachments.add(getAttachmentFromRS(rs));
                        }

                    }
                } catch (SQLException e) {
                    throw new SystemException("SQL Feil ved databasekall", e);
                }
            }
        }
        return attachments;
    }

    private static String getParamsString(List<Integer> allAttachmentIds) {
        StringBuilder params = new StringBuilder("(");
        for (int i = 0; i < allAttachmentIds.size(); i++) {
            params.append('?');
            if(i < allAttachmentIds.size() - 1) {
                params.append(',');
            }
        }
        params.append(")");
        return params.toString();
    }

    public void setContentIdHelper(ContentIdHelper contentIdHelper) {
        this.contentIdHelper = contentIdHelper;
    }

    public void setContentNotifier(ContentEventListener contentNotifier) {
        this.contentNotifier = contentNotifier;
    }
}
