package no.kantega.openaksess.search.solr;

import no.kantega.search.api.search.SearchResult;
import no.kantega.search.api.search.Searcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/META-INF/spring/applicationContext-test.xml"})
public class SearcherIntegrationTest {
    @Autowired
    private Searcher searcher;

    @Test
    public void wildcardQueryShouldWork(){
        SearchResult search = searcher.search("elektronisk faktura");
        int numberOfHits = search.getNumberOfHits();
        assertTrue("Number of hits should be larger than 0", numberOfHits > 0);
    }

}
