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

import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.cache.SiteCache;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.data.Site;
import no.kantega.publishing.common.data.SortOrder;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.data.enums.ExpireAction;
import no.kantega.publishing.common.data.enums.ServerType;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.security.realm.SecurityRealm;
import no.kantega.publishing.security.realm.SecurityRealmFactory;

import java.util.*;

public class ExpireContentAlertJob {
    private ContentAlertListener[] listeners;
    private int daysBeforeWarning = 14;
    private static String SOURCE = "ExpireContentAlertJob";

    public void execute() {

        if (Aksess.getServerType() == ServerType.SLAVE) {
            Log.info(SOURCE, "Job is disabled for server type slave", null, null);
            return;
        }
        
        try {
            Log.info(SOURCE, "Looking for content will expire in less than " + daysBeforeWarning + " days", null, null);


            List sites = SiteCache.getSites();
            for (int i = 0; i < sites.size(); i++) {
                Site site = (Site)sites.get(i);
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
                List contentList = ContentAO.getContentList(query, -1, sort, false);

                String defaultUserEmail = null;

                try {
                    Configuration config = Aksess.getConfiguration();
                    defaultUserEmail = config.getString("mail" + alias + "contentexpire.recipient");
                } catch (ConfigurationException e) {
                    Log.error(SOURCE, e, null, null);
                }

                Map users = new HashMap();

                // Insert docs into hashmap
                for (int j = 0; j < contentList.size(); j++) {
                    Content content = (Content)contentList.get(j);

                    if (content.getExpireAction() == ExpireAction.REMIND) {
                        String userId;
                        if (defaultUserEmail != null && defaultUserEmail.indexOf("@") != -1) {
                            userId = defaultUserEmail;
                        } else {
                            if (content.getOwnerPerson() != null && content.getOwnerPerson().length() > 0) {
                                userId = content.getOwnerPerson();
                            } else {
                                userId = content.getModifiedBy();
                            }
                        }
                        if (userId != null && userId.length() > 0) {
                            List userContentList = (List)users.get(userId);
                            if (userContentList == null) {
                                userContentList =  new ArrayList();
                                users.put(userId, userContentList);
                            }
                            userContentList.add(content);
                        }
                    }
                }

                // Iterate through users
                Iterator it = users.keySet().iterator();
                while (it.hasNext()) {
                    String userId = (String)it.next();

                    List userContentList = (List)users.get(userId);

                    User user = null;
                    if (userId.indexOf("@") != -1) {
                        user = new User();
                        user.setId(userId);
                        user.setEmail(userId);
                    } else {
                        // Lookup user with userid
                        SecurityRealm realm = SecurityRealmFactory.getInstance();
                        user = realm.lookupUser(userId);
                    }


                    // Send message using listeners
                    if (user != null) {
                        Log.info(SOURCE, "Sending alert to user " + user.getId() + " - " + userContentList.size() + " docs about to expire", null, null);
                        for (int j = 0; j < listeners.length; j++) {
                            ContentAlertListener listener = listeners[j];
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

    public void setListeners(ContentAlertListener[] listeners) {
        this.listeners = listeners;
    }

    public void setDaysBeforeWarning(Integer days) {
        daysBeforeWarning = days.intValue();
    }
}


