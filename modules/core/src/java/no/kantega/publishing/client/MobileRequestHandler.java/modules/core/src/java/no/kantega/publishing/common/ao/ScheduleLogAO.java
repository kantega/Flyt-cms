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

import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.commons.exception.SystemException;

import java.util.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User: Anders Skar, Kantega AS
 * Date: May 15, 2007
 * Time: 10:29:20 AM
 */
public class ScheduleLogAO {
    private static String SOURCE = "ScheduleLogAO";

    public static Date getLastRun(String service) throws SystemException {
        Date lastRun = null;

        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();

            PreparedStatement st = c.prepareStatement("select * from schedulelog where Service = ?");
            st.setString(1, service);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                lastRun = rs.getTimestamp("LastRun");
            }
            rs.close();

        } catch (SQLException e) {
            throw new SystemException("SQL feil", SOURCE, e);
        } finally {

            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {

            }
        }

        return lastRun;
    }

    public static void setLastrun(String service , Date lastRun) throws SystemException {
        Date previous = getLastRun(service);

        if (lastRun == null) lastRun = new Date();

        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();

            PreparedStatement st;

            if (previous != null) {
                // Oppdaterer basen med nåværende tidspunkt
                st = c.prepareStatement("update schedulelog set LastRun = ? where Service = ?");
                st.setTimestamp(1, new java.sql.Timestamp(lastRun.getTime()));
                st.setString(2, service);
                st.execute();
            } else {
                // Oppdaterer basen med nåværende tidspunkt
                st = c.prepareStatement("insert into schedulelog values(?,?)");
                st.setString(1, service);
                st.setTimestamp(2, new java.sql.Timestamp(lastRun.getTime()));
                st.execute();
            }
        } catch (SQLException e) {
            throw new SystemException("SQL feil", SOURCE, e);
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
