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
        SearchQuery q = new SearchQuery(searchContext, "as", "*");
        String indexedContentType = "indexedContentType";
        q.setFacetFields(Collections.singletonList(indexedContentType));
        SearchResponse search = searcher.search(q);
        Map<String,List<Pair<String,Long>>> facetFields = search.getFacetFields();
        assertEquals("Facet fields had wrong size", 1, facetFields.size());

        List<Pair<String, Long>> pairs = facetFields.get(indexedContentType);
        assertEquals(1, pairs.size());

        assertEquals("aksess-document", pairs.get(0).first);
        assertEquals((Long)6L, pairs.get(0).second);
    }

    @Test
    public void dateRangeFacet() throws ParseException {
        SearchContext searchContext = new SearchContext() {};
        SearchQuery q = new SearchQuery(searchContext, "as", "*");
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        q.setDateRangeFacets(Collections.singletonList(new DateRange("createDate", dateFormat.parse("01-01-2010"), dateFormat.parse("01-01-2012"), "+1MONTH")));
        SearchResponse search = searcher.search(q);
        Map<String, List<Pair<String, Integer>>> rangeFacet = search.getRangeFacet();
        assertFalse("Date facet was empty", rangeFacet.isEmpty());

        List<Pair<String, Integer>> createDate = rangeFacet.get("createDate");
        assertEquals(5, createDate.size());
        for (Pair<String, Integer> facet : createDate) {
            assertEquals((Integer)1, facet.second);
        }
    }

    @Test
    public void facetQuery(){
        SearchContext searchContext = new SearchContext() {};
        SearchQuery q = new SearchQuery(searchContext, "as", "*");
        String oldContent = "createDate:[* TO 2011-12-30T23:59:59Z]";
        String newContent = "createDate:[2012-01-01T23:59:59Z TO *]";
        q.setFacetQueries(Arrays.asList(oldContent, newContent));
        SearchResponse search = searcher.search(q);
        List<Pair<String, Integer>> facetQuery = search.getFacetQuery();
        assertFalse("Facet query result was empty", facetQuery.isEmpty());

        assertEquals(oldContent, facetQuery.get(0).first);
        assertEquals(newContent, facetQuery.get(1).first);
        assertEquals((Integer)4, facetQuery.get(0).second);
        assertEquals((Integer)2, facetQuery.get(1).second);
    }

    @Test
    public void facetQueryDrilldown(){
        SearchContext searchContext = new SearchContext() {};
        SearchQuery q = new SearchQuery(searchContext, "as", "createDate:[* TO 2011-12-30T23:59:59Z]");
        q.setFacetFields(Arrays.asList("displayTemplateId"));
        SearchResponse search = searcher.search(q);

        assertEquals(4, search.getNumberOfHits());
        List<Pair<String, Long>> displayTemplateId = search.getFacetFields().get("displayTemplateId");
        assertEquals(3, displayTemplateId.size());

        assertEquals("1", displayTemplateId.get(0).first);
        assertEquals((Long)2L, displayTemplateId.get(0).second);

        assertEquals("2", displayTemplateId.get(1).first);
        assertEquals((Long)1L, displayTemplateId.get(1).second);

        assertEquals("3", displayTemplateId.get(2).first);
        assertEquals((Long)1L, displayTemplateId.get(2).second);

    }
}
