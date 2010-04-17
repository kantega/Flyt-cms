/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.common.ao;

import no.kantega.publishing.modules.linkcheck.crawl.LinkEmitter;
import no.kantega.publishing.modules.linkcheck.check.LinkOccurrenceHandler;
import no.kantega.publishing.modules.linkcheck.check.LinkOccurrence;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.commons.sqlsearch.SearchTerm;

import java.util.List;

public interface LinkDao {

    /**
     * Empties the link list.
     */
    public void deleteAllLinks();


    /**
     *
     * @param emitter
     */
    public void saveAllLinks(LinkEmitter emitter);


    /**
     *
     * @param term
     * @param handler
     */
    public void doForEachLink(SearchTerm term, no.kantega.publishing.modules.linkcheck.check.LinkHandler handler);


    /**
     * Calls the supplied LinkOccurrenceHandler for each link in the site.
     *
     * @param cid - Fetches all the links for this page and below this page
     * @param sort -
     * @param handler - Callback
     */
    public void doForEachLinkOccurrence(ContentIdentifier parent, String sort, LinkOccurrenceHandler handler);

    /**
     * Gets all broken links for a given content
     *
     * @param contentId - The id of the content to investigate
     * @return List of link occurrences
     */
    public List<LinkOccurrence> getLinksforContentId(int contentId);


    /**
     * Removes links for a given contentId.
     * @param contentId - The contentId for which to remove links
     */
    public void deleteLinksForContentId(int contentId);


    /**
     * Returns the total number of broken links.
     * @return int - Total
     */
    public int getNumberOfLinks();
}
