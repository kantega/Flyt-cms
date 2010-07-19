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

package no.kantega.search.core;

import no.kantega.search.AbstractSearchTestCase;

import java.text.ParseException;

/**
 * Date: Dec 3, 2008
 * Time: 1:02:16 PM
 *
 * @author Tarje Killingberg
 */
public class SearchTest extends AbstractSearchTestCase {

    private static final String SOURCE = SearchTest.class.getName();


    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testDefaultSearch() throws ParseException {
//        // Create criteria
//        Date lastModifiedFrom = IndexGenerator.dateFormat.parse("2008-05-24");
//        Date lastModifiedTo = new Date();
//        LastModifiedCriterion modifiedCriterion = new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo);
//
//        // Create search query
//        SearchQueryDefaultImpl searchQuery = new SearchQueryDefaultImpl();
//        searchQuery.addCriterion(modifiedCriterion);
//
//        // Search
//        SearchResult result = searcher.search(searchQuery);
//
//        // Verify result
//        List<DocumentHit> documentHits = result.getDocumentHits();
////        assertEquals(3, documentHits.size());
//
//        /* ******************** NEW SEARCH ******************** */
//        // Create criteria
//        lastModifiedFrom = IndexGenerator.dateFormat.parse("2008-05-24");
//        lastModifiedTo = null;
//        modifiedCriterion = new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo);
//
//        // Create search query
//        searchQuery = new SearchQueryDefaultImpl();
//        searchQuery.addCriterion(modifiedCriterion);
//
//        // Search
//        result = searcher.search(searchQuery);
//
//        // Verify result
//        documentHits = result.getDocumentHits();
////        assertEquals(3, documentHits.size());
//
//        /* ******************** NEW SEARCH ******************** */
//        // Create criteria
//        lastModifiedFrom = null;
//        lastModifiedTo = IndexGenerator.dateFormat.parse("2008-05-24");
//        modifiedCriterion = new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo);
//
//        // Create search query
//        searchQuery = new SearchQueryDefaultImpl();
//        searchQuery.addCriterion(modifiedCriterion);
//
//        // Search
////        result = searcher.search(searchQuery);
//
//        // Verify result
////        documentHits = result.getDocumentHits();
////        assertEquals(8, documentHits.size());
//
//        /* ******************** NEW SEARCH ******************** */
//        // Create criteria
//        lastModifiedFrom = IndexGenerator.dateFormat.parse("2008-05-24");
//        lastModifiedTo = IndexGenerator.dateFormat.parse("2008-05-24");
//        modifiedCriterion = new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo);
//
//        // Create search query
//        searchQuery = new SearchQueryDefaultImpl();
//        searchQuery.addCriterion(modifiedCriterion);
//
//        // Search
//        result = searcher.search(searchQuery);
//
//        // Verify result
//        documentHits = result.getDocumentHits();
////        assertEquals(1, documentHits.size());
////        SearchHit searchHit = searchHits.get(0);
////        assertEquals("100", searchHit.getDocument().get(Fields.CONTENT_ID));
//
//        /* ******************** NEW SEARCH ******************** */
//        // Create criteria
//        lastModifiedFrom = null;
//        lastModifiedTo = null;
//        modifiedCriterion = new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo);
//
//        // Create search query
//        searchQuery = new SearchQueryDefaultImpl();
//        searchQuery.addCriterion(modifiedCriterion);
//
//        // Search
//        try {
//            result = searcher.search(searchQuery);
//            fail();
//        } catch (IllegalArgumentException e) {
//            // OK
//        }
    }

    public void testExtendedSearch() throws ParseException {
//        // Create criteria
//        TextCriterion textCriterion = new TextCriterion(Fields.CONTENT, "trondheim");
//
//        // Create search query
//        SearchQueryExtendedImpl searchQuery = new SearchQueryExtendedImpl();
//        searchQuery.addCriterion(textCriterion);
//        searchQuery.setFieldname(Fields.CONTENT_TEMPLATE_ID);
//
//        // Search
//        SearchResult result = searcher.search(searchQuery);
//
//        // Verify result
//        // TODO
    }

}
