package no.kantega.openaksess.search.solr;

import no.kantega.search.api.search.*;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.*;
import static no.kantega.openaksess.search.solr.Utils.getDummySearchContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/META-INF/spring/applicationContext-solrSearch-test.xml"})
public class SearcherIntegrationTest {
    @Autowired
    private Searcher searcher;
    private final String originalQuery = "as";

    private SearchResponse doSearchSiteOne(String query){
        return doSearch(query, "siteId:1");
    }

    private SearchResponse doSearch(String query, String filter){
        SearchContext searchContext = getDummySearchContext();
        SearchQuery q = new SearchQuery(searchContext, query, filter, "indexedContentType:aksess-document");
        q.setHighlightSearchResultDescription(true);
        return searcher.search(q);
    }

    @Test
    public void resultShouldHaveHits(){
        SearchResponse searchResponse = doSearchSiteOne(originalQuery);
        assertTrue("Number of hits should be larger than 0", searchResponse.getNumberOfHits().intValue() > 0);
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
        SearchResponse response = doSearchSiteOne("kan");
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
        for(SearchResult searchResult : response.getDocumentHits()){
            assertTrue(searchResult.getTitle() + " did not contain highlight", searchResult.getDescription().contains("<span class=\"highlight\""));
        }
    }

    @Test
    public void testTitleStartingWith(){
        SearchContext searchContext = getDummySearchContext();
        SearchQuery q = new SearchQuery(searchContext, "title_no:Kan*");
        SearchResponse response = searcher.search(q);
        for(SearchResult searchResult : response.getDocumentHits()){
            String title = searchResult.getTitle();
            assertTrue(title + " did not contain start with kan", title.toLowerCase().startsWith("kan"));
        }

        assertTrue("Number of hits should be larger than 0", response.getNumberOfHits().intValue() > 0);
    }



    private void doForAllhits(Assertion assertion, SearchResponse searchResponse){

        List<SearchResult> documentHits = searchResponse.getDocumentHits();
        for (SearchResult documentHit : documentHits) {
            assertion.doAssert(documentHit);
        }
    }

    private interface Assertion {
        void doAssert(SearchResult searchResult);
    }
}
