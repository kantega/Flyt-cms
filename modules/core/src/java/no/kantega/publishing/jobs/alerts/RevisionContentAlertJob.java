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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.data.SortOrder;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.data.enums.ServerType;
import no.kantega.publishing.content.api.ContentAO;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.security.realm.SecurityRealm;
import no.kantega.publishing.security.realm.SecurityRealmFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class RevisionContentAlertJob {
    private static final Logger log = LoggerFactory.getLogger(RevisionContentAlertJob.class);
    private ContentAlertListener[] listeners;
    private int daysBeforeWarning = 30;

    @Autowired
    private ContentAO contentAO;

    public void execute() {

        if (Aksess.getServerType() == ServerType.SLAVE) {
            log.info( "Job is disabled for server type slave");
            return;
        }
        
        try {
            log.debug( "Looking for content revision in " + daysBeforeWarning + " days");

            Multimap<String, Content> users = ArrayListMultimap.create();

            // Create query to find all docs with revision
            ContentQuery query = new ContentQuery();

            Date fromDate = new Date();
            query.setRevisionDateFrom(fromDate);

            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE, 30);
            query.setRevisionDateTo(calendar.getTime());

            SortOrder sort = new SortOrder(ContentProperty.TITLE, false);

            List<Content> contentList = contentAO.getContentList(query, -1, sort, false);

            // Insert docs into hashmap
            for (Content content : contentList) {
                String userId = content.getOwnerPerson();
                if (isNotBlank(userId)) {
                    users.put(userId, content);
                }
            }

            // Iterate through users
            for (String userId : users.keySet()) {
                Collection<Content> userContentList = users.get(userId);

                // Lookup user with userid
                SecurityRealm realm = SecurityRealmFactory.getInstance();
                User ownerPerson = realm.lookupUser(userId);

                // Send message using listeners
                if (ownerPerson != null) {
                    log.info("Sending alert to user " + ownerPerson.getId() + " - " + userContentList.size() + " docs for revision");
                    for (ContentAlertListener listener : listeners) {
                        listener.sendContentAlert(ownerPerson, new ArrayList<>(userContentList));
                    }
                } else {
                    log.debug("Skipping alert, user unknown " + userId + " - " + userContentList.size() + " docs for revision");
                }
            }

        } catch (SystemException e) {
            log.error("Error when seding revision alert", e);
        }

    }

    public void setListeners(ContentAlertListener[] listeners) {
        this.listeners = listeners;
    }

    public void setDaysBeforeWarning(Integer days) {
        daysBeforeWarning = days;
    }
}


