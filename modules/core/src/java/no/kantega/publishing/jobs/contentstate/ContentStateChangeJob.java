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

package no.kantega.publishing.jobs.contentstate;


import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.enums.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class ContentStateChangeJob  {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ContentStateUpdater stateUpdater;

    @Scheduled(cron = "${jobs.contentstatechange.trigger:0 0/5 * * * ?}")
    public void execute() {
        if (Aksess.getServerType() == ServerType.SLAVE) {
            log.info( "Job is disabled for server type slave");
            return;
        }
        stateUpdater.expireContent();
        stateUpdater.publishContent();
    }
}
