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

import no.kantega.publishing.api.mailsubscription.MailSubscriptionAgent;
import no.kantega.publishing.api.mailsubscription.MailSubscriptionInterval;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.ScheduleLogAO;
import no.kantega.publishing.common.data.enums.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.List;

/**
 * Sends mailsubscriptions at different values of MailSubscriptionInterval.
 */
public class MailSubscriptionJob  {
    private static final Logger log = LoggerFactory.getLogger(MailSubscriptionJob.class);
    private static final String SOURCE = "aksess.jobs.MailSubscriptionJob";

    private List<MailSubscriptionAgent> mailSubscriptionAgents;

    private void executeInternal(MailSubscriptionInterval interval) {

        if (Aksess.getServerType() == ServerType.SLAVE) {
            log.info( "Job is disabled for server type slave");
            return;
        }

        boolean jobDisabled = Aksess.getConfiguration().getBoolean("mailsubscription.job.disabled", false);

        if(jobDisabled){
            log.info( "Mailsubscriptionjob disabled");
        }else{
            log.debug( "Looking for mailsubscriptions");

            Date previousRun = ScheduleLogAO.getLastRun(SOURCE + "-" + interval);
            ScheduleLogAO.setLastrun(SOURCE + "-" + interval, new Date());

            if (previousRun != null) {
                for (MailSubscriptionAgent agent : mailSubscriptionAgents) {
                    log.info( "Sending mailsubscriptions using agent " + agent.getClass().getSimpleName());
                    agent.emailNewContentSincePreviousDate(previousRun, interval);
                }
            }
        }
    }


    public void setMailSubscriptionAgents(List<MailSubscriptionAgent> mailSubscriptionAgents) {
        this.mailSubscriptionAgents = mailSubscriptionAgents;
    }

    @Scheduled(cron = "${mail.subscription.trigger.weekly}")
    public void mailSubscriptionJobWeekly(){
        executeInternal(MailSubscriptionInterval.weekly);
    }
    @Scheduled(cron = "${mail.subscription.trigger.daily}")
    public void mailSubscriptionTriggerDaily(){
        executeInternal(MailSubscriptionInterval.daily);
    }

    @Scheduled(cron = "${mail.subscription.trigger.immediate}")
    public void mailSubscriptionTriggerImmediate(){
        executeInternal(MailSubscriptionInterval.immediate);
    }
}
