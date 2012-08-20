package no.kantega.openaksess.search.solr;

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
import static no.kantega.openaksess.search.solr.Utils.getDummySearchContext;
import static org.apache.commons.collections.CollectionUtils.select;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/META-INF/spring/applicationContext-solrSearch-test.xml"})
public class SearcherFaceteIntegrationTest {
    @Autowired
    private Searcher searcher;

    @Test
    public void indexedContentTypeAsFacet(){
        SearchContext searchContext = getDummySearchContext();
        SearchQuery q = new SearchQuery(searchContext, "as", "indexedContentType:aksess-document");
        String indexedContentType = "indexedContentType";
        q.setFacetFields(Collections.singletonList(indexedContentType));
        SearchResponse search = searcher.search(q);
        Map<String,Collection<FacetResult>> facetFields = search.getFacets();
        assertEquals("Facet fields had wrong size", 1, facetFields.size());

        Collection<FacetResult> facetResults = facetFields.get(indexedContentType);
        assertEquals(1, facetResults.size());

        FacetResult next = facetResults.iterator().next();
        assertEquals("aksess-document", next.getValue());
        assertEquals(6L, next.getCount());
    }

    @Test
    public void dateRangeFacet() throws ParseException {
        SearchContext searchContext = getDummySearchContext();
        SearchQuery q = new SearchQuery(searchContext, "as", "indexedContentType:aksess-document");
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        q.setDateRangeFacets(Collections.singletonList(new DateRange("createDate", dateFormat.parse("01-01-2010"), dateFormat.parse("01-01-2012"), "+1MONTH")));
        SearchResponse search = searcher.search(q);
        Map<String, Collection<FacetResult>> rangeFacet = search.getFacets();
        assertFalse("Date facet was empty", rangeFacet.isEmpty());

        Collection<FacetResult> createDate = rangeFacet.get("createDate");
        assertEquals(5, createDate.size());
        for (FacetResult facet : createDate) {
            assertEquals(1, facet.getCount());
        }
    }

    @Test
    public void facetQuery(){
        SearchContext searchContext = getDummySearchContext();
        SearchQuery q = new SearchQuery(searchContext, "as", "indexedContentType:aksess-document");
        String oldContent = "createDate:[* TO 2011-12-30T23:59:59Z]";
        String newContent = "createDate:[2012-01-01T23:59:59Z TO *]";
        q.setFacetQueries(Arrays.asList(oldContent, newContent));
        SearchResponse search = searcher.search(q);
        Map<String, Collection<FacetResult>> facets = search.getFacets();
        assertFalse("Facet query result was empty", facets.isEmpty());

        Iterator<FacetResult> createDate = facets.get("createDate").iterator();
        FacetResult first = createDate.next();
        FacetResult second = createDate.next();
        assertEquals("[* TO 2011-12-30T23:59:59Z]", first.getValue());
        assertEquals("[2012-01-01T23:59:59Z TO *]", second.getValue());
        assertEquals(4, first.getCount());
        assertEquals(2, second.getCount());
    }

    @Test
    public void facetQuery2(){
        SearchContext searchContext = getDummySearchContext();
        SearchQuery q = new SearchQuery(searchContext, "as", "indexedContentType:aksess-document");
        q.setFacetQueries(asList("createDate:[NOW/DAY-7DAYS TO NOW]",
                "createDate:[NOW/MONTH-1MONTH TO NOW/DAY-7DAYS]",
                "createDate:[NOW/YEAR-1YEAR TO NOW/MONTH-1MONTH]",
                "createDate:[NOW/YEAR-3YEARS TO NOW/YEAR-1YEAR]",
                "createDate:[* TO NOW/YEAR-3YEARS]"));
        SearchResponse search = searcher.search(q);
        Map<String, Collection<FacetResult>> facets = search.getFacets();
        assertFalse("Facet query result was empty", facets.isEmpty());
    }



    @Test
    public void facetQueryDrilldown(){
        SearchContext searchContext = getDummySearchContext();
        SearchQuery q = new SearchQuery(searchContext, "as", "createDate:[* TO 2011-12-30T23:59:59Z]");
        q.setFacetFields(Arrays.asList("displayTemplateId"));
        SearchResponse search = searcher.search(q);

        assertEquals(4, search.getNumberOfHits());
        Collection<FacetResult> displayTemplateId = search.getFacets().get("displayTemplateId");
        assertEquals(3, displayTemplateId.size());

        Iterator<FacetResult> iterator = displayTemplateId.iterator();

        FacetResult first = iterator.next();
        assertEquals("1", first.getValue());
        assertEquals(2L, first.getCount());

        FacetResult second = iterator.next();
        assertEquals("2", second.getValue());
        assertEquals(1L, second.getCount());

        FacetResult third = iterator.next();
        assertEquals("3", third.getValue());
        assertEquals(1L, third.getCount());
    }

    @Test
    public void exploreLocationFacet(){
        SearchContext searchContext = getDummySearchContext();
        SearchQuery q = new SearchQuery(searchContext, "rett");
        q.setFacetFields(Arrays.asList("location"));
        SearchResponse search = searcher.search(q);

        assertEquals(3, search.getNumberOfHits());
        Collection<FacetResult> location = search.getFacets().get("location");
        assertEquals(4, location.size());
        assertEquals(1, select(location, getPredicate("/1", 3L)).size());
        assertEquals(1, select(location, getPredicate("/1/1", 1L)).size());
        assertEquals(1, select(location, getPredicate("/1/2", 1L)).size());
        assertEquals(1, select(location, getPredicate("/1/3", 1L)).size());

    }

    private Predicate getPredicate(final String path, final Number count) {
        return new Predicate() {
            public boolean evaluate(Object object) {
                FacetResult facetResult = (FacetResult) object;
                return facetResult.getValue().equals(path) && facetResult.getCount().equals(count);
            }
        };
    }
}
