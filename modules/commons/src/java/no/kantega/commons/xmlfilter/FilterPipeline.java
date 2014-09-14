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

package no.kantega.commons.xmlfilter;

import no.kantega.commons.exception.SystemException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLFilterImpl;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class FilterPipeline extends XMLFilterImpl {
    private static final Logger log = LoggerFactory.getLogger(FilterPipeline.class);

    List<XMLFilter> filters = new ArrayList<>();

    public void addFilter(XMLFilterImpl filter) {
        if (filters.size() == 0) {
            setContentHandler(filter);
        } else {
            XMLFilter parent = filters.get(filters.size() - 1);
            parent.setContentHandler(filter);
            filter.setParent(parent);
        }
        filters.add(filter);
    }

    public void setEnd(ContentHandler end) {
        if (filters.size() > 0) {
            XMLFilter parent = filters.get(filters.size() - 1);
            parent.setContentHandler(end);
        }
    }

    public String filter(String content) throws SystemException {
        try {
            Document document = Jsoup.parseBodyFragment(content);
            document.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
            document.outputSettings().prettyPrint(false);

            StringReader reader = new StringReader(document.getElementsByTag("body").html());
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            setParent(xmlReader);

            DefaultHandler dh = null;
            xmlReader.setContentHandler(this);

            SerializerHandler end = new SerializerHandler();
            this.setEnd(end);
            if(filters.size() == 0) {
                this.setContentHandler(end);
            }

            saxParser.parse(new InputSource(reader), dh);
            return end.getContent();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            log.error("Could not filter", e);
            throw new SystemException("Could not filter", e);
        }
    }

    public void removeFilters() {
        filters = new ArrayList<>();
    }

    private static class SerializerHandler extends DefaultHandler {
        private final StringWriter stringWriter;

        public SerializerHandler() {
            stringWriter = new StringWriter();
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            stringWriter.write(ch, start, length);

        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            stringWriter.write("<" + qName);

            for(int i = 0; i < attributes.getLength(); i++){
                stringWriter.append(' ');
                stringWriter.append(attributes.getQName(i));
                stringWriter.append("=\"");
                stringWriter.append(attributes.getValue(i));
                stringWriter.append('"');
            }
            stringWriter.write('>');
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            stringWriter.write("</" + qName + ">");

        }

        public String getContent() {
            return stringWriter.getBuffer().toString();
        }
    }
}
