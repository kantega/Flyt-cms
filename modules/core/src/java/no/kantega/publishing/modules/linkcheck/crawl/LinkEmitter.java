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

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.content.ContentAO;
import no.kantega.publishing.api.content.ContentHandler;
import no.kantega.publishing.api.link.LinkDao;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.util.Counter;
import no.kantega.publishing.eventlog.EventLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public class LinkEmitter {
    private LinkExtractor linkExtractor;

    private static final Logger log = LoggerFactory.getLogger(LinkEmitter.class);

    @Autowired
    private LinkDao linkDao;

    @Autowired
    private EventLog eventLog;

    @Autowired
    private ContentAO contentAO;

    @PostConstruct
    public void initLinkEmitter() {
        this.linkExtractor = new LinkExtractor(eventLog, contentAO);
    }

    public void emittLinks(final LinkHandler handler) {
        try {
            final Counter contentCount = new Counter();
            final Counter contentLinkCount = new Counter();
            final Counter attributeLinkCount = new Counter();
            long start = System.currentTimeMillis();

            contentAO.doForEachInContentList(new ContentQuery(), new ContentHandler() {
                public void handleContent(Content content) {

                    linkDao.deleteLinksForContentId(content.getId());

                    contentCount.increment();

                    try {
                        linkExtractor.extractLinks(content, new LinkHandler() {
                            public void contentLinkFound(Content content, String link) {
                                if(isValidLink(link)) {
                                    log.debug("Extracted {} from content with id {}", link, content.getId());
                                    contentLinkCount.increment();
                                    handler.contentLinkFound(content, link);
                                }
                            }

                            public void attributeLinkFound(Content content, String link, String attributeName) {
                                if(isValidLink(link)) {
                                    log.debug("Extracted {} from attribute {}", link, attributeName);
                                    attributeLinkCount.increment();
                                    handler.attributeLinkFound(content,  link, attributeName);
                                }
                            }

                        });
                    } catch (SystemException e) {
                        log.error("Error extracting links from Content " + content.getId() +" [" + content.getTitle() +"].", e);
                    }
                }
            });
            log.info("Found " + contentLinkCount.getI() +" page links and " +attributeLinkCount.getI() +" attribute links in " + contentCount.getI() +" pages in " + (System.currentTimeMillis() -start) +" ms.");
        } catch (SystemException e) {
            log.error("Excteption getting  content for link checking", e);
        }
    }
    public void emittLinksForContent(final LinkHandler handler, Content content){
        ContentHandler contentHandler = new ContentHandler() {
            @Override
            public void handleContent(Content content) {
                linkDao.deleteLinksForContentId(content.getId());

                try{
                    linkExtractor.extractLinks(content, new LinkHandler() {
                        public void contentLinkFound(Content content, String link) {
                            if(isValidLink(link)) {
                                log.debug("Extracted {} from content with id {}", link, content.getId());
                                handler.contentLinkFound(content, link);
                            }
                        }

                        public void attributeLinkFound(Content content, String link, String attributeName) {
                            if(isValidLink(link)) {
                                log.debug("Extracted {} from attribute {}", link, attributeName);
                                handler.attributeLinkFound(content,  link, attributeName);
                            }
                        }

                    });
                }catch (SystemException e){
                    log.error("Error extracting links from content "+content.getId() + " [" + content.getTitle() + "].",e);
                }
            }
        };
        contentHandler.handleContent(content);
    }
    private boolean isValidLink(String link) {
        return link.startsWith("http") || link.startsWith(Aksess.VAR_WEB);
    }

    public void setLinkDao(LinkDao linkDao) {
        this.linkDao = linkDao;
    }
}
