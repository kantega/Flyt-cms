package no.kantega.openaksess.search.solr;

import no.kantega.publishing.common.data.Content;
import no.kantega.search.api.search.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/META-INF/spring/applicationContext-test.xml"})
public class SearcherIntegrationTest {
    @Autowired
    private Searcher searcher;
    private SearchResponse searchResponse;
    private final String fullQuery = "title_no:as";
    private final String originalQuery = "as";

    @Before
    public void doSearch(){
        SearchContext searchContext = new SearchContext() {

        };
        SearchQuery q = new SearchQuery(searchContext, originalQuery);
        q.setFullQuery(fullQuery);
        searchResponse = searcher.search(q);
    }

    @Test
    public void resultShouldHaveHits(){
        assertTrue("Number of hits should be larger than 0", searchResponse.getNumberOfHits() > 0);
    }

    @Test
    @Ignore // search under 1 second is possible
    public void resultShouldHaveQueryTime(){
        assertTrue("QueryTime should be larger than 0", searchResponse.getQueryTime() > 0);
    }

    @Test
    public void resultShouldHaveSearchString(){
        assertEquals("Query should be equal", fullQuery, searchResponse.getQuery().getFullQuery());
        assertEquals("Query should be equal", originalQuery, searchResponse.getQuery().getOriginalQuery());
    }

    @Test
    public void allResultsShouldHaveASinTitle(){
        doForAllhits(new Assertion() {
            public void doAssert(SearchResult searchResult) {
                assertTrue("Title did not contain 'as'", searchResult.getTitle().toLowerCase().contains("as"));
            }
        });
    }

    @Test
    public void allResultsShouldHaveContentObjects(){
        doForAllhits(new Assertion() {
            public void doAssert(SearchResult searchResult) {
                assertTrue("Document was not Content", searchResult.getDocument() instanceof Content);
            }
        });
    }

    @Test
    public void allResultsShouldHaveId(){
        doForAllhits(new Assertion() {
            public void doAssert(SearchResult searchResult) {
                assertTrue("Search result id was 0", searchResult.getId() > 0);
            }
        });
    }

    @Test
    public void allResultsShouldHaveUrl(){
        doForAllhits(new Assertion() {
            public void doAssert(SearchResult searchResult) {
                assertNotNull("Searchresult url was null", searchResult.getUrl());
            }
        });
    }

    @Test
    public void allResultsShouldHaveSecurityId(){
        doForAllhits(new Assertion() {
            public void doAssert(SearchResult searchResult) {
                assertTrue("Result did not have securityid", searchResult.getSecurityId() > 0);
            }
        });
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
