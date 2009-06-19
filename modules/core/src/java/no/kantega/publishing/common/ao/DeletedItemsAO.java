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

import no.kantega.publishing.common.data.Note;
import no.kantega.publishing.common.data.DeletedItem;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.commons.exception.SystemException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class DeletedItemsAO {
    private static final String SOURCE = "aksess.DeletedItemsAO";

    public static int addDeletedItem(DeletedItem item) throws SystemException {
        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement p = c.prepareStatement("INSERT INTO deleteditems (Title, ObjectType, DeletedDate, DeletedBy) VALUES (?, ?, ?, ?)", new String[] {"Id"});

            p.setString(1, item.getTitle());
            p.setInt(2, item.getObjectType());
            p.setTimestamp(3, new java.sql.Timestamp(new Date().getTime()));
            p.setString(4, item.getDeletedBy());

            p.executeUpdate();

            ResultSet rs = p.getGeneratedKeys();
            if(rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SystemException("Could get the generated key", SOURCE, null);
            }

        } catch (SQLException e) {
          throw new SystemException("SQL exception while adding deleteditem", SOURCE, e);
        } finally {
            if(c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                    // Could not close connection, probably closed already
                }
            }
        }
    }


    public static void purgeDeletedItem(int id) throws SystemException {

        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement p = c.prepareStatement("DELETE  FROM deleteditems WHERE Id = ?");
            p.setInt(1, id);
            p.executeUpdate();
        } catch (SQLException e) {
            throw new SystemException("SQL Exception while purging deleteditem", SOURCE, e);
        } finally {
            if(c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                    // Could not close connection, probably closed already
                }
            }
        }
    }


    public static List getDeletedItems(String userId) throws SystemException {

        List items = new ArrayList();

        Connection c = null;

        String query = "";
        if (userId != null && userId.length() != 0) {
            query = "SELECT * FROM deleteditems WHERE DeletedBy = ?";
        } else {
            query = "SELECT * FROM deleteditems";
        }

        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement p = c.prepareStatement(query + " ORDER By DeletedDate");
            if (userId != null && userId.length() != 0) {
                p.setString(1, userId);
            }
            ResultSet rs = p.executeQuery();
            while(rs.next()) {
                items.add(getDeletedItemFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new SystemException("SQL Exception while getting deleteditems", SOURCE, e);
        } finally {
            if(c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                    // Could not close connection, probably closed already
                }
            }
        }
        return items;
    }


    private static DeletedItem getDeletedItemFromResultSet(ResultSet rs) throws SQLException {
        DeletedItem item = new DeletedItem();
        item.setId(rs.getInt("Id"));
        item.setTitle(rs.getString("Title"));
        item.setObjectType(rs.getInt("ObjectType"));
        item.setDeletedDate(rs.getTimestamp("DeletedDate"));
        item.setDeletedBy(rs.getString("DeletedBy"));
        return item;
    }
}
