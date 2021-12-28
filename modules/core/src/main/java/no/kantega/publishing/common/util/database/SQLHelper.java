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

package no.kantega.publishing.common.util.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 *
 */
public class SQLHelper {
    public static ResultSet getResultSet(Connection c, String query) throws SQLException {
        return getResultSet(c, query, null);
    }


    public static ResultSet getResultSet(Connection c, String query, Object[] params) throws SQLException {
        PreparedStatement s = c.prepareStatement(query);
        setParams(s, params);
        return s.executeQuery();
    }


    public static int getInt(Connection c, String query, String field) throws SQLException {
        return getInt(c, query, field, null);
    }


    public static int getInt(Connection c, String query, String field, Object[] params) throws SQLException {
        int result = -1;
        try(PreparedStatement s = c.prepareStatement(query)) {
            setParams(s, params);
            try(ResultSet rs = s.executeQuery()) {
                if (rs.next()) {
                    result = rs.getInt(field);
                }
            }
        }
        return result;
    }


    public static String getString(Connection c, String query, String field) throws SQLException {
        return getString(c, query, field, null);
    }


    public static String getString(Connection c, String query, String field, Object[] params) throws SQLException {
        String result = null;

        try(PreparedStatement s = c.prepareStatement(query)) {
            setParams(s, params);
            try(ResultSet rs = s.executeQuery()) {
                if (rs.next()) {
                    result = rs.getString(field);
                }
            }
        }
        return result;
    }

    
    private static void setParams(PreparedStatement st, Object[] params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                Object o = params[i];
                if (o instanceof Date) {
                    Date d = (Date)o;
                    st.setTimestamp(i + 1, new java.sql.Timestamp(d.getTime()));
                } else {
                    st.setObject(i + 1, o);
                }
            }
        }

    }
}
