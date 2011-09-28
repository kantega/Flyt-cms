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

import no.kantega.publishing.common.data.EventLogEntry;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventLogAO {

    private DataSource dataSource;

    public Query createQuery() {
        return new Query();
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public class Query {
        private Date from;
        private Date to;
        private String userId;
        private String subjectName;
        private int subjectType = -1;
        private String eventName;

        public Query setFrom(Date from) {
            this.from = from;
            return this;
        }

        public Query setTo(Date to) {
            this.to = to;
            return this;
        }

        public Query setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Query setSubjectName(String subjectName) {
            this.subjectName = subjectName;
            return this;
        }

        public Query setEventName(String eventName) {
            this.eventName = eventName;
            return this;
        }

        public Query setSubjectType(int subjectType) {
            this.subjectType = subjectType;
            return this;
        }

        public List<EventLogEntry> list() {
            List<EventLogEntry> events = new ArrayList<EventLogEntry>();

            Connection c = null;
            try {
                c = dataSource.getConnection();
                String where = "";
                if (from != null) {
                    where += " where Time >= ?";
                }
                if (to != null) {
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
                if (subjectType != -1) {
                    if (where.length() == 0) where += " where ";
                    else where += " and ";
                    where += " SubjectType = ?";
                }
                if (eventName != null && eventName.length() > 0) {
                    if (where.length() == 0) where += " where ";
                    else where += " and ";
                    where += " EventName like ?";
                }

                PreparedStatement st = c.prepareStatement("select * from eventlog " + where + " order by Time desc");
                int p = 1;
                if (from != null) {
                    st.setTimestamp(p++, new Timestamp(from.getTime()));
                }
                if (to != null) {
                    st.setTimestamp(p++, new Timestamp(to.getTime()));
                }
                if (userId != null && userId.length() > 0) {
                    st.setString(p++, "%" + userId + "%");
                }
                if (subjectName != null && subjectName.length() > 0) {
                    st.setString(p++, "%" + subjectName + "%");
                }

                if (subjectType != -1 ) {
                    st.setInt(p++, subjectType);
                }
                if (eventName != null && eventName.length() > 0) {
                    st.setString(p, "%" + eventName + "%");
                }
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
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
                throw new RuntimeException(e);
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
}
