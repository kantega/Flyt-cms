package no.kantega.openaksess.search.solr;

import no.kantega.search.api.search.SearchContext;
import no.kantega.search.api.search.SearchQuery;
import no.kantega.search.api.search.SearchResponse;
import no.kantega.search.api.search.Searcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;
import static no.kantega.openaksess.search.solr.Utils.getDummySearchContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/META-INF/spring/applicationContext-solrSearch-test.xml"})
public class GroupingIntegrationTest {
    @Autowired
    private Searcher searcher;

    @Test
    public void resultsShouldBeGroupedByIndexedContentType(){
        SearchContext searchContext = getDummySearchContext();
        SearchQuery q = new SearchQuery(searchContext, "as");

        q.setGroupField("indexedContentType");

        SearchResponse search = searcher.search(q);
        // Avanade has neglish as language, as is then ignored since it is an english stopword.
        assertEquals("Wrong number of results", 12, search.getNumberOfHits());
    }
}
