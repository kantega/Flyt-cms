package no.kantega.openaksess.search.solr;

import no.kantega.search.api.search.*;
import org.apache.commons.collections.Predicate;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.*;
import static no.kantega.openaksess.search.solr.Utils.getDummySearchContext;
import static org.apache.commons.collections.CollectionUtils.select;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/META-INF/spring/applicationContext-solrSearch-test.xml"})
public class SearcherIntegrationTest {
    @Autowired
    private Searcher searcher;
    private final String originalQuery = "as";

    private SearchResponse doSearchSiteOne(String query){
        return doSearch(query, "siteId:1");
    }

    private SearchResponse doSearchSiteIdAbsent(String query){
        return doSearch(query, "siteId:\\-1");
    }

    private SearchResponse doSearchSiteOneAndSiteAbsent(String query){
        return doSearch(query, "siteId:1 OR siteId:\\-1");
    }

    private SearchResponse doSearchNoSiteFilter(String query){
        return doSearch(query, "");
    }

    private SearchResponse doSearch(String query, String filter){
        return doSearch(query, filter, "indexedContentType:aksess-document");
    }

    private SearchResponse doSearch(String query, String filter, String indexedContentType){
        SearchContext searchContext = getDummySearchContext();
        SearchQuery q = new SearchQuery(searchContext, query, filter, indexedContentType);
        q.setHighlightSearchResultDescription(true);
        return searcher.search(q);
    }

    @Test
    public void resultShouldHaveHits(){
        SearchResponse searchResponse = doSearchSiteOne(originalQuery);
        assertTrue("Number of hits should be larger than 0", searchResponse.getNumberOfHits().intValue() > 0);
    }

    @Test
    public void resultShouldHaveHitsFromSiteOneAndSiteNegativeOne(){
        SearchResponse searchResponse = doSearchSiteOne(originalQuery);
        int hitsOnSiteOne = searchResponse.getNumberOfHits().intValue();

        searchResponse = doSearchSiteOneAndSiteAbsent(originalQuery);
        int hitsOnSiteOneAndAbsent = searchResponse.getNumberOfHits().intValue();

        assertTrue("There should be more hits when searching site 1 and no site", hitsOnSiteOneAndAbsent > hitsOnSiteOne);

        searchResponse = doSearchNoSiteFilter(originalQuery);
        int hitsWihoutSiteFilter = searchResponse.getNumberOfHits().intValue();

        assertEquals("Should be same result size", hitsOnSiteOneAndAbsent, hitsWihoutSiteFilter);
    }

    @Test
    public void resultShouldHaveAccandoForAbsentSiteId(){
        SearchResponse searchResponse = doSearchSiteIdAbsent(originalQuery);
        Collection accando = select(searchResponse.getSearchHits(), new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                return ((SearchResult) o).getTitle().contains("Accando");
            }
        });
        assertTrue("Should have size one", accando.size() == 1);
    }

    @Test
    @Ignore // search under 1 second is possible
    public void resultShouldHaveQueryTime(){
        SearchResponse searchResponse = doSearchSiteOne(originalQuery);
        assertTrue("QueryTime should be larger than 0", searchResponse.getQueryTime() > 0);
    }

    @Test
    public void resultShouldHaveSearchString(){
        SearchResponse searchResponse = doSearchSiteOne(originalQuery);
        assertEquals("Query should be equal", originalQuery, searchResponse.getQuery().getOriginalQuery());
    }

    @Test
    public void allResultsShouldHaveASinTitle(){
        SearchResponse searchResponse = doSearchSiteOne(originalQuery);
        doForAllhits(new Assertion() {
            public void doAssert(SearchResult searchResult) {
                assertTrue("Title did not contain 'as'", searchResult.getTitle().toLowerCase().contains("as"));
            }
        }, searchResponse);
    }

    @Test
    public void allResultsShouldHaveContentObjects(){
        SearchResponse searchResponse = doSearchSiteOne(originalQuery);
        doForAllhits(new Assertion() {
            public void doAssert(SearchResult searchResult) {
                assertTrue("Document was not Content", searchResult.getIndexedContentType().equals("aksess-document"));
            }
        }, searchResponse);
    }

    @Test
    public void allResultsShouldHaveId(){
        SearchResponse searchResponse = doSearchSiteOne(originalQuery);
        doForAllhits(new Assertion() {
            public void doAssert(SearchResult searchResult) {
                assertTrue("Search result id was 0", searchResult.getId() > 0);
            }
        }, searchResponse);
    }

    @Test
    public void allResultsShouldHaveUrl(){
        SearchResponse searchResponse = doSearchSiteOne(originalQuery);
        doForAllhits(new Assertion() {
            public void doAssert(SearchResult searchResult) {
                assertNotNull("Searchresult url was null", searchResult.getUrl());
            }
        }, searchResponse);
    }

    @Test
    public void allResultsShouldHaveSecurityId(){
        SearchResponse searchResponse = doSearchSiteOne(originalQuery);
        doForAllhits(new Assertion() {
            public void doAssert(SearchResult searchResult) {
                assertTrue("Result did not have securityid", searchResult.getSecurityId() > 0);
            }
        }, searchResponse);
    }

    @Test
    public void resultShouldHaveEmptyListIfNoSpellSuggestions(){
        SearchResponse searchResponse = doSearchSiteOne(originalQuery);
        assertNotNull("SpellSuggestions was null", searchResponse.getSpellSuggestions());
    }

    @Test
    public void searchResultShouldHaveSpellSuggestions(){
        SearchResponse response = doSearchSiteOne("kante");
        assertFalse("Search did not have spell suggestions", response.getSpellSuggestions().isEmpty());
    }

    @Test
    public void misSpelledWordShouldGetSuggestion(){
        SearchContext searchContext = getDummySearchContext();
        SearchQuery q = new SearchQuery(searchContext, "ell", "kantøga");
        List<String> suggest = searcher.suggest(q);
        assertFalse("No suggestions", suggest.isEmpty());
    }

    @Test
    public void descriptionShouldBehighlighted(){
        SearchContext searchContext = getDummySearchContext();
        SearchQuery q = new SearchQuery(searchContext, originalQuery);
        q.setHighlightSearchResultDescription(true);
        SearchResponse response = searcher.search(q);
        for(SearchResult searchResult : response.getSearchHits()){
            assertTrue(searchResult.getTitle() + " did not contain highlight", searchResult.getDescription().contains("<em class=\"highlight\""));
        }
    }

    @Test
    public void titleShouldBehighlighted(){
        SearchContext searchContext = getDummySearchContext();
        SearchQuery q = new SearchQuery(searchContext, originalQuery);
        q.setHighlightSearchResultDescription(true);
        SearchResponse response = searcher.search(q);
        for(SearchResult searchResult : response.getSearchHits()){
            assertTrue(searchResult.getTitle() + " did not contain highlight", searchResult.getTitle().contains("<em class=\"highlight\">")
                    || searchResult.getDescription().contains("<em class=\"highlight\">"));
        }
    }

    @Test
    public void testTitleStartingWith(){
        SearchContext searchContext = getDummySearchContext();
        SearchQuery q = new SearchQuery(searchContext, "title_no:Kan*");
        q.setHighlightSearchResultDescription(false);
        SearchResponse response = searcher.search(q);
        for(SearchResult searchResult : response.getSearchHits()){
            String title = searchResult.getTitle();
            assertTrue(title + " did not contain start with kan", title.toLowerCase().startsWith("kan"));
        }

        assertTrue("Number of hits should be larger than 0", response.getNumberOfHits().intValue() > 0);
    }

    @Test
    public void titleAndDescriptionShouldBeBoosted(){
        SearchResponse kantega = doSearch("kantega", "", "indexedContentType:herp-document");
        List<SearchResult> searchHits = kantega.getSearchHits();
        assertEquals("Wrong number of search results", 3, searchHits.size());
        assertEquals(5, searchHits.get(0).getId());
        assertEquals(6, searchHits.get(1).getId());
        assertEquals(7, searchHits.get(2).getId());
    }


    private void doForAllhits(Assertion assertion, SearchResponse searchResponse){

        List<SearchResult> documentHits = searchResponse.getSearchHits();
        for (SearchResult documentHit : documentHits) {
            assertion.doAssert(documentHit);
        }
    }

    private interface Assertion {
        void doAssert(SearchResult searchResult);
    }
}
