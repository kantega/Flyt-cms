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

package no.kantega.publishing.search.dao;

import no.kantega.publishing.search.index.model.TmBaseName;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class AksessDao {
    private DataSource dataSource;
    private static final int STATUS_PUBLISHED = 30;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public int countActiveContentIds() throws SQLException {
        Connection c = dataSource.getConnection();
        try {
            PreparedStatement p = c.prepareStatement("SELECT count(DISTINCT content.ContentId) FROM content, associations WHERE content.ContentId = associations.ContentId AND associations.IsDeleted = 0");

            ResultSet rs = p.executeQuery();
            int n = 0;
            if(rs.next()) {
                n = rs.getInt(1);
            }

            return n;
        } finally {
            c.close();
        }


    }
    public int countActiveAttachmentIds() throws SQLException {
        Connection c = dataSource.getConnection();
        try {
            PreparedStatement p = c.prepareStatement("SELECT count(DISTINCT attachments.Id) FROM attachments, content, associations WHERE attachments.ContentId = content.ContentId AND content.ContentId = associations.ContentId AND associations.IsDeleted = 0 AND (Filename like '%pdf' OR Filename like '%doc')");

            ResultSet rs = p.executeQuery();
            int n = 0;
            if(rs.next()) {
                n = rs.getInt(1);
            }

            return n;
        } finally {
            c.close();
        }


    }
    public int getNextActiveContentId(int i) throws SQLException {
        Connection c = dataSource.getConnection();
        try {
            PreparedStatement p = c.prepareStatement("SELECT DISTINCT content.ContentId FROM content, associations WHERE content.ContentId = associations.ContentId AND associations.IsDeleted = 0 AND content.ContentId > ? ORDER BY ContentID");
            p.setInt(1, i);
            ResultSet rs = p.executeQuery();
            int n = -1;
            if(rs.next()) {
                n = rs.getInt("ContentId");
            }

            return n;
        } finally {
            c.close();
        }
    }

    public int getNextActiveAttachmentId(int i) throws SQLException {
        Connection c = dataSource.getConnection();
        try {
            PreparedStatement p = c.prepareStatement("SELECT DISTINCT attachments.Id FROM attachments, content, associations WHERE attachments.ContentId = content.ContentId AND content.ContentId = associations.ContentId AND associations.IsDeleted = 0 AND (Filename like '%pdf' OR Filename like '%doc') AND attachments.id > ? ORDER BY attachments.id");
            p.setInt(1, i);
            ResultSet rs = p.executeQuery();
            int n = -1;
            if(rs.next()) {
                n = rs.getInt("Id");
            }

            return n;
        } finally {
            c.close();
        }
    }

    public int getActiveContentVersionId(int contentId) throws SQLException {
        Connection c = dataSource.getConnection();
        try {
            PreparedStatement p = c.prepareStatement("SELECT ContentVersionId FROM contentversion WHERE ContentId = ? AND IsActive=1 AND Status = ?");
            p.setInt(1, contentId);
            p.setInt(2, STATUS_PUBLISHED);
            int a = -1;
            ResultSet rs = p.executeQuery();
            if(rs.next()) {
                a = rs.getInt("ContentVersionId");
            }

            return a;
        } finally {
            c.close();
        }
    }

    // TODO: Slett denne ?
    @Deprecated
    public int[] getAttachmentIdsByContentId(int contentId) throws SQLException {
        Connection c = dataSource.getConnection();

        try {
            PreparedStatement p = c.prepareStatement("SELECT Id FROM attachments WHERE ContentId=? AND (Filename like '%pdf' OR Filename like '%doc')");
            p.setInt(1, contentId);

            ResultSet rs = p.executeQuery();

            List ids = new ArrayList();

            while(rs.next()) {
                ids.add(rs.getInt(1));
            }

            int[] ret = new int[ids.size()];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = (Integer) ids.get(i);
            }

            return ret;
        } finally {
            c.close();
        }
    }

    public java.util.Date getContentLastPublishedDate(int contentId) throws SQLException {
        int a = getActiveContentVersionId(contentId);
        Connection c = dataSource.getConnection();
        try {
            PreparedStatement p = c.prepareStatement("SELECT LastModified FROM contentversion WHERE ContentVersionId = ? AND IsActive = 1");
            p.setInt(1, a);

            ResultSet rs = p.executeQuery();

            java.util.Date d = new java.util.Date();
            if(rs.next()) {
                d = new java.util.Date(rs.getTimestamp(1).getTime());
            }


            return d;
        } finally {
            c.close();
        }
    }

    public TmBaseName[] getTmBaseNames(int contentId) throws SQLException {
        Connection c = null;

        try {
            c = dataSource.getConnection();
            PreparedStatement p = c.prepareStatement("SELECT ContentId, tmbasename.TopicId, tmbasename.TopicMapId, tmbasename.Basename " +
                    "FROM ct2topic, tmbasename " +
                    "WHERE ContentId=? " +
                    "AND tmbasename.TopicId=ct2topic.TopicId " +
                    "AND tmbasename.TopicMapId=ct2topic.TopicMapId");

            p.setInt(1, contentId);
            ResultSet rs = p.executeQuery();

            List baseNames = new ArrayList();

            while(rs.next()) {
                TmBaseName bn = new TmBaseName();
                bn.setTopicId(rs.getString("TopicId"));
                bn.setTopicMapId(rs.getInt("TopicMapId"));
                bn.setBaseName(rs.getString("Basename"));
                baseNames.add(bn);
            }


            return (TmBaseName[]) baseNames.toArray(new TmBaseName[0]);
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }
}
