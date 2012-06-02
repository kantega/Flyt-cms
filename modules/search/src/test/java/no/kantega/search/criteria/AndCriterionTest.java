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
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Date: Dec 5, 2008
 * Time: 2:08:59 PM
 *
 * @author Tarje Killingberg
 */
public class AndCriterionTest extends AbstractCriterionTest {

    private static final String SOURCE = AndCriterionTest.class.getName();

    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testBothOfTwoHit() throws ParseException {
        // Create criteria
        AndCriterion andCriterion = new AndCriterion();
        Date lastModifiedFrom = dateFormat.parse("2005-09-01");
        Date lastModifiedTo = dateFormat.parse("2005-09-30");
        andCriterion.add(new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo));

        PerFieldAnalyzerWrapper perFieldAnalyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer());
        perFieldAnalyzer.addAnalyzer(Fields.CONTENT, new SnowballAnalyzer("Norwegian"));
        

        TextCriterion textCriterion = new TextCriterion(Fields.CONTENT, "spasertrur", perFieldAnalyzer);
        andCriterion.add(textCriterion);

        // Create search query
        SearchQueryDefaultImpl searchQuery = new SearchQueryDefaultImpl();
        searchQuery.addCriterion(andCriterion);

        // Search
        SearchResult result = getSearcher().search(searchQuery);

        // Verify result
        List<DocumentHit> documentHits = result.getDocumentHits();
        assertEquals(1, documentHits.size());
    }

    public void testFirstOfTwoHit() throws ParseException {
        // Create criteria
        AndCriterion andCriterion = new AndCriterion();
        Date lastModifiedFrom = dateFormat.parse("2008-05-24");
        Date lastModifiedTo = new Date();
        andCriterion.add(new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo));
        lastModifiedFrom = dateFormat.parse("2007-12-24");
        lastModifiedTo = dateFormat.parse("2008-05-01");
        andCriterion.add(new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo));

        // Create search query
        SearchQueryDefaultImpl searchQuery = new SearchQueryDefaultImpl();
        searchQuery.addCriterion(andCriterion);

        // Search
        SearchResult result = getSearcher().search(searchQuery);

        // Verify result
        List<DocumentHit> documentHits = result.getDocumentHits();
        assertEquals(0, documentHits.size());
    }

    public void testSecondOfTwoHit() throws ParseException {
        // Create criteria
        AndCriterion andCriterion = new AndCriterion();
        Date lastModifiedFrom = dateFormat.parse("2008-10-24");
        Date lastModifiedTo = new Date();
        andCriterion.add(new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo));
        lastModifiedFrom = dateFormat.parse("2007-12-24");
        lastModifiedTo = dateFormat.parse("2008-05-24");
        andCriterion.add(new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo));

        // Create search query
        SearchQueryDefaultImpl searchQuery = new SearchQueryDefaultImpl();
        searchQuery.addCriterion(andCriterion);

        // Search
        SearchResult result = getSearcher().search(searchQuery);

        // Verify result
        List<DocumentHit> documentHits = result.getDocumentHits();
        assertEquals(0, documentHits.size());
    }

    public void testNoHit() throws ParseException {
        // Create criteria
        AndCriterion andCriterion = new AndCriterion();
        Date lastModifiedFrom = dateFormat.parse("2008-06-24");
        Date lastModifiedTo = new Date();
        andCriterion.add(new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo));
        lastModifiedFrom = dateFormat.parse("2007-12-24");
        lastModifiedTo = dateFormat.parse("2008-02-10");
        andCriterion.add(new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo));

        // Create search query
        SearchQueryDefaultImpl searchQuery = new SearchQueryDefaultImpl();
        searchQuery.addCriterion(andCriterion);

        // Search
        SearchResult result = getSearcher().search(searchQuery);

        // Verify result
        List<DocumentHit> documentHits = result.getDocumentHits();
        assertEquals(0, documentHits.size());
    }

}
