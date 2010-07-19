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

package no.kantega.publishing.common.service.impl;

import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.common.data.BaseObject;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.User;
import no.kantega.commons.exception.SystemException;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.Date;

public class EventLog {
    public static final String SOURCE = "Aksess.EventLog";

    public static void log(SecuritySession securitySession, HttpServletRequest request, String event, String subject, BaseObject object) throws SystemException {
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

    public static void log(SecuritySession securitySession, HttpServletRequest request, String event, String subject) throws SystemException {
        log(securitySession, request, event, subject, null);
    }

    public static void log(String username, String remoteAddr, String event, String subject, BaseObject object) throws SystemException {

        if (event.length() > 255) {
            event = event.substring(0, 254);
        }
        if (subject != null && subject.length() > 255) {
            subject = subject.substring(0, 254);
        }

        if (username != null && username.length() > 255) {
            username = username.substring(0, 254);
        }

        int subjectType = -1;
        int subjectId = -1;
        if (object != null) {
            subjectType = object.getObjectType();
            subjectId = object.getId();
        }

        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement st = c.prepareStatement("insert into eventlog values(?,?,?,?,?,?,?)");
            st.setTimestamp(1, new java.sql.Timestamp(new Date().getTime()));
            st.setString(2, username);
            st.setString(3, event);
            st.setString(4, subject);
            st.setString(5, remoteAddr);
            st.setInt(6, subjectType);
            st.setInt(7, subjectId);
            st.execute();
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
    }
}
