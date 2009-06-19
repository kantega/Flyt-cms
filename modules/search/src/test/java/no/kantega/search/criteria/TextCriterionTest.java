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

import no.kantega.search.index.Fields;
import no.kantega.search.query.SearchQueryDefaultImpl;
import no.kantega.search.result.DocumentHit;
import no.kantega.search.result.SearchResult;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

/**
 * Date: Dec 5, 2008
 * Time: 4:51:48 PM
 *
 * @author Tarje Killingberg
 */
public class TextCriterionTest extends AbstractCriterionTest {

    private static final String SOURCE = TextCriterionTest.class.getName();


    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testHit() throws ParseException {
        // Create criteria
        PerFieldAnalyzerWrapper perFieldAnalyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer());
        perFieldAnalyzer.addAnalyzer(Fields.CONTENT, new SnowballAnalyzer("Norwegian"));

        TextCriterion textCriterion = new TextCriterion(Fields.CONTENT, "speilvegg", perFieldAnalyzer);

        // Create search query
        SearchQueryDefaultImpl searchQuery = new SearchQueryDefaultImpl();
        searchQuery.addCriterion(textCriterion);

        // Search
        SearchResult result = getSearcher().search(searchQuery);

        // Verify result
        List<DocumentHit> documentHits = result.getDocumentHits();
        assertEquals(2, documentHits.size());
    }

    public void testNoHit() throws ParseException {
        // Create criteria
        PerFieldAnalyzerWrapper perFieldAnalyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer());
        perFieldAnalyzer.addAnalyzer(Fields.CONTENT, new SnowballAnalyzer("Norwegian"));

        TextCriterion textCriterion = new TextCriterion(Fields.CONTENT, "\"hainnhoinn i bainn\"", perFieldAnalyzer);

        // Create search query
        SearchQueryDefaultImpl searchQuery = new SearchQueryDefaultImpl();
        searchQuery.addCriterion(textCriterion);

        // Search
        SearchResult result = getSearcher().search(searchQuery);

        // Verify result
        List<DocumentHit> documentHits = result.getDocumentHits();
        assertEquals(0, documentHits.size());
    }

    public void testEmptyString() throws IOException, ParseException {
//        // Create criteria
//        TextCriterion textCriterion = new TextCriterion(Fields.CONTENT, "");
//
//        // Create search query
//        SearchQueryDefaultImpl searchQuery = new SearchQueryDefaultImpl();
//        searchQuery.addCriterion(textCriterion);
//
//        // Search
//        SearchResult result = getSearcher().search(searchQuery);
//
//        // Verify result
//        List<DocumentHit> documentHits = result.getDocumentHits();
//        assertEquals("Et tomt søk bør returnere alle dokumenter i indeksen.", getIndexSize(), documentHits.size());
//        DocumentHit documentHit = documentHits.get(0);
//        assertEquals("100", documentHit.getDocument().get(Fields.CONTENT_ID));
    }

    public void testWildcardString() {
//        fail("IKKE IMPLEMENTERT");
    }

}
