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
import no.kantega.publishing.common.data.EventLogEntry;
import no.kantega.commons.exception.SystemException;

import java.util.List;
import java.util.Date;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EventLogAO {
    private static final String SOURCE = "aksess.SearchAO";

    public static List search(Date from, Date end, String userId, String subjectName, String eventName) throws SystemException {
        List events = new ArrayList();

        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();
            String where = "";
            if (from != null) {
                where += " where Time >= ?";
            }
            if (end != null) {
                if (where.length() == 0) where += " where ";
                else where += " and ";
                where += " Time <= ?";
            }
            if (userId != null && userId.length() > 0) {
                if (where.length() == 0) where += " where ";
                else where += " and ";
                where += " UserId like ?";
            }
            if (subjectName != null && subjectName.length() > 0) {
                if (where.length() == 0) where += " where ";
                else where += " and ";
                where += " SubjectName like ?";
            }
            if (eventName != null && eventName.length() > 0) {
                if (where.length() == 0) where += " where ";
                else where += " and ";
                where += " EventName like ?";
            }

            PreparedStatement st = c.prepareStatement("select * from eventlog " + where + " order by Time desc");
            int p = 1;
            if (from != null) {
                st.setDate(p++, new java.sql.Date(from.getTime()));
            }
            if (end != null) {
                st.setDate(p++, new java.sql.Date(end.getTime()));
            }
            if (userId != null && userId.length() > 0) {
                st.setString(p++, "%" + userId + "%");
            }
            if (subjectName != null && subjectName.length() > 0) {
                st.setString(p++,  "%" + subjectName + "%");
            }
            if (eventName != null && eventName.length() > 0) {
                st.setString(p++,  "%" + eventName + "%");
            }
            ResultSet rs = st.executeQuery();
            while(rs.next()) {
                EventLogEntry event = new EventLogEntry();
                event.setTime(rs.getTimestamp("Time"));
                event.setUserId(rs.getString("UserId"));
                if (event.getUserId() == null) {
                    event.setUserId("");
                }
                event.setEventName(rs.getString("EventName"));
                event.setSubjectName(rs.getString("SubjectName"));
                event.setRemoteAddress(rs.getString("RemoteAddress"));
                event.setSubjectType(rs.getInt("SubjectType"));
                event.setSubjectId(rs.getInt("SubjectId"));
                events.add(event);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            throw new SystemException("SQL Feil", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {

            }
        }

        return events;
    }
}
