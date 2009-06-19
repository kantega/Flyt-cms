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

package no.kantega.search.criteria;

import no.kantega.search.query.SearchQueryDefaultImpl;
import no.kantega.search.result.DocumentHit;
import no.kantega.search.result.SearchResult;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Date: Dec 5, 2008
 * Time: 4:23:31 PM
 *
 * @author Tarje Killingberg
 */
public class LastModifiedCriterionTest extends AbstractCriterionTest {

    private static final String SOURCE = LastModifiedCriterionTest.class.getName();


    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testHit() throws ParseException {
        // Create criteria
        Date lastModifiedFrom = dateFormat.parse("2008-04-10");
        Date lastModifiedTo = dateFormat.parse("2008-08-01");
        LastModifiedCriterion lastModifiedCriterion = new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo);

        // Create search query
        SearchQueryDefaultImpl searchQuery = new SearchQueryDefaultImpl();
        searchQuery.addCriterion(lastModifiedCriterion);

        // Search
        SearchResult result = getSearcher().search(searchQuery);

        // Verify result
        List<DocumentHit> documentHits = result.getDocumentHits();
        assertEquals(3, documentHits.size());
    }

    public void testLowerBoundHitHit() throws ParseException {
        // Create criteria
        Date lastModifiedFrom = dateFormat.parse("2008-05-30");
        Date lastModifiedTo = dateFormat.parse("2008-06-01");
        LastModifiedCriterion lastModifiedCriterion = new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo);

        // Create search query
        SearchQueryDefaultImpl searchQuery = new SearchQueryDefaultImpl();
        searchQuery.addCriterion(lastModifiedCriterion);

        // Search
        SearchResult result = getSearcher().search(searchQuery);

        // Verify result
        List<DocumentHit> documentHits = result.getDocumentHits();
        assertEquals(1, documentHits.size());
    }

    public void testUpperBoundHit() throws ParseException {
        // Create criteria
        Date lastModifiedFrom = dateFormat.parse("2008-04-10");
        Date lastModifiedTo = dateFormat.parse("2008-05-06");
        LastModifiedCriterion lastModifiedCriterion = new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo);

        // Create search query
        SearchQueryDefaultImpl searchQuery = new SearchQueryDefaultImpl();
        searchQuery.addCriterion(lastModifiedCriterion);

        // Search
        SearchResult result = getSearcher().search(searchQuery);

        // Verify result
        List<DocumentHit> documentHits = result.getDocumentHits();
        assertEquals(2, documentHits.size());
    }

}
