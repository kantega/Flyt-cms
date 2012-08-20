package no.kantega.openaksess.search.solr;

import com.google.gdata.util.common.base.Pair;
import no.kantega.search.api.search.*;
import org.apache.commons.collections.Predicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.apache.commons.collections.CollectionUtils.select;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/META-INF/spring/applicationContext-solrSearch-test.xml"})
public class SearcherFaceteIntegrationTest {
    @Autowired
    private Searcher searcher;

    @Test
    public void indexedContentTypeAsFacet(){
        SearchContext searchContext = getDummySearchContext();
        SearchQuery q = new SearchQuery(searchContext, "as", "*");
        String indexedContentType = "indexedContentType";
        q.setFacetFields(Collections.singletonList(indexedContentType));
        SearchResponse search = searcher.search(q);
        Map<String,Collection<FacetResult>> facetFields = search.getFacets();
        assertEquals("Facet fields had wrong size", 1, facetFields.size());

        Collection<FacetResult> facetResults = facetFields.get(indexedContentType);
        assertEquals(1, facetResults.size());

        assertEquals("aksess-document", facetResults.iterator().next());
        assertEquals((Long)6L, pairs.get(0).second);
    }

    @Test
    public void dateRangeFacet() throws ParseException {
        SearchContext searchContext = getDummySearchContext();
        SearchQuery q = new SearchQuery(searchContext, "as", "*");
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        q.setDateRangeFacets(Collections.singletonList(new DateRange("createDate", dateFormat.parse("01-01-2010"), dateFormat.parse("01-01-2012"), "+1MONTH")));
        SearchResponse search = searcher.search(q);
        Map<String, List<Pair<String, Integer>>> rangeFacet = search.getFacets();
        assertFalse("Date facet was empty", rangeFacet.isEmpty());

        List<Pair<String, Integer>> createDate = rangeFacet.get("createDate");
        assertEquals(5, createDate.size());
        for (Pair<String, Integer> facet : createDate) {
            assertEquals((Integer)1, facet.second);
        }
    }

    @Test
    public void facetQuery(){
        SearchContext searchContext = getDummySearchContext();
        SearchQuery q = new SearchQuery(searchContext, "as", "*");
        String oldContent = "createDate:[* TO 2011-12-30T23:59:59Z]";
        String newContent = "createDate:[2012-01-01T23:59:59Z TO *]";
        q.setFacetQueries(Arrays.asList(oldContent, newContent));
        SearchResponse search = searcher.search(q);
        List<Pair<String, Integer>> facetQuery = search.getFacets();
        assertFalse("Facet query result was empty", facetQuery.isEmpty());

        assertEquals(oldContent, facetQuery.get(0).first);
        assertEquals(newContent, facetQuery.get(1).first);
        assertEquals((Integer)4, facetQuery.get(0).second);
        assertEquals((Integer)2, facetQuery.get(1).second);
    }

    @Test
    public void facetQuery2(){
        SearchContext searchContext = getDummySearchContext();
        SearchQuery q = new SearchQuery(searchContext, "as", "*");
        q.setFacetQueries(asList("createDate:[NOW/DAY-7DAYS TO NOW]",
                "createDate:[NOW/MONTH-1MONTH TO NOW/DAY-7DAYS]",
                "createDate:[NOW/YEAR-1YEAR TO NOW/MONTH-1MONTH]",
                "createDate:[NOW/YEAR-3YEARS TO NOW/YEAR-1YEAR]",
                "createDate:[* TO NOW/YEAR-3YEARS]"));
        SearchResponse search = searcher.search(q);
        List<Pair<String, Integer>> facetQuery = search.getFacets();
        assertFalse("Facet query result was empty", facetQuery.isEmpty());
    }



    @Test
    public void facetQueryDrilldown(){
        SearchContext searchContext = getDummySearchContext();
        SearchQuery q = new SearchQuery(searchContext, "as", "createDate:[* TO 2011-12-30T23:59:59Z]");
        q.setFacetFields(Arrays.asList("displayTemplateId"));
        SearchResponse search = searcher.search(q);

        assertEquals(4, search.getNumberOfHits());
        List<Pair<String, Long>> displayTemplateId = search.getFacets().get("displayTemplateId");
        assertEquals(3, displayTemplateId.size());

        assertEquals("1", displayTemplateId.get(0).first);
        assertEquals((Long)2L, displayTemplateId.get(0).second);

        assertEquals("2", displayTemplateId.get(1).first);
        assertEquals((Long)1L, displayTemplateId.get(1).second);

        assertEquals("3", displayTemplateId.get(2).first);
        assertEquals((Long)1L, displayTemplateId.get(2).second);
    }

    @Test
    public void exploreLocationFacet(){
        SearchContext searchContext = getDummySearchContext();
        SearchQuery q = new SearchQuery(searchContext, "rett");
        q.setFacetFields(Arrays.asList("location"));
        SearchResponse search = searcher.search(q);

        assertEquals(3, search.getNumberOfHits());
        List<Pair<String, Long>> location = search.getFacets().get("location");
        assertEquals(3, location.size());
        assertEquals(1, select(location, getPredicate("/1/1", 1L)).size());
        assertEquals(1, select(location, getPredicate("/1/2", 1L)).size());
        assertEquals(1, select(location, getPredicate("/1/3", 1L)).size());

    }

    private Predicate getPredicate(final String path, final Long count) {
        return new Predicate() {
            public boolean evaluate(Object object) {
                Pair<String, Long> pair = (Pair<String, Long>) object;
                return pair.first.equals(path) && pair.second.equals(count);
            }
        };
    }

    private SearchContext getDummySearchContext() {
        return new SearchContext() {
            public String getSearchUrl() {
                return "";
            }
        };
    }
}
