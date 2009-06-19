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

import no.kantega.publishing.common.data.Site;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.common.util.database.SQLHelper;
import no.kantega.commons.log.Log;
import no.kantega.commons.exception.SystemException;

import java.util.List;
import java.util.ArrayList;
import java.sql.*;

public class SiteAO {
    private static final String SOURCE = "aksess.SiteAO";

    private static Site getSiteFromRS(ResultSet rs) throws SQLException {
        Site site = new Site();

        site.setId(rs.getInt("SiteId"));
        site.setName(rs.getString("Name"));
        site.setAlias(rs.getString("Alias"));

        return site;
    }


    public static List getSites() throws SystemException {
        List sites = new ArrayList();
        
        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            // Hent sites
            ResultSet rs = SQLHelper.getResultSet(c, "select * from sites order by SiteId");
            while(rs.next()) {
                Site site = getSiteFromRS(rs);
                sites.add(site);
            }

            rs.close();

            // Hent domenenavn
            rs = SQLHelper.getResultSet(c, "select SiteId, Hostname from site2hostname order by SiteId, IsDefault desc");
            while(rs.next()) {
                int siteId = rs.getInt("SiteId");
                String hostname = rs.getString("Hostname");
                for (int i = 0; i < sites.size(); i++) {
                    Site site = (Site)sites.get(i);
                    if (site.getId() == siteId) {
                        List hosts = site.getHostnames();
                        hosts.add(hostname);
                        break;
                    }
                }
            }

            return sites;

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


    public static void setSite(Site site) throws SystemException {
        Connection c = null;

        String oldAlias = null;
        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement st = null;
            if (site.getId() == -1) {
                // Ny
                st = c.prepareStatement("insert into sites (Name, Alias) values(?,?)", new String[] {"SiteId"});
            } else {
                // Oppdater
                st = c.prepareStatement("update sites set Name = ?, Alias = ? where SiteId = ?");
            }

            st.setString(1, site.getName());
            st.setString(2, site.getAlias());

            if (site.getId() != -1) {
                st.setInt(3, site.getId());
                oldAlias = SQLHelper.getString(c, "select * from sites where SiteId = " + site.getId(), "Alias");
            }


            st.execute();

            if (site.getId() == -1) {
                // Finn id til nytt objekt
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    site.setId(rs.getInt(1));
                }
                rs.close();
            }


            st.close();

            if (oldAlias != null) {
                st = c.prepareStatement("update content set Alias = ? where Alias = ?");
                st.setString(1, site.getAlias());
                st.setString(2, oldAlias);
                st.execute();
            }

            if (site.getId() != -1) {
                // Slett gamle domener
                st = c.prepareStatement("delete from site2hostname where SiteId = ?");
                st.setInt(1, site.getId());
                st.execute();
            }

            // Legg inn tilhørende host for hvert domene, første er default
            st = c.prepareStatement("insert into site2hostname values(?,?,?)");
            List hosts = site.getHostnames();
            for (int i = 0; i < hosts.size(); i++) {
                String hostname = (String)hosts.get(i);
                st.setInt(1, site.getId());
                st.setString(2, hostname);
                st.setInt(3, i == 0 ? 1:0);
                st.execute();
            }

        } catch (SQLException e) {
            Log.error(SOURCE, e, null, null);
            throw new SystemException(SOURCE, "Feil ved oppretting av site", e);
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
