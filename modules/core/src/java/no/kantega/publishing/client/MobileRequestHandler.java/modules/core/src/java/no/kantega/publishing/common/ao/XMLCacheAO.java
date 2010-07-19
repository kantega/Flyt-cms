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
import no.kantega.commons.util.XMLHelper;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.common.util.database.SQLHelper;
import no.kantega.publishing.common.data.XMLCacheEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import org.w3c.dom.Document;

public class XMLCacheAO {
    private static final String SOURCE = "XMLCacheAO";

    public static XMLCacheEntry getXMLFromCache(String id) throws SystemException {
        XMLCacheEntry cacheEntry = null;

        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement st = c.prepareStatement("select * from xmlcache where id = ?");
            st.setString(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                cacheEntry = new XMLCacheEntry();
                String tmp = rs.getString("Data");
                cacheEntry.setId(id);
                cacheEntry.setXml(XMLHelper.getDocument(tmp));
                cacheEntry.setLastUpdated(rs.getTimestamp("LastUpdated"));
            }
            st.close();
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

        return cacheEntry;
    }

    public static void storeXMLInCache(XMLCacheEntry cacheEntry) throws SystemException {
        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();
            boolean isUpdate = false;

            PreparedStatement st = c.prepareStatement("select id from xmlcache where id = ?");
            st.setString(1, cacheEntry.getId());
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                isUpdate = true;
            }
            st.close();

            String str = XMLHelper.getString(cacheEntry.getXml());
            if (isUpdate) {
                st = c.prepareStatement("update xmlcache set Data = ?, LastUpdated = ? where Id = ?");
                st.setString(1, str);
                st.setTimestamp(2, new java.sql.Timestamp(new Date().getTime()));
                st.setString(3, cacheEntry.getId());
            } else {
                st = c.prepareStatement("insert into xmlcache values(?,?,?)");
                st.setString(1, cacheEntry.getId());
                st.setString(2, str);
                st.setTimestamp(3, new java.sql.Timestamp(new Date().getTime()));
            }
            st.execute();
            st.close();

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

    public static List getSummary() throws SystemException {
        List list = new ArrayList();

        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();
            ResultSet rs = SQLHelper.getResultSet(c, "select Id, LastUpdated from xmlcache");
            while (rs.next()) {
                XMLCacheEntry cacheEntry = new XMLCacheEntry();
                cacheEntry.setId(rs.getString("Id"));
                cacheEntry.setLastUpdated(rs.getTimestamp("LastUpdated"));
                list.add(cacheEntry);
            }
            rs.close();
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
        return list;
    }

}
