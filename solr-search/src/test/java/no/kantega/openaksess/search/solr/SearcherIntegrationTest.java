package no.kantega.openaksess.search.solr;

import no.kantega.search.api.search.SearchResponse;
import no.kantega.search.api.search.SearchResult;
import no.kantega.search.api.search.Searcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/META-INF/spring/applicationContext-test.xml"})
public class SearcherIntegrationTest {
    @Autowired
    private Searcher searcher;
    private SearchResponse searchResponse;
    private final String query = "title_no:as";

    @Before
    public void doSearch(){
        searchResponse = searcher.search(query);
    }

    @Test
    public void resultShouldHaveHits(){
        assertTrue("Number of hits should be larger than 0", searchResponse.getNumberOfHits() > 0);
    }

    @Test
    public void resultShouldHaveQueryTime(){
        assertTrue("QueryTime should be larger than 0", searchResponse.getQueryTime() > 0);
    }

    @Test
    public void resultShouldHaveSearchString(){
        assertEquals("Query should be equal", query, searchResponse.getQuery());
    }

    @Test
    public void allResultsShouldHaveASinTitle(){
        List<SearchResult> documentHits = searchResponse.getDocumentHits();
        for (SearchResult documentHit : documentHits) {
            assertTrue(documentHit.getTitle().toLowerCase().contains("as"));
        }
    }
}
