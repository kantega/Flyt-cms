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

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Date: Dec 5, 2008
 * Time: 4:30:42 PM
 *
 * @author Tarje Killingberg
 */
public class OrCriterionTest extends AbstractCriterionTest {

    private static final String SOURCE = OrCriterionTest.class.getName();


    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testAllOfThreeHit() throws ParseException {
        // Create criteria
        OrCriterion orCriterion = new OrCriterion();
        Date lastModifiedFrom = dateFormat.parse("2008-05-06");
        Date lastModifiedTo = dateFormat.parse("2008-05-07");
        orCriterion.add(new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo));
        lastModifiedFrom = dateFormat.parse("2005-09-10");
        lastModifiedTo = dateFormat.parse("2005-09-11");
        orCriterion.add(new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo));
        lastModifiedFrom = dateFormat.parse("2008-04-10");
        lastModifiedTo = dateFormat.parse("2008-04-12");
        orCriterion.add(new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo));

        // Create search query
        SearchQueryDefaultImpl searchQuery = new SearchQueryDefaultImpl();
        searchQuery.addCriterion(orCriterion);

        // Search
        SearchResult result = getSearcher().search(searchQuery);

        // Verify result
        List<DocumentHit> documentHits = result.getDocumentHits();
        assertEquals(3, documentHits.size());
    }

    public void testAllOfTwoHit() throws ParseException {
        // Create criteria
        OrCriterion orCriterion = new OrCriterion();
        Date lastModifiedFrom = dateFormat.parse("2008-05-06");
        Date lastModifiedTo = dateFormat.parse("2008-05-07");
        orCriterion.add(new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo));
        lastModifiedFrom = dateFormat.parse("2005-09-10");
        lastModifiedTo = dateFormat.parse("2005-09-11");
        orCriterion.add(new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo));

        // Create search query
        SearchQueryDefaultImpl searchQuery = new SearchQueryDefaultImpl();
        searchQuery.addCriterion(orCriterion);

        // Search
        SearchResult result = getSearcher().search(searchQuery);

        // Verify result
        List<DocumentHit> documentHits = result.getDocumentHits();
        assertEquals(2, documentHits.size());
    }

    public void testFirstOfTwoHit() throws ParseException {
        // Create criteria
        OrCriterion orCriterion = new OrCriterion();
        Date lastModifiedFrom = dateFormat.parse("2008-07-15");
        Date lastModifiedTo = dateFormat.parse("2008-07-20");
        orCriterion.add(new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo));
        lastModifiedFrom = dateFormat.parse("2008-05-25");
        lastModifiedTo = dateFormat.parse("2008-06-01");
        orCriterion.add(new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo));

        // Create search query
        SearchQueryDefaultImpl searchQuery = new SearchQueryDefaultImpl();
        searchQuery.addCriterion(orCriterion);

        // Search
        SearchResult result = getSearcher().search(searchQuery);

        // Verify result
        List<DocumentHit> documentHits = result.getDocumentHits();
        assertEquals(1, documentHits.size());
    }

    public void testSecondOfTwoHit() throws ParseException {
        // Create criteria
        OrCriterion orCriterion = new OrCriterion();
        Date lastModifiedFrom = dateFormat.parse("2008-05-25");
        Date lastModifiedTo = dateFormat.parse("2008-06-01");
        orCriterion.add(new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo));
        lastModifiedFrom = dateFormat.parse("2008-07-15");
        lastModifiedTo = dateFormat.parse("2008-07-20");
        orCriterion.add(new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo));

        // Create search query
        SearchQueryDefaultImpl searchQuery = new SearchQueryDefaultImpl();
        searchQuery.addCriterion(orCriterion);

        // Search
        SearchResult result = getSearcher().search(searchQuery);

        // Verify result
        List<DocumentHit> documentHits = result.getDocumentHits();
        assertEquals(1, documentHits.size());
    }

    public void testNoHit() throws ParseException {
        // Create criteria
        OrCriterion orCriterion = new OrCriterion();
        Date lastModifiedFrom = dateFormat.parse("2008-06-20");
        Date lastModifiedTo = dateFormat.parse("2008-06-27");
        orCriterion.add(new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo));
        lastModifiedFrom = dateFormat.parse("2007-12-30");
        lastModifiedTo = dateFormat.parse("2008-01-02");
        orCriterion.add(new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo));

        // Create search query
        SearchQueryDefaultImpl searchQuery = new SearchQueryDefaultImpl();
        searchQuery.addCriterion(orCriterion);

        // Search
        SearchResult result = getSearcher().search(searchQuery);

        // Verify result
        List<DocumentHit> documentHits = result.getDocumentHits();
        assertEquals(0, documentHits.size());
    }

}
