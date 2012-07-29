package no.kantega.openaksess.search.solr;

import no.kantega.publishing.common.data.Content;
import no.kantega.search.api.search.*;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/META-INF/spring/applicationContext-solrSearch-test.xml"})
public class SearcherIntegrationTest {
    @Autowired
    private Searcher searcher;
    private SearchResponse searchResponse;
    private final String fullQuery = "title_no:as";
    private final String originalQuery = "as";

    private SearchResponse doSearch(String fullQuery){
        SearchContext searchContext = new SearchContext() {};
        SearchQuery q = new SearchQuery(searchContext, originalQuery, fullQuery);
        return searcher.search(q);
    }

    @Test
    public void resultShouldHaveHits(){
        searchResponse = doSearch(fullQuery);
        assertTrue("Number of hits should be larger than 0", searchResponse.getNumberOfHits() > 0);
    }

    @Test
    @Ignore // search under 1 second is possible
    public void resultShouldHaveQueryTime(){
        searchResponse = doSearch(fullQuery);
        assertTrue("QueryTime should be larger than 0", searchResponse.getQueryTime() > 0);
    }

    @Test
    public void resultShouldHaveSearchString(){
        searchResponse = doSearch(fullQuery);
        assertEquals("Query should be equal", fullQuery, searchResponse.getQuery().getFullQuery());
        assertEquals("Query should be equal", originalQuery, searchResponse.getQuery().getOriginalQuery());
    }

    @Test
    public void allResultsShouldHaveASinTitle(){
        searchResponse = doSearch(fullQuery);
        doForAllhits(new Assertion() {
            public void doAssert(SearchResult searchResult) {
                assertTrue("Title did not contain 'as'", searchResult.getTitle().toLowerCase().contains("as"));
            }
        });
    }

    @Test
    public void allResultsShouldHaveContentObjects(){
        searchResponse = doSearch(fullQuery);
        doForAllhits(new Assertion() {
            public void doAssert(SearchResult searchResult) {
                assertTrue("Document was not Content", searchResult.getDocument() instanceof Content);
            }
        });
    }

    @Test
    public void allResultsShouldHaveId(){
        searchResponse = doSearch(fullQuery);
        doForAllhits(new Assertion() {
            public void doAssert(SearchResult searchResult) {
                assertTrue("Search result id was 0", searchResult.getId() > 0);
            }
        });
    }

    @Test
    public void allResultsShouldHaveUrl(){
        searchResponse = doSearch(fullQuery);
        doForAllhits(new Assertion() {
            public void doAssert(SearchResult searchResult) {
                assertNotNull("Searchresult url was null", searchResult.getUrl());
            }
        });
    }

    @Test
    public void allResultsShouldHaveSecurityId(){
        searchResponse = doSearch(fullQuery);
        doForAllhits(new Assertion() {
            public void doAssert(SearchResult searchResult) {
                assertTrue("Result did not have securityid", searchResult.getSecurityId() > 0);
            }
        });
    }

    @Test
    public void resultShouldHaveEmptyListIfNoSpellSuggestions(){
        searchResponse = doSearch(fullQuery);
        assertNotNull("SpellSuggestions was null", searchResponse.getSpellSuggestions());
    }

    @Test
    public void searchResultShouldHaveSpellSuggestions(){
        searchResponse = doSearch(fullQuery);
        SearchResponse response = doSearch("title_no:elektroni");
        assertFalse("Search did not have spell suggestions", response.getSpellSuggestions().isEmpty());
    }

    @Test
    public void misSpelledWordShouldGetSuggestion(){
        SearchContext searchContext = new SearchContext() {};
        SearchQuery q = new SearchQuery(searchContext, "ell", "title_no:ell");
        List<String> suggest = searcher.suggest(q);
        assertFalse("No suggestions", suggest.isEmpty());
    }

    @Test
    public void descriptionShouldBehighlighted(){
        SearchContext searchContext = new SearchContext() {};
        SearchQuery q = new SearchQuery(searchContext, originalQuery, "description_no:beskrivelse");
        q.setHighlightSearchResultDescription(true);
        SearchResponse response = searcher.search(q);
        for(SearchResult searchResult : response.getDocumentHits()){
            assertTrue(searchResult.getDescription().contains("<span class=\"highlight\""));
        }
    }

    private void doForAllhits(Assertion assertion){
        List<SearchResult> documentHits = searchResponse.getDocumentHits();
        for (SearchResult documentHit : documentHits) {
            assertion.doAssert(documentHit);
        }
    }

    private interface Assertion {
        void doAssert(SearchResult searchResult);
    }
}
