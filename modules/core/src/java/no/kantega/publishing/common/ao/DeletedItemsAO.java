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
import no.kantega.publishing.common.data.DeletedItem;
import no.kantega.publishing.common.util.database.dbConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeletedItemsAO {

    public static int addDeletedItem(DeletedItem item) throws SystemException {
        try (Connection c = dbConnectionFactory.getConnection()){
            PreparedStatement p = c.prepareStatement("INSERT INTO deleteditems (Title, ObjectType, DeletedDate, DeletedBy) VALUES (?, ?, ?, ?)", new String[] {"ID"});

            p.setString(1, item.getTitle());
            p.setInt(2, item.getObjectType());
            p.setTimestamp(3, new java.sql.Timestamp(new Date().getTime()));
            p.setString(4, item.getDeletedBy());

            p.executeUpdate();

            ResultSet rs = p.getGeneratedKeys();
            if(rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SystemException("Could get the generated key", null);
            }

        } catch (SQLException e) {
          throw new SystemException("SQL exception while adding deleteditem", e);
        }
    }


    public static void purgeDeletedItem(int id) throws SystemException {

        try (Connection c = dbConnectionFactory.getConnection()){
            PreparedStatement p = c.prepareStatement("DELETE  FROM deleteditems WHERE Id = ?");
            p.setInt(1, id);
            p.executeUpdate();
        } catch (SQLException e) {
            throw new SystemException("SQL Exception while purging deleteditem", e);
        }
    }


    public static List<DeletedItem> getDeletedItems(String userId) throws SystemException {

        List<DeletedItem> items = new ArrayList<>();

        String query;
        if (userId != null && userId.length() != 0) {
            query = "SELECT * FROM deleteditems WHERE DeletedBy = ?";
        } else {
            query = "SELECT * FROM deleteditems";
        }

        try (Connection c = dbConnectionFactory.getConnection()){
            PreparedStatement p = c.prepareStatement(query + " ORDER By DeletedDate");
            if (userId != null && userId.length() != 0) {
                p.setString(1, userId);
            }
            ResultSet rs = p.executeQuery();
            while(rs.next()) {
                items.add(getDeletedItemFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new SystemException("SQL Exception while getting deleteditems", e);
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
