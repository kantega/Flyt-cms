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

package no.kantega.publishing.jobs.mailsubscription;

import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.ScheduleLogAO;
import no.kantega.publishing.common.cache.SiteCache;
import no.kantega.publishing.common.data.Site;
import no.kantega.publishing.common.data.enums.ServerType;
import no.kantega.publishing.modules.mailsubscription.agent.MailSubscriptionAgent;
import no.kantega.publishing.modules.mailsubscription.data.MailSubscription;
import org.quartz.StatefulJob;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class MailSubscriptionJob extends QuartzJobBean implements StatefulJob {
    private static final String SOURCE = "aksess.jobs.MailSubscriptionJob";

    // Denne jobben kjøres ved ulike intervall for å sende ut meldinger, trenger å vite hvilket intervall
    // dette er for å sende ut til de rette personene som skal ha varsling f.eks daglig, ukentlig osv.
    private String interval = MailSubscription.IMMEDIATE;

    protected void executeInternal(org.quartz.JobExecutionContext jobExecutionContext) throws org.quartz.JobExecutionException {

        if (Aksess.getServerType() == ServerType.SLAVE) {
            Log.info(SOURCE, "Job is disabled for server type slave", null, null);
            return;
        }

        boolean jobDisabled = false;
        try {
            jobDisabled = Aksess.getConfiguration().getBoolean("mailsubscription.job.disabled", false);
        } catch (ConfigurationException e) {
            Log.debug(SOURCE, "Unable to read aksess configuration", null, null);
        }
        if(jobDisabled){
            Log.debug(SOURCE, "Mailsubscriptionjob disabled", null, null);
        }else{
            Log.debug(SOURCE, "Looking for mailsubscriptions", null, null);
            try {
                Configuration config = Aksess.getConfiguration();

                Date previousRun = ScheduleLogAO.getLastRun(SOURCE + "-" + interval);
                ScheduleLogAO.setLastrun(SOURCE + "-" + interval, new Date());

                if (previousRun != null) {
                    // Send ut epost med alle nye meldinger
                    MailSubscriptionAgent agent = new MailSubscriptionAgent();

                    boolean groupEmails = config.getBoolean("mail.subscription.groupemails", false);

                    if (groupEmails) {
                        // Send en epost for alle sites
                        Log.debug(SOURCE, "Sending mailsubscriptions for all sites", null, null);
                        agent.emailNewContentSincePreviousDate(previousRun, interval, null);
                    } else {
                        // Send en epost for hver site
                        List sites = SiteCache.getSites();
                        for (int i = 0; i < sites.size(); i++) {
                            Site site = (Site) sites.get(i);
                            Log.debug(SOURCE, "Sending mailsubscriptions for site:  " + site.getName(), null, null);
                            agent.emailNewContentSincePreviousDate(previousRun, interval, site);
                        }
                    }
                }
            } catch (SystemException e) {
                Log.error(SOURCE, e, null, null);
            } catch (SQLException e) {
                Log.error(SOURCE, e, null, null);
            } catch (ConfigurationException e) {
                Log.error(SOURCE, e, null, null);
            }
        }
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }
}
