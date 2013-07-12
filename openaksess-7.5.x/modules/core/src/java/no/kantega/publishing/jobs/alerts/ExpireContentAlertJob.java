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
import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.data.SortOrder;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.data.enums.ExpireAction;
import no.kantega.publishing.common.data.enums.ServerType;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.security.realm.SecurityRealm;
import no.kantega.publishing.security.realm.SecurityRealmFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ExpireContentAlertJob {
    private ContentAlertListener[] listeners;
    private int daysBeforeWarning = 14;
    private static String SOURCE = "ExpireContentAlertJob";

    @Autowired
    private SiteCache siteCache;

    public void execute() {

        if (Aksess.getServerType() == ServerType.SLAVE) {
            Log.info(SOURCE, "Job is disabled for server type slave", null, null);
            return;
        }
        
        try {
            Log.info(SOURCE, "Looking for content will expire in less than " + daysBeforeWarning + " days", null, null);


            List<Site> sites = siteCache.getSites();
            for (Site site : sites) {
                String alias = ".";
                if (site != null && !site.getAlias().equals("/")) {
                    alias = site.getAlias();
                    alias = alias.replace('/', '.');
                }

                ContentQuery query = new ContentQuery();

                Date fromDate = new Date();
                query.setExpireDateFrom(fromDate);

                Calendar calendar = new GregorianCalendar();
                calendar.add(Calendar.DATE, daysBeforeWarning);
                query.setExpireDateTo(calendar.getTime());
                query.setSiteId(site.getId());

                SortOrder sort = new SortOrder(ContentProperty.TITLE, false);
                List<Content> contentList = ContentAO.getContentList(query, -1, sort, false);

                String defaultUserEmail = null;

                try {
                    Configuration config = Aksess.getConfiguration();
                    defaultUserEmail = config.getString("mail" + alias + "contentexpire.recipient");
                } catch (ConfigurationException e) {
                    Log.error(SOURCE, e, null, null);
                }

                Multimap<String, Content> users = ArrayListMultimap.create();

                // Insert docs into hashmap
                for (Content content : contentList) {

                    if (content.getExpireAction() == ExpireAction.REMIND) {
                        String userId;
                        if (defaultUserEmail != null && defaultUserEmail.contains("@")) {
                            userId = defaultUserEmail;
                        } else {
                            if (content.getOwnerPerson() != null && content.getOwnerPerson().length() > 0) {
                                userId = content.getOwnerPerson();
                            } else {
                                userId = content.getModifiedBy();
                            }
                        }
                        if (isNotBlank(userId)) {
                            users.put(userId, content);
                        }
                    }
                }

                // Iterate through users
                for (Map.Entry<String, Collection<Content>> entry : users.asMap().entrySet()) {
                    String userId = entry.getKey();
                    User user = getUser(userId);


                    // Send message using listeners
                    List<Content> userContentList = new ArrayList<>(entry.getValue());
                    if (user != null) {
                        Log.info(SOURCE, "Sending alert to user " + user.getId() + " - " + userContentList.size() + " docs about to expire", null, null);
                        for (ContentAlertListener listener : listeners) {
                            listener.sendContentAlert(user, userContentList);
                        }
                    } else {
                        Log.info(SOURCE, "Skipping alert, user unknown " + userId + " - " + userContentList.size() + " docs about to expire", null, null);
                    }
                }


            }

        } catch (SystemException e) {
            Log.error(SOURCE, e, null, null);
        }

    }

    private User getUser(String userId) {
        User user = null;
        if (userId.contains("@")) {
            user = new User();
            user.setId(userId);
            user.setEmail(userId);
        } else {
            // Lookup user with userid
            SecurityRealm realm = SecurityRealmFactory.getInstance();
            user = realm.lookupUser(userId);
        }
        return user;
    }

    public void setListeners(ContentAlertListener[] listeners) {
        this.listeners = listeners;
    }

    public void setDaysBeforeWarning(Integer days) {
        daysBeforeWarning = days.intValue();
    }
}


