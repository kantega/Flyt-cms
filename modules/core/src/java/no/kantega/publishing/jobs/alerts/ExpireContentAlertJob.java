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
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.data.SortOrder;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.data.enums.ExpireAction;
import no.kantega.publishing.common.data.enums.ServerType;
import no.kantega.publishing.content.api.ContentAO;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.security.realm.SecurityRealm;
import no.kantega.publishing.security.realm.SecurityRealmFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ExpireContentAlertJob {
    private static final Logger log = LoggerFactory.getLogger(ExpireContentAlertJob.class);
    private ContentAlertListener[] listeners;
    private int daysBeforeWarning = 14;

    @Autowired
    private SiteCache siteCache;

    @Autowired
    private ContentAO contentAO;

    @Scheduled(cron = "${jobs.expirecontent.trigger}")
    public void execute() {

        if (Aksess.getServerType() == ServerType.SLAVE) {
            log.info( "Job is disabled for server type slave");
            return;
        }
        
        try {
            log.info( "Looking for content will expire in less than " + daysBeforeWarning + " days");


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
                List<Content> contentList = contentAO.getContentList(query, -1, sort, false);

                Configuration config = Aksess.getConfiguration();
                String defaultUserEmail = config.getString("mail" + alias + "contentexpire.recipient");


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
                        log.info( "Sending alert to user " + user.getId() + " - " + userContentList.size() + " docs about to expire");
                        for (ContentAlertListener listener : listeners) {
                            listener.sendContentAlert(user, userContentList);
                        }
                    } else {
                        log.info( "Skipping alert, user unknown " + userId + " - " + userContentList.size() + " docs about to expire");
                    }
                }


            }

        } catch (SystemException e) {
            log.error("", e);
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
        daysBeforeWarning = days;
    }
}


