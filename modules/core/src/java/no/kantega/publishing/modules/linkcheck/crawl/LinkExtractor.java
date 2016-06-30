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
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.attribute.AttributeDataType;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.*;
import no.kantega.publishing.eventlog.Event;
import no.kantega.publishing.eventlog.EventLog;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class LinkExtractor {
    private static final Logger log = LoggerFactory.getLogger(LinkExtractor.class);

    private final EventLog eventLog;
    private final ContentAO contentAO;

    public LinkExtractor(EventLog eventLog, ContentAO contentAO) {
        this.eventLog = eventLog;
        this.contentAO = contentAO;
    }

    public synchronized void extractLinks(Content content, LinkHandler linkHandler) throws SystemException {

        if (content.isExternalLink()) {
            linkHandler.contentLinkFound(content, content.getLocation());
        } else {
            content = contentAO.getContent(ContentIdentifier.fromContentId(content.getId()), true);

            List<Attribute> attributes = content.getAttributes(AttributeDataType.CONTENT_DATA);
            for (Attribute attribute : attributes) {
                String attrName = (isNotBlank(attribute.getTitle())) ? attribute.getTitle() : attribute.getName();
                if (attribute instanceof HtmltextAttribute) {
                    String html = attribute.getValue();
                    try {
                        if (html != null) {
                            Elements links = Jsoup.parse(html).select("a[href]");
                            for (Element link : links) {
                                String href = link.attr("href");
                                linkHandler.attributeLinkFound(content, href, attrName);

                            }
                        }
                    } catch (Throwable e) {
                        eventLog.log("LinkExtractor", "localhost", Event.FAILED_LINK_EXTRACT, String.format("Failed to extract links from %s", content.getUrl()), content);
                        log.error("contentId: {}, associationid: {}, attribute: {} {}",
                                content.getId(), content.getAssociation().getId(), attrName, html);
                    }
                } else if (attribute instanceof UrlAttribute) {
                    String link = attribute.getValue();
                    if (link != null && link.length() > 0) {
                        if (link.startsWith("/")) {
                            link = Aksess.VAR_WEB + link;
                        }
                        linkHandler.attributeLinkFound(content, link, attrName);
                    }
                } else if (attribute instanceof FileAttribute && isNotBlank(attribute.getValue())) {
                    try {
                        int attachmentId = Integer.parseInt(attribute.getValue());
                        String link = Aksess.VAR_WEB + "/attachment.ap?id=" + attachmentId;
                        linkHandler.attributeLinkFound(content, link, attrName);
                    } catch (Exception e) {
                        log.error("Error getting Content({}) FileAttribute {} with value {}", content.getId(), attribute.getName(), attribute.getValue());
                    }
                } else if (attribute instanceof MediaAttribute && isNotBlank(attribute.getValue())) {
                    try {
                        int mediaId = Integer.parseInt(attribute.getValue());
                        String link = Aksess.VAR_WEB + "/multimedia.ap?id=" + mediaId;
                        linkHandler.attributeLinkFound(content, link, attrName);
                    } catch (Exception e) {
                        log.error("Error getting Content({}) FileAttribute {} with value {}", content.getId(), attribute.getName(), attribute.getValue());
                    }
                }

            }

        }
    }
}
