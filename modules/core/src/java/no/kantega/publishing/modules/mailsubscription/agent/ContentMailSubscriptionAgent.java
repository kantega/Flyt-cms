/*
 * Copyright 2013 Kantega AS
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

package no.kantega.publishing.modules.mailsubscription.agent;

import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.mailsubscription.MailSubscription;
import no.kantega.publishing.api.mailsubscription.MailSubscriptionAgent;
import no.kantega.publishing.api.mailsubscription.MailSubscriptionInterval;
import no.kantega.publishing.api.mailsubscription.MailSubscriptionService;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.data.SortOrder;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.security.data.Role;
import no.kantega.publishing.security.data.enums.Privilege;
import no.kantega.publishing.security.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Class for sending mail subscriptions notifying users about new content (for newsletters etc)
 */
public class ContentMailSubscriptionAgent implements MailSubscriptionAgent {
    private static final Logger log = LoggerFactory.getLogger(ContentMailSubscriptionAgent.class);
    private static final String SOURCE = "aksess.MailSubscriptionAgent";

    private MailSubscriptionDeliveryService mailSubscriptionDeliveryService;

    @Autowired
    private MailSubscriptionService mailSubscriptionService;

    @Autowired
    private SiteCache siteCache;

    @Override
    public void emailNewContentSincePreviousDate(Date previousRun, MailSubscriptionInterval interval) {
        if (previousRun != null) {
            // Send ut epost med alle nye meldinger
            boolean groupEmails = false;
            try {
                groupEmails = Aksess.getConfiguration().getBoolean("mail.subscription.groupemails", false);
            } catch (ConfigurationException e) {
                log.info( "Unable to read configuration value for 'mail.subscription.groupemails'");
            }

            if (groupEmails) {
                // Send en epost for alle sites
                log.debug( "Sending mailsubscriptions for all sites");
                emailNewContentForSite(previousRun, interval, null);
            } else {
                // Send en epost for hver site
                List<Site> sites = siteCache.getSites();
                for (Site site : sites) {
                    log.debug( "Sending mailsubscriptions for site:  " + site.getName());
                    emailNewContentForSite(previousRun, interval, site);
                }
            }
        }
    }


    private void emailNewContentForSite(Date previousRun, MailSubscriptionInterval interval, Site site) {
        ContentQuery query = new ContentQuery();
        query.setPublishDateFrom(previousRun);
        if (site != null) {
            query.setSiteId(site.getId());
        }

        List<Content> allContentList = ContentAO.getContentList(query, -1, new SortOrder(ContentProperty.PUBLISH_DATE, false), true);

        // This job only sends notificiation about content which is viewable by everyone, all protected content is excluded
        List<Content> contentList = new ArrayList<>();
        Role everyone = new Role();
        everyone.setId(Aksess.getEveryoneRole());

        boolean sendProtectedContent = false;
        try {
            sendProtectedContent = Aksess.getConfiguration().getBoolean("mail.subscription.sendprotectedcontent", false);
        } catch (ConfigurationException e) {
            log.info( "Unable to read configuration value for 'mail.subscription.sendprotectedcontent'");
        }

        for (Content content : allContentList) {
            if (sendProtectedContent || SecurityService.isAuthorized(everyone, content, Privilege.VIEW_CONTENT)) {
                contentList.add(content);
                log.debug( "New content:" + content.getTitle());
            } else {
                log.info( "Content was not sent due to permissions:" + content.getTitle() + " (set mail.subscription.sendprotectedcontent=true to send all content)");
            }
        }


        // Cut descriptions and remove tags
        for (Content content : contentList) {
            String body = content.getDescription();
            body = body.replaceAll("<(.|\\n)+?>", "");
            if (body.length() > 400) {
                body = body.substring(0, 399) + "...";
            }
            content.setDescription(body);
        }

        if (contentList.size() > 0) {
            List<MailSubscription> subscriptions = mailSubscriptionService.getMailSubscriptionByInterval(interval);

            sendEmail(contentList, subscriptions, site);
        }

    }


    /**
     *
     * @param content - List of new content
     * @param subscriptions - list of subscriptions
     * @param site - Which site to send newsletter for
     * @throws ConfigurationException -
     * @throws SystemException -
     */
    protected void sendEmail(List<Content> content, List<MailSubscription> subscriptions, Site site) {
        Map<String, List<Content>> subscribers = new HashMap<>();

        for (MailSubscription subscription : subscriptions) {
            String email = subscription.getEmail();
            if (email.contains("@")) {
                List<Content> subscriberContent = subscribers.get(email);
                if (subscriberContent == null) {
                    subscriberContent = new ArrayList<Content>();
                    subscribers.put(email, subscriberContent);
                }

                for (Content c : content) {
                    // Check if user subscribes to content
                    if (isSubscriptionMatch(subscription, c, site)) {
                        subscriberContent.add(c);
                    }
                }
            }
        }

        for (String email : subscribers.keySet()) {
            // Send email to this user
            List<Content> subscriberContent = subscribers.get(email);
            if (subscriberContent != null && subscriberContent.size() > 0) {
                try {
                    mailSubscriptionDeliveryService.sendEmail(email, subscriberContent, site);
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }
    }

    /**
     *  Return true iff at least one of the associations for the given Content matches the MailSubscription criteria
     *
     */
    protected boolean isSubscriptionMatch(MailSubscription subscription, Content c, Site site) {
        List<Association> associations = c.getAssociations();
        for (Association a : associations) {
            if (site == null || site.getId() == a.getSiteId()) {
                // Correct site
                if (isSubscriptionMatchForAssociation(subscription, c, a)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return true if all specified criteria match for an association.
     *
     */
    protected boolean isSubscriptionMatchForAssociation(MailSubscription subscription, Content c, Association a) {
        // We don't support matching "anything"
        if(subscription.getChannel() <= 0 && subscription.getDocumenttype() <= 0) {
            return false;
        }
        // A specified channel should match
        if(subscription.getChannel() > 0 && subscription.getChannel() != a.getParentAssociationId()) {
            return false;
        }
        // A specified document type should match
        return !(subscription.getDocumenttype() > 0 && subscription.getDocumenttype() != c.getDocumentTypeId());

    }


    public void setMailSubscriptionDeliveryService(MailSubscriptionDeliveryService mailSubscriptionDeliveryService) {
        this.mailSubscriptionDeliveryService = mailSubscriptionDeliveryService;
    }

}
