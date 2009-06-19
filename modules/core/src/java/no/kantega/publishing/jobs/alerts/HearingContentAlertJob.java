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

package no.kantega.publishing.jobs.alerts;

import no.kantega.commons.log.Log;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.ao.HearingAO;
import no.kantega.publishing.common.ao.ScheduleLogAO;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.security.realm.SecurityRealm;
import no.kantega.publishing.security.realm.SecurityRealmFactory;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.spring.RootContext;
import no.kantega.publishing.org.OrganizationManager;
import no.kantega.publishing.org.OrgUnit;
import no.kantega.publishing.org.UserCallbackHandler;

import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.context.ApplicationContext;

/**
 * User: Anders Skar, Kantega AS
 * Date: May 14, 2007
 * Time: 12:49:27 PM
 */
public class HearingContentAlertJob {
    private ContentAlertListener[] listeners;

    private static String SOURCE = "aksess.jobs.HearingContentAlertJob";

    public void execute() {


        Log.debug(SOURCE, "Looking for hearings", null, null);
        Connection c = null;
        try {
            Date previousRun = ScheduleLogAO.getLastRun(SOURCE);
            Date thisRun = new Date();

            c = dbConnectionFactory.getConnection();

            if (previousRun != null) {
                List contentList = new ArrayList();

                // Finn alle dokumenter som er satt til høring siden forrige gang
                PreparedStatement st = c.prepareStatement("select distinct ContentId from contentversion where status = ? and lastmodified >= ?");
                st.setInt(1, ContentStatus.HEARING);
                st.setTimestamp(2, new java.sql.Timestamp(previousRun.getTime()));
                ResultSet rs = st.executeQuery();
                while(rs.next()) {
                    int contentId = rs.getInt("ContentId");
                    ContentIdentifier cid = new ContentIdentifier(contentId);
                    cid.setStatus(ContentStatus.HEARING);
                    Content content = ContentAO.getContent(cid, true);
                    if (content != null) {
                        contentList.add(content);
                    }
                }

                Map contentLists = getHearingUsers(contentList);
                Iterator usersIt = contentLists.keySet().iterator();

                while (usersIt.hasNext()) {
                    String userId =  (String)usersIt.next();

                    // Finn brukeren
                    SecurityRealm realm = SecurityRealmFactory.getInstance();
                    User user = realm.lookupUser(userId);

                    // Send ut meldinger
                    if (user != null) {
                        List userContentList = (List)contentLists.get(userId);
                        for (int j = 0; j < listeners.length; j++) {
                            ContentAlertListener listener = listeners[j];
                            listener.sendContentAlert(user, userContentList);
                        }
                    }
                }
            }

            ScheduleLogAO.setLastrun(SOURCE, thisRun);

        } catch (SystemException e) {
            Log.error(SOURCE, e, null, null);
        } catch (SQLException e) {
            Log.error(SOURCE, e, null, null);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
                Log.error(SOURCE, e, null, null);
            }
        }
    }

    private Map getHearingUsers(List contentList) throws SystemException {
        Map users = new HashMap();
        OrganizationManager orgManager = null;

        // Legg til organisasjonsenheter
        ApplicationContext context = RootContext.getInstance();
        Iterator it = context.getBeansOfType(OrganizationManager.class).values().iterator();
        if(it.hasNext()) {
            orgManager = (OrganizationManager) it.next();
        }

        for (int i = 0; i < contentList.size(); i++) {
            Content content = (Content) contentList.get(i);
            Hearing hearing = HearingAO.getHearingByContentVersion(content.getVersionId());
            if (hearing != null) {
                // Legg til direkte inviterte
                List persons = HearingAO.getPersonInviteesForHearing(hearing.getId());
                for (int j = 0; j < persons.size(); j++) {
                    HearingInvitee invitee =  (HearingInvitee)persons.get(j);
                    String userId = invitee.getReference();
                    List list = (List)users.get(userId);
                    if (list == null) {
                        list = new ArrayList();
                        list.add(content);
                        users.put(userId, list);
                    } else {
                        list.add(content);
                    }
                    Log.debug(SOURCE, "Hearing for userid:" + userId, null, null);
                }

                if (orgManager != null) {
                    List orgs = HearingAO.getOrgUnitInviteesForHearing(hearing.getId());

                    ContentListUsercallbackHandler cb = new ContentListUsercallbackHandler();
                    cb.setUsers(users);
                    cb.setContent(content);

                    for (int j = 0; j < orgs.size(); j++) {
                        HearingInvitee invitee =  (HearingInvitee)orgs.get(j);

                        Log.debug(SOURCE, "Hearing for orgunit:" + invitee.getReference(), null, null);

                        OrgUnit unit = orgManager.getUnitByExternalId(invitee.getReference());
                        if (unit != null) {
                            orgManager.doForUsersInOrgUnit(unit, cb);
                        }
                    }
                }
            }
        }

        return users;
    }



    public void setListeners(ContentAlertListener[] listeners) {
        this.listeners = listeners;
    }
}

class ContentListUsercallbackHandler implements UserCallbackHandler {
    private Map users = null;
    private Content content = null;

    public void handleUser(String user) {
        List contentList = (List)users.get(user);
        if (contentList == null) {
            contentList = new ArrayList();
            contentList.add(content);
            users.put(user, contentList);
        } else {
            contentList.add(content);
        }
    }

    public void setUsers(Map users) {
        this.users = users;
    }

    public void setContent(Content content) {
        this.content = content;
    }
}

