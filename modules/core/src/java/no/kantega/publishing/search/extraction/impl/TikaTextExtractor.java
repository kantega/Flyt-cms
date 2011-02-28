/*
 * Copyright 2011 Kantega AS
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

package no.kantega.publishing.search.extraction.impl;

import no.kantega.publishing.search.extraction.TextExtractor;
import org.apache.log4j.Logger;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

import java.io.InputStream;

public class TikaTextExtractor implements TextExtractor {
    private Logger log = Logger.getLogger(getClass());

    public String extractText(InputStream is, String fileName) {
        try {
            Metadata metadata = new Metadata();
            metadata.set(Metadata.RESOURCE_NAME_KEY, fileName);
            AutoDetectParser autoDetectParser = new AutoDetectParser();
            ContentHandler bodyHandler = new BodyContentHandler();
            autoDetectParser.parse(is, bodyHandler, metadata);

            return bodyHandler.toString();
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            return "";
        }
    }
}
