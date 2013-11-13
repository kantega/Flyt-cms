package no.kantega.search.api.search;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Specifies a query to perform a search.
 * @see SearchResponse
 */
public class SearchQuery {
    public static final int DEFAULT_RESULTS_PER_PAGE= 50;

    private final SearchContext searchContext;
    private final String originalQuery;
    private List<String> filterQueries;
    private boolean highlightSearchResultDescription = true;
    private List<String> facetFields;
    private List<String> facetQueries;
    private List<DateRange> dateRangeFacets;
    private Integer resultsPerPage = DEFAULT_RESULTS_PER_PAGE;
    private Integer pageNumber = 0;
    private String groupField;
    private List<String> groupQueries;
    private int offset = 0;
    private boolean fuzzySearch = false;
    private boolean boostByPublishDate = false;
    private IndexedLanguage indexedLanguage = IndexedLanguage.NO;
    private boolean appendFiltersToPageUrls = true;
    private QueryType queryType = QueryType.Default;
    private List<String> additionalQueryFields;

    /**
     * Construct an query with a query string which typically comes from the user, and an
     * exact query to restrict/filter the results by.
     * @param searchContext - The context in which this query is executed
     * @param originalQuery - The original query, e.g "kino"
     * @param filterQueries - An exact query built to restrict the super set of documents that
     *                    can be returned, without influencing score.
     *                    e.g. "kommune:trondheim", where kommune is a field, results where only results with
     *                    value "trondheim" in kommune-field is returned.
     *                    This should follow the Solr query syntax,
     *                    http://wiki.apache.org/solr/SolrQuerySyntax and
     *                    http://lucene.apache.org/core/3_6_0/queryparsersyntax.html
     *
     */
    public SearchQuery(SearchContext searchContext, String originalQuery, List<String> filterQueries) {
        this.searchContext = searchContext;
        this.originalQuery = originalQuery;
        this.filterQueries = filterQueries;
    }

    public SearchQuery(SearchContext searchContext, String originalQuery, String... filterQueries) {
        this.searchContext = searchContext;
        this.originalQuery = originalQuery;
        this.filterQueries = Arrays.asList(filterQueries);
    }

    /**
     * Construct an query with a query string which typically comes from the user.
     * @param searchContext - The context in which this query is executed
     * @param originalQuery - The original query, e.g "kino"
     *
     */
    public SearchQuery(SearchContext searchContext, String originalQuery) {
        this.searchContext = searchContext;
        this.originalQuery = originalQuery;
    }

    public List<String> getFilterQueries() {
        if(filterQueries == null) return Collections.emptyList();
        return filterQueries;
    }

    public String getOriginalQuery() {
        return originalQuery;
    }

    public SearchContext getSearchContext() {
        return searchContext;
    }

    /**
     * @param highlightSearchResultDescription - set to true if highlighting should be performed.
     */
    public void setHighlightSearchResultDescription(boolean highlightSearchResultDescription) {
        this.highlightSearchResultDescription = highlightSearchResultDescription;
    }

    public boolean isHighlightSearchResultDescription() {
        return highlightSearchResultDescription;
    }

    public boolean useFacet() {
        return facetFields != null || facetQueries != null || dateRangeFacets != null;
    }

    public List<String> getFacetFields() {
        if(facetFields == null){
            return Collections.emptyList();
        }
        return facetFields;
    }

    /**
     * @param facetFields - The indexed fields facet to use facets for.
     */
    public void setFacetFields(List<String> facetFields) {
        this.facetFields = facetFields;
    }

    public List<String> getFacetQueries() {
        if(facetQueries == null){
            return Collections.emptyList();
        }
        return facetQueries;
    }

    public List<DateRange> getDateRangeFacets() {
        if(dateRangeFacets == null){
            dateRangeFacets = Collections.emptyList();
        }
        return dateRangeFacets;
    }

    /**
     * @param dateRangeFacets which should be used for the SearchResponse
     */
    public void setDateRangeFacets(List<DateRange> dateRangeFacets) {
        this.dateRangeFacets = dateRangeFacets;
    }

    /**
     * @param facetQueries which should be used for the SearchResponse.
     *                     http://wiki.apache.org/solr/SimpleFacetParameters#Facet_Fields_and_Facet_Queries
     */
    public void setFacetQueries(List<String> facetQueries) {
        this.facetQueries = facetQueries;
    }

    public void setResultsPerPage(Integer resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }

    public Integer getResultsPerPage() {
        return resultsPerPage;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setGroupField(String groupField) {
        this.groupField = groupField;
    }

    public String getGroupField() {
        return groupField;
    }

    public boolean getResultsAreGrouped() {
        return getGroupField() != null || !getGroupQueries().isEmpty();
    }

    public void setGroupQueries(String... query) {
        groupQueries = Arrays.asList(query);
    }

    public List<String> getGroupQueries() {
        if((groupQueries == null)) return Collections.emptyList();
        return groupQueries;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean isFuzzySearch() {
        return fuzzySearch;
    }

    public void setFuzzySearch(boolean fuzzySearch) {
        this.fuzzySearch = fuzzySearch;
    }

    /**
     * If set, adds boost query that adds boost to newer documents
     */
    public boolean isBoostByPublishDate() {
        return boostByPublishDate;
    }

    public void setBoostByPublishDate(boolean boostByPublishDate) {
        this.boostByPublishDate = boostByPublishDate;
    }

    public IndexedLanguage getIndexedLanguage() {
        return indexedLanguage;
    }

    public void setIndexedLanguage(IndexedLanguage indexedLanguage) {
        this.indexedLanguage = indexedLanguage;
    }

    public boolean isAppendFiltersToPageUrls() {
        return appendFiltersToPageUrls;
    }

    public void setAppendFiltersToPageUrls(boolean appendFiltersToPageUrls) {
        this.appendFiltersToPageUrls = appendFiltersToPageUrls;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    /**
     * By default only one all-containing field is queried. This field uses «standard» analyzing, so in some cases it
     * is useful to add custom fields that are handled in some special matter. It is also possible to specify
     * that this query should use <code>QueryType.Lucene</code>, but then boosting and such is lost.
     * @return fields that should be queried in addition to the all-containing field.
     */
    public List<String> getAdditionalQueryFields() {
        return additionalQueryFields == null ? Collections.<String>emptyList() : additionalQueryFields;
    }

    public void setAdditionalQueryFields(List<String> additionalQueryFields) {
        this.additionalQueryFields = additionalQueryFields;
    }
}
