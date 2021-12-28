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

package no.kantega.publishing.api.link;

import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.modules.linkcheck.check.LinkOccurrence;
import no.kantega.publishing.modules.linkcheck.check.LinkQueryGenerator;
import no.kantega.publishing.modules.linkcheck.crawl.LinkEmitter;

import java.util.List;

public interface LinkDao {

    /**
     * Empties the link list.
     */
    void deleteAllLinks();


    /**
     *
     * @param emitter
     */
    void saveAllLinks(LinkEmitter emitter);

    /**
     *
     * @param emitter
     * @param content
     */
    void saveLinksForContent(LinkEmitter emitter, Content content);

    /**
     *
     * @param linkQueryGenerator
     * @param handler
     */
    void doForEachLink(LinkQueryGenerator linkQueryGenerator, no.kantega.publishing.modules.linkcheck.check.LinkHandler handler);

    /**
     * Find all broken links under given parent
     *
     * @param parent - ContentIdentifier for parent
     * @param sortBy - string to sort on
     * @return List of broken links
     */
    List<LinkOccurrence> getBrokenLinksUnderParent(ContentIdentifier parent, String sortBy);

    /**
     * All broken links
     * @param sort
     * @return List of broken links
     */
    List<LinkOccurrence> getAllBrokenLinks(String sort);


    /**
     * Gets all broken links for a given content
     *
     * @param contentId - The id of the content to investigate
     * @return List of link occurrences
     */
    List<LinkOccurrence> getBrokenLinksforContentId(int contentId);


    List<LinkOccurrence> getLinksforContentId(int contentId);

    /**
     * Removes links for a given contentId.
     * @param contentId - The contentId for which to remove links
     */
    void deleteLinksForContentId(int contentId);


    /**
     * Returns the total number of broken links.
     * @return int - Total
     */
    int getNumberOfLinks();

}
