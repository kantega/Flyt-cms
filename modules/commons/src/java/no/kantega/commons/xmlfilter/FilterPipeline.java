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

import java.util.ArrayList;
import java.util.List;

public class FilterPipeline {
    private static final Logger log = LoggerFactory.getLogger(FilterPipeline.class);

    List<Filter> filters = new ArrayList<>();

    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    public String filter(String content) throws SystemException {
        try {
            Document document = Jsoup.parseBodyFragment(content);
            document.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
            document.outputSettings().prettyPrint(false);

            for (Filter filter : filters) {
                document = filter.runFilter(document);
            }

            return document.getElementsByTag("body").html();
        } catch (Exception e) {
            log.error("Could not filter", e);
            throw new SystemException("Could not filter", e);
        }
    }

    public void removeFilters() {
        filters = new ArrayList<>();
    }

}
