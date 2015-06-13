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

package no.kantega.publishing.modules.linkcheck.crawl;

import no.kantega.publishing.api.link.LinkDao;
import no.kantega.publishing.api.runtime.ServerType;
import no.kantega.publishing.api.scheduling.DisableOnServertype;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.modules.linkcheck.check.LinkCheckerJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class LinkCrawlerJob {
    private static final Logger log = LoggerFactory.getLogger(LinkCrawlerJob.class);

    private LinkCheckerJob checker;

    @Autowired
    private LinkDao linkDao;

    @Autowired
    private LinkEmitter emitter;

    @Scheduled(cron = "${jobs.linkCrawl.trigger}")
    @DisableOnServertype(ServerType.SLAVE)
    public void runLinkCrawler() {
        if(!Aksess.isLinkCheckerEnabled()) {
            return;
        }
        log.info("Executing LinkCrawlerJob");

        linkDao.saveAllLinks(emitter);
        log.info("Saved all links");
        checker.runLinkChecker();

        log.info("Execution of LinkCrawlerJob finished");
    }

    public void setChecker(LinkCheckerJob checker) {
        this.checker = checker;
    }

}
