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

import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.data.SortOrder;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.security.realm.SecurityRealmFactory;
import no.kantega.publishing.security.realm.SecurityRealm;
import no.kantega.commons.log.Log;
import no.kantega.commons.exception.SystemException;

import java.util.*;

public class RevisionContentAlertJob {
    private ContentAlertListener[] listeners;
    private int daysBeforeWarning = 30;
    private static String SOURCE = "RevisionContentAlertJob";

    public void execute() {

        try {
            Log.debug(SOURCE, "Looking for content revision in " + daysBeforeWarning + " days", null, null);

            Map users = new HashMap();

            // Create query to find all docs with revision
            ContentQuery query = new ContentQuery();

            Date fromDate = new Date();
            query.setRevisionDateFrom(fromDate);

            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE, 30);
            query.setRevisionDateTo(calendar.getTime());

            SortOrder sort = new SortOrder(ContentProperty.TITLE, false);

            List contentList = ContentAO.getContentList(query, -1, sort, false);

            // Insert docs into hashmap
            for (int i = 0; i < contentList.size(); i++) {
                Content content = (Content)contentList.get(i);

                String userId = content.getOwnerPerson();
                if (userId != null && userId.length() > 0) {
                    List userContentList = (List)users.get(userId);
                    if (userContentList == null) {
                        userContentList =  new ArrayList();
                        users.put(userId, userContentList);
                    }
                    userContentList.add(content);
                }
            }

            // Iterate through users
            Iterator it = users.keySet().iterator();
            while (it.hasNext()) {
                String userId = (String)it.next();

                List userContentList = (List)users.get(userId);

                // Lookup user with userid
                SecurityRealm realm = SecurityRealmFactory.getInstance();
                User ownerPerson = realm.lookupUser(userId);

                // Send message using listeners
                if (ownerPerson != null) {
                    Log.debug(SOURCE, "Sending alert to user " + ownerPerson.getId() + " - " + userContentList.size() + " docs for revision", null, null);
                    for (int j = 0; j < listeners.length; j++) {
                        ContentAlertListener listener = listeners[j];
                        listener.sendContentAlert(ownerPerson, userContentList);
                    }
                } else {
                    Log.debug(SOURCE, "Skipping alert, user unknown " + userId + " - " + userContentList.size() + " docs for revision", null, null);
                }
            }

        } catch (SystemException e) {
            Log.error(SOURCE, e, null, null);
        }

    }

    public void setListeners(ContentAlertListener[] listeners) {
        this.listeners = listeners;
    }

    public void setDaysBeforeWarning(Integer days) {
        daysBeforeWarning = days.intValue();
    }
}


