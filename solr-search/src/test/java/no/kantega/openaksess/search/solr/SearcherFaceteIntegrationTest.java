package no.kantega.openaksess.search.solr;

import com.google.gdata.util.common.base.Pair;
import no.kantega.search.api.search.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/META-INF/spring/applicationContext-solrSearch-test.xml"})
public class SearcherFaceteIntegrationTest {
    @Autowired
    private Searcher searcher;

    @Test
    public void indexedContentTypeAsFacet(){
        SearchContext searchContext = new SearchContext() {};
        SearchQuery q = new SearchQuery(searchContext, "ell", "*");
        q.setFacetFields(Collections.singletonList("indexedContentType"));
        SearchResponse search = searcher.search(q);
        Map<String,List<Pair<String,Long>>> facetFields = search.getFacetFields();
        assertEquals("Facet fields had wrong size", 1, facetFields.size());
    }

    @Test
    public void dateRangeFacet() throws ParseException {
        SearchContext searchContext = new SearchContext() {};
        SearchQuery q = new SearchQuery(searchContext, "ell", "*");
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        q.setDateRangeFacets(Collections.singletonList(new DateRange("publishDate", dateFormat.parse("01-01-2010"), dateFormat.parse("01-01-2012"), "+1MONTH")));
        SearchResponse search = searcher.search(q);
        Map<String, List<Pair<String, Integer>>> rangeFacet = search.getRangeFacet();
        assertFalse("Date facet was empty", rangeFacet.isEmpty());
    }

    @Test
    public void facetQuery(){
        SearchContext searchContext = new SearchContext() {};
        SearchQuery q = new SearchQuery(searchContext, "ell", "*");
        q.setFacetQueries(Arrays.asList("publishDate:[* TO 2011-12-30T23:59:59Z]", "publishDate:[2012-01-01T23:59:59Z TO *]"));
        SearchResponse search = searcher.search(q);
        List<Pair<String, Integer>> facetQuery = search.getFacetQuery();
        assertFalse("Facete wuery result was empty", facetQuery.isEmpty());
    }
}
