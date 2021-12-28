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
import no.kantega.publishing.api.xmlcache.XMLCacheEntry;
import no.kantega.publishing.api.xmlcache.XmlCache;
import no.kantega.publishing.common.util.database.SQLHelper;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class XMLCacheImpl implements XmlCache {
    private static final Logger log = LoggerFactory.getLogger(XMLCacheImpl.class);

    @Override
    public XMLCacheEntry getXMLFromCache(String id){
        XMLCacheEntry cacheEntry = null;

        try (Connection c = dbConnectionFactory.getConnection();
            PreparedStatement st = c.prepareStatement("select * from xmlcache where id = ?")){
            st.setString(1, id);
            try(ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    cacheEntry = new XMLCacheEntry();
                    String tmp = rs.getString("Data");
                    cacheEntry.setId(id);
                    cacheEntry.setXml(XMLHelper.getDocument(tmp));
                    cacheEntry.setLastUpdated(rs.getTimestamp("LastUpdated"));
                }
            }
        } catch (SQLException e) {
            log.error("SQL Feil ved databasekall", e);
            throw new SystemException("SQL Feil ved databasekall", e);
        }

        return cacheEntry;
    }

    @Override
    public void storeXMLInCache(XMLCacheEntry cacheEntry){
        try (Connection c = dbConnectionFactory.getConnection()){
            boolean isUpdate = false;
            try (PreparedStatement st = c.prepareStatement("select id from xmlcache where id = ?")){

                st.setString(1, cacheEntry.getId());
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        isUpdate = true;
                    }
                }
            }
            String str = XMLHelper.getString(cacheEntry.getXml());
            try(PreparedStatement st =
                    isUpdate ? c.prepareStatement("update xmlcache set Data = ?, LastUpdated = ? where Id = ?")
                    : c.prepareStatement("insert into xmlcache values(?,?,?)")) {
                if (isUpdate) {
                    st.setString(1, str);
                    st.setTimestamp(2, new java.sql.Timestamp(new Date().getTime()));
                    st.setString(3, cacheEntry.getId());
                } else {
                    st.setString(1, cacheEntry.getId());
                    st.setString(2, str);
                    st.setTimestamp(3, new java.sql.Timestamp(new Date().getTime()));
                }
                st.execute();
            }

        } catch (SQLException e) {
            log.error("SQL Feil ved databasekall", e);
            throw new SystemException("SQL Feil ved databasekall", e);
        }
    }

    public List<XMLCacheEntry> getSummary() {
        List<XMLCacheEntry> list = new ArrayList<>();

        try (Connection c = dbConnectionFactory.getConnection()){
            ResultSet rs = SQLHelper.getResultSet(c, "select Id, LastUpdated from xmlcache");
            while (rs.next()) {
                XMLCacheEntry cacheEntry = new XMLCacheEntry();
                cacheEntry.setId(rs.getString("Id"));
                cacheEntry.setLastUpdated(rs.getTimestamp("LastUpdated"));
                list.add(cacheEntry);
            }
            rs.close();
        } catch (SQLException e) {
            log.error("SQL Feil ved databasekall", e);
            throw new SystemException("SQL Feil ved databasekall", e);
        }
        return list;
    }

}
