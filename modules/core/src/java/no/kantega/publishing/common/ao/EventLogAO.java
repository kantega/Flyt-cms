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

import com.google.gdata.util.common.base.Pair;
import no.kantega.publishing.api.model.BaseObject;
import no.kantega.publishing.eventlog.EventLog;
import no.kantega.publishing.eventlog.EventLogEntry;
import no.kantega.publishing.eventlog.EventLogQuery;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class EventLogAO extends JdbcDaoSupport implements EventLog {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${eventlog.enabled}")
    private boolean eventlogIsEnabled = false;

    public List<EventLogEntry> getQueryResult(EventLogQuery eventLogQuery) {
        Pair<String, List<Object>> queryAndArguments = buildEventLogQueryString(eventLogQuery);
        return getJdbcTemplate().query(queryAndArguments.first, new EventLogQueryMapper(), queryAndArguments.second.toArray());
    }

    public void log(SecuritySession securitySession, HttpServletRequest request, String event, String subject, BaseObject object) {
        if (eventlogIsEnabled) {
            User user = securitySession.getUser();

            String remoteAddr = "localhost";
            if (request != null) {
                remoteAddr = request.getRemoteAddr();
            }

            String username = "";
            if (user != null) {
                username = user.getName();
            }

            log(username, remoteAddr, event, subject, object);
        }
    }

    public void log(SecuritySession securitySession, HttpServletRequest request, String event, String subject) {
         log(securitySession, request, event, subject, null);
    }

    public void log(String username, String remoteAddr, String event, String subject, BaseObject object) {
        if (eventlogIsEnabled) {
            if (event.length() > 255) {
                event = event.substring(0, 254);
            }
            if (subject != null && subject.length() > 255) {
                subject = subject.substring(0, 254);
            }

            if (username != null && username.length() > 255) {
                username = username.substring(0, 254);
            }

            if(subject != null && subject.length() > 255){
                subject = subject.substring(0, 254);
            }

            int subjectType = -1;
            int subjectId = -1;
            if (object != null) {
                subjectType = object.getObjectType();
                subjectId = object.getId();
            }
            try {
                getJdbcTemplate().update("insert into eventlog values(?,?,?,?,?,?,?)", new Date(), username, event, subject, remoteAddr, subjectType, subjectId);
            } catch (Exception e) {
                log.error("Tried to insert {}, {}, {}, {}, {}, {}, {}", new Date(), username, event, subject, remoteAddr, subjectType, subjectId);
                log.error("Error inserting ", e);
            }
        }
    }

    private Pair<String, List<Object>> buildEventLogQueryString(EventLogQuery eventLogQuery) {
        StringBuilder where = new StringBuilder("select * from eventlog where");
        List<Object> arguments = new ArrayList<>();
        if (eventLogQuery.getFrom() != null) {
            where.append(" Time >= ?");
            arguments.add(eventLogQuery.getFrom());
        }
        if (eventLogQuery.getTo() != null) {
            if (where.length() > 0) where.append(" and");
            where.append(" Time <= ?");
            arguments.add(eventLogQuery.getTo());
        }
        if (isNotBlank(eventLogQuery.getUserId())) {
            if (where.length() > 0) where.append(" and");
            where.append(" UserId like ?");
            arguments.add("%" + eventLogQuery.getUserId() + "%");
        }
        if (isNotBlank(eventLogQuery.getSubjectName())) {
            if (where.length() > 0) where.append(" and");
            where.append(" SubjectName like ?");
            arguments.add("%" + eventLogQuery.getSubjectName()+ "%");
        }
        if (eventLogQuery.getSubjectType() != -1) {
            if (where.length() > 0) where.append(" and");
            where.append(" SubjectType = ?");
            arguments.add(eventLogQuery.getSubjectType());
        }
        if (isNotBlank(eventLogQuery.getEventName())) {
            if (where.length() > 0) where.append(" and");
            where.append(" EventName like ?");
            arguments.add("%" + eventLogQuery.getEventName() + "%");
        }
        where.append(" order by Time desc");
        return new Pair<>(where.toString(), arguments);
    }

    private class EventLogQueryMapper implements RowMapper<EventLogEntry> {
        public EventLogEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
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
            return event;
        }
    }
}
