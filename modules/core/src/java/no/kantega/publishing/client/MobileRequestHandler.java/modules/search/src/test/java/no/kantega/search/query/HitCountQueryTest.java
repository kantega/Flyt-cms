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

package no.kantega.search.query;

import no.kantega.search.AbstractSearchTestCase;
import no.kantega.search.query.hitcount.HitCountQueryDefaultImpl;
import no.kantega.search.query.hitcount.HitCountQuery;
import no.kantega.search.query.hitcount.RangeHitCountQuery;
import no.kantega.search.criteria.TextCriterion;
import no.kantega.search.index.Fields;
import no.kantega.search.result.HitCount;
import no.kantega.search.result.SearchResultExtendedImpl;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.analysis.Analyzer;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: Jan 19, 2009
 * Time: 8:23:38 AM
 *
 * @author Tarje Killingberg
 */
public class HitCountQueryTest extends AbstractSearchTestCase {

    private static final String SOURCE = HitCountQueryTest.class.getName();


    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Tester SearchHandlerExtendedImpl med forskjellige oppsett av HitCountQueries
     *
     * @throws Exception hvis noe kaster en exception
     */
    public void testHitCountQuerySingle() throws Exception {
        Analyzer analyzer = getIndexManager().getAnalyzerFactory().createInstance();

        int[] numberOfHits = new int[3];
        int[] totalHitCounts = new int[3];
        String searchPhrase = "trondheim";

        // Create search query
        SearchQueryExtendedImpl searchQuery = new SearchQueryExtendedImpl();
        HitCountQueryDefaultImpl hitCountQuery = new HitCountQueryDefaultImpl(Fields.CONTENT_TEMPLATE_ID);
        searchQuery.addHitCountQuery(hitCountQuery);
        TextCriterion textCriterion = new TextCriterion(searchPhrase, analyzer);
        searchQuery.addCriterion(textCriterion);

        // Search
        SearchResultExtendedImpl searchResult = (SearchResultExtendedImpl)getSearcher().search(searchQuery);
        numberOfHits[0] = searchResult.getNumberOfHits();

        // Verify result
        List<HitCount> hitCounts = searchResult.getHitCounts();
        // Summere HitCount'ene
        totalHitCounts[0] = 0;
        for (HitCount hitCount : hitCounts) {
            totalHitCounts[0] += hitCount.getHitCount();
        }
        // Sjekk at HitCount er regnet ut for alle termene i feltet
        List<String> terms = new ArrayList<String>();
        TermEnum termEnum = getIndexManager().getIndexReaderManager().getReader("aksess").terms(new Term(Fields.CONTENT_TEMPLATE_ID, ""));
        for ( ; termEnum.term() != null && termEnum.term().field() == Fields.CONTENT_TEMPLATE_ID ; termEnum.next()) {
            terms.add(termEnum.term().text());
        }
        assertEquals("HitCount er ikke regnet ut for riktig antall termer", terms.size(), hitCounts.size());

        // Create search query
        searchQuery = new SearchQueryExtendedImpl();
        String[] hitCountTerms = new String[]{ "1", "5", "10" };
        hitCountQuery = new HitCountQueryDefaultImpl(Fields.CONTENT_TEMPLATE_ID, hitCountTerms, true);
        searchQuery.addHitCountQuery(hitCountQuery);
        textCriterion = new TextCriterion(searchPhrase, analyzer);
        searchQuery.addCriterion(textCriterion);

        // Search
        searchResult = (SearchResultExtendedImpl)getSearcher().search(searchQuery);
        numberOfHits[1] = searchResult.getNumberOfHits();

        // Verify result
        hitCounts = searchResult.getHitCounts();
        // Summere HitCount'ene
        totalHitCounts[1] = 0;
        for (HitCount hitCount : hitCounts) {
            totalHitCounts[1] += hitCount.getHitCount();
        }
        assertEquals("HitCount er ikke regnet ut for riktig antall termer", hitCountTerms.length, hitCounts.size());
        assertTrue("Totalt antall HitCounts for alle termer ("+ totalHitCounts[0] +") skal være større enn totalt antall HitCounts for et subsett av termene (" + totalHitCounts[1] + ").", totalHitCounts[0] > totalHitCounts[1]);
        assertEquals("Antall søketreff skal være like uavhengig av HitCountQueries.", numberOfHits[0], numberOfHits[1]);

        // Create search query
        searchQuery = new SearchQueryExtendedImpl();
        hitCountTerms = new String[]{ "1", "5", "10" };
        hitCountQuery = new HitCountQueryDefaultImpl(Fields.CONTENT_TEMPLATE_ID, hitCountTerms, false);
        searchQuery.addHitCountQuery(hitCountQuery);
        textCriterion = new TextCriterion(searchPhrase, analyzer);
        searchQuery.addCriterion(textCriterion);

        // Search
        searchResult = (SearchResultExtendedImpl)getSearcher().search(searchQuery);
        numberOfHits[2] = searchResult.getNumberOfHits();

        // Verify result
        hitCounts = searchResult.getHitCounts();
        totalHitCounts[2] = 0;
        for (HitCount hitCount : hitCounts) {
            totalHitCounts[2] += hitCount.getHitCount();
        }
        assertEquals("HitCount er ikke regnet ut for riktig antall termer", hitCountTerms.length + 1, hitCounts.size());
        assertEquals("Totalt antall HitCounts for alle termer skal være lik totalt antall HitCounts for et subsett av termene pluss 'other'.", totalHitCounts[0], totalHitCounts[2]);
        assertEquals("Antall søketreff skal være like uavhengig av HitCountQueries.", numberOfHits[0], numberOfHits[2]);
    }

    /**
     * Tester SearchHandlerExtendedImpl med forskjellige oppsett av HitCountQueries
     *
     * @throws Exception hvis noe kaster en exception
     */
    public void testHitCountQueryMultiple() throws Exception {
        Analyzer analyzer = getIndexManager().getAnalyzerFactory().createInstance();

        int[] numberOfHits = new int[3];
        int[] totalHitCounts = new int[3];
        String searchPhrase = "nidelven";

        // Create search query
        SearchQueryExtendedImpl searchQuery = new SearchQueryExtendedImpl();
        HitCountQueryDefaultImpl hitCountQuery = new HitCountQueryDefaultImpl(Fields.CONTENT_TEMPLATE_ID);
        searchQuery.addHitCountQuery(hitCountQuery);
        hitCountQuery = new HitCountQueryDefaultImpl(Fields.DOCTYPE);
        searchQuery.addHitCountQuery(hitCountQuery);
        TextCriterion textCriterion = new TextCriterion(searchPhrase, analyzer);
        searchQuery.addCriterion(textCriterion);

        // Search
        SearchResultExtendedImpl searchResult = (SearchResultExtendedImpl)getSearcher().search(searchQuery);
        numberOfHits[0] = searchResult.getNumberOfHits();

        // Verify result
        List<HitCount> hitCounts = searchResult.getHitCounts();
        // Summere HitCount'ene
        totalHitCounts[0] = 0;
        for (HitCount hitCount : hitCounts) {
            totalHitCounts[0] += hitCount.getHitCount();
        }
        // Sjekk at HitCount er regnet ut for alle termene i feltet
        List<String> terms = new ArrayList<String>();
        TermEnum termEnum = getIndexManager().getIndexReaderManager().getReader("aksess").terms(new Term(Fields.CONTENT_TEMPLATE_ID, ""));
        for ( ; termEnum.term() != null && termEnum.term().field() == Fields.CONTENT_TEMPLATE_ID ; termEnum.next()) {
            terms.add(termEnum.term().text());
        }
        termEnum = getIndexManager().getIndexReaderManager().getReader("aksess").terms(new Term(Fields.DOCTYPE, ""));
        for ( ; termEnum.term() != null && termEnum.term().field() == Fields.DOCTYPE ; termEnum.next()) {
            terms.add(termEnum.term().text());
        }
        assertEquals("HitCount er ikke regnet ut for riktig antall termer", terms.size(), hitCounts.size());

        // Create search query
        searchQuery = new SearchQueryExtendedImpl();
        String[][] hitCountTerms = new String[][]{ { "1", "5", "10" }, { "Content" } };
        hitCountQuery = new HitCountQueryDefaultImpl(Fields.CONTENT_TEMPLATE_ID, hitCountTerms[0], true);
        searchQuery.addHitCountQuery(hitCountQuery);
        hitCountQuery = new HitCountQueryDefaultImpl(Fields.DOCTYPE, hitCountTerms[1], true);
        searchQuery.addHitCountQuery(hitCountQuery);
        textCriterion = new TextCriterion(searchPhrase, analyzer);
        searchQuery.addCriterion(textCriterion);

        // Search
        searchResult = (SearchResultExtendedImpl)getSearcher().search(searchQuery);
        numberOfHits[1] = searchResult.getNumberOfHits();

        // Verify result
        hitCounts = searchResult.getHitCounts();
        // Summere HitCount'ene
        totalHitCounts[1] = 0;
        for (HitCount hitCount : hitCounts) {
            totalHitCounts[1] += hitCount.getHitCount();
        }
        assertEquals("HitCount er ikke regnet ut for riktig antall termer", hitCountTerms[0].length + hitCountTerms[1].length, hitCounts.size());
        assertEquals("Antall søketreff skal være like uavhengig av HitCountQueries.", numberOfHits[0], numberOfHits[1]);

        // Create search query
        searchQuery = new SearchQueryExtendedImpl();
        hitCountQuery = new HitCountQueryDefaultImpl(Fields.CONTENT_TEMPLATE_ID, hitCountTerms[0], false);
        searchQuery.addHitCountQuery(hitCountQuery);
        hitCountQuery = new HitCountQueryDefaultImpl(Fields.DOCTYPE, hitCountTerms[1], false);
        searchQuery.addHitCountQuery(hitCountQuery);
        textCriterion = new TextCriterion(searchPhrase, analyzer);
        searchQuery.addCriterion(textCriterion);

        // Search
        searchResult = (SearchResultExtendedImpl)getSearcher().search(searchQuery);
        numberOfHits[2] = searchResult.getNumberOfHits();

        // Verify result
        hitCounts = searchResult.getHitCounts();
        totalHitCounts[2] = 0;
        for (HitCount hitCount : hitCounts) {
            totalHitCounts[2] += hitCount.getHitCount();
        }
        assertEquals("HitCount er ikke regnet ut for riktig antall termer", hitCountTerms[0].length + hitCountTerms[1].length + 2, hitCounts.size());
        assertEquals("Totalt antall HitCounts for alle termer skal være lik totalt antall HitCounts for et subsett av termene pluss 'other'.", totalHitCounts[0], totalHitCounts[2]);
        assertEquals("Antall søketreff skal være like uavhengig av HitCountQueries.", numberOfHits[0], numberOfHits[2]);
    }


    public void testRangeHitCountQuery() {
        Analyzer analyzer = getIndexManager().getAnalyzerFactory().createInstance();

        int numberOfHits = 0;
        int totalHitCounts = 0;
        int nofBins = 5;
        String searchPhrase = "trondheim";

        // Create search query
        SearchQueryExtendedImpl searchQuery = new SearchQueryExtendedImpl();
        RangeHitCountQuery rangeHitCountQuery = new RangeHitCountQuery(Fields.LAST_MODIFIED, nofBins);
        searchQuery.addHitCountQuery(rangeHitCountQuery);
        TextCriterion textCriterion = new TextCriterion(searchPhrase, analyzer);
        searchQuery.addCriterion(textCriterion);

        // Search
        SearchResultExtendedImpl searchResult = (SearchResultExtendedImpl)getSearcher().search(searchQuery);
        numberOfHits = searchResult.getNumberOfHits();

        // Verify result
        List<HitCount> hitCounts = searchResult.getHitCounts();
        // Summere HitCount'ene
        totalHitCounts = 0;
        for (HitCount hitCount : hitCounts) {
            System.out.println("hitCount = " + hitCount);
            totalHitCounts += hitCount.getHitCount();
        }
        // Sjekk at HitCount er regnet ut for alle termene i feltet
        List<String> terms = new ArrayList<String>();
//        TermEnum termEnum = getIndexManager().getIndexReaderManager().getReader("aksess").terms(new Term(Fields.CONTENT_TEMPLATE_ID, ""));
//        for ( ; termEnum.term() != null && termEnum.term().field() == Fields.CONTENT_TEMPLATE_ID ; termEnum.next()) {
//            terms.add(termEnum.term().text());
//        }
        assertEquals("HitCount er ikke regnet ut for riktig antall termer", 5, hitCounts.size());
        assertEquals(numberOfHits, totalHitCounts);
    }

}
