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
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.HtmltextAttribute;
import no.kantega.publishing.common.data.attributes.UrlAttribute;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.Aksess;
import org.cyberneko.html.parsers.SAXParser;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class LinkExtractor {
    private static final String SOURCE = "aksess.LinkExtractor";

    private SAXParser parser;

    public LinkExtractor() {
        parser = new SAXParser();

        try {
            parser.setFeature("http://cyberneko.org/html/features/balance-tags/document-fragment", true);
            parser.setProperty("http://cyberneko.org/html/properties/names/elems", "match");
        } catch (SAXNotSupportedException e) {
            throw new RuntimeException(e);
        } catch (SAXNotRecognizedException e) {
            throw new RuntimeException(e);
        }


    }
    public synchronized void extractLinks(Content content, LinkHandler linkHandler) throws SystemException {


        LinkExtractingHandler htmlHandler = new LinkExtractingHandler();

        parser.setContentHandler(htmlHandler);

        if(content.isExternalLink()) {
            linkHandler.contentLinkFound(content, content.getLocation());
        } else {
            content = ContentAO.getContent(new ContentIdentifier(content.getId()), true);

            List attributes = content.getAttributes(AttributeDataType.CONTENT_DATA);
            for (int i = 0; i < attributes.size(); i++) {
                Attribute a =  (Attribute) attributes.get(i);
                String attrName = (a.getTitle() != null && !a.getTitle().equals(""))? a.getTitle() : a.getName();
                if(a instanceof HtmltextAttribute) {
                    String html = a.getValue();
                    try {
                        if(html != null) {
                            parser.parse(new InputSource(new StringReader(html)));
                        }
                    } catch (SAXException e) {
                        Log.error(SOURCE, e, null, null);
                    } catch (IOException e) {
                        Log.error(SOURCE, e, null, null);
                    }
                } else if (a instanceof UrlAttribute) {
                    String link = a.getValue();
                    if (link != null && link.length() > 0) {
                        if (link.startsWith("/")) {
                            link = Aksess.VAR_WEB + link;
                        }
                        linkHandler.attributeLinkFound(content, link, attrName);
                    }
                }

                Iterator links = htmlHandler.getLinks().iterator();
                while (links.hasNext()) {
                    String link = (String) links.next();
                    linkHandler.attributeLinkFound(content, link, attrName);
                }
                htmlHandler.clear();
            }

        }

    }

    class LinkExtractingHandler extends DefaultHandler {
        private List links = new ArrayList();

        public void startElement(String string, String string1, String string2, Attributes attributes) throws SAXException {
            if(string1.equalsIgnoreCase("a")) {
                String href = attributes.getValue("href");
                if(href != null) {
                    links.add(href);
                }
            }
        }

        public List getLinks() {
            return links;
        }

        public void clear() {
            links.clear();
        }
    }

}
