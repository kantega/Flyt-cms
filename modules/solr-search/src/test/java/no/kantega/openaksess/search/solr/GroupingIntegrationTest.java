package no.kantega.openaksess.search.solr;

import no.kantega.search.api.search.*;
import org.apache.commons.collections.Predicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.kantega.openaksess.search.solr.Utils.getDummySearchContext;
import static org.apache.commons.collections.CollectionUtils.select;
import static org.junit.Assert.assertEquals;

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

        assertEquals("Wrong number of content types", 4, search.getGroupResultResponses().size());

        assertEquals("Wrong number of results", 17, search.getNumberOfHits());
        GroupResultResponse aksessDocument = (GroupResultResponse) select(search.getGroupResultResponses(), getGroupValuePredicate("aksess-document")).iterator().next();
        assertEquals("Wrong number of results", 6, aksessDocument.getNumFound().intValue());

        GroupResultResponse derpDocument = (GroupResultResponse) select(search.getGroupResultResponses(), getGroupValuePredicate("derp-document")).iterator().next();
        assertEquals("Wrong number of results", 6, derpDocument.getNumFound().intValue());

    }

    private Predicate getGroupValuePredicate(final String groupValue) {
        return new Predicate() {
            public boolean evaluate(Object object) {
                GroupResultResponse groupResultResponse = (GroupResultResponse) object;
                return groupResultResponse.getGroupValue().equals(groupValue);
            }
        };
    }
}
