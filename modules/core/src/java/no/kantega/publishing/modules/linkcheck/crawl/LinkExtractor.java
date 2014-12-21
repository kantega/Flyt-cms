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
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.HtmltextAttribute;
import no.kantega.publishing.common.data.attributes.UrlAttribute;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.content.api.ContentAO;
import no.kantega.publishing.eventlog.Event;
import no.kantega.publishing.eventlog.EventLog;
import org.cyberneko.html.parsers.SAXParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class LinkExtractor {
    private static final Logger log = LoggerFactory.getLogger(LinkExtractor.class);

    private SAXParser parser;
    private final EventLog eventLog;
    private final ContentAO contentAO;

    public LinkExtractor(EventLog eventLog, ContentAO contentAO) {
        this.eventLog = eventLog;
        this.contentAO = contentAO;
        parser = new SAXParser();

        try {
            parser.setFeature("http://cyberneko.org/html/features/balance-tags/document-fragment", true);
            parser.setProperty("http://cyberneko.org/html/properties/names/elems", "match");
        } catch (SAXNotSupportedException | SAXNotRecognizedException e) {
            throw new RuntimeException(e);
        }


    }
    public synchronized void extractLinks(Content content, LinkHandler linkHandler) throws SystemException {
        LinkExtractingHandler htmlHandler = new LinkExtractingHandler();

        parser.setContentHandler(htmlHandler);

        if(content.isExternalLink()) {
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
                            parser.parse(new InputSource(new StringReader(html)));
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
                }

                for (String link : htmlHandler.getLinks()) {
                    linkHandler.attributeLinkFound(content, link, attrName);
                }
                htmlHandler.clear();
            }

        }

    }

    private class LinkExtractingHandler extends DefaultHandler {
        private List<String> links = new ArrayList<>();

        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if(localName.equalsIgnoreCase("a")) {
                String href = attributes.getValue("href");
                if(href != null) {
                    links.add(href);
                }
            }
        }

        public List<String> getLinks() {
            return links;
        }

        public void clear() {
            links.clear();
        }
    }

}
