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
    private List<String> boostFunctions = Collections.emptyList();
    private List<String> boostQueries = Collections.emptyList();

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

    /**
     * @return the filter queries to apply when executing the query.
     */
    public List<String> getFilterQueries() {
        if(filterQueries == null) return Collections.emptyList();
        return filterQueries;
    }

    /**
     * @return the original query string from the user.
     */
    public String getOriginalQuery() {
        return originalQuery;
    }

    /**
     * @return the <code>SearchContext</code> the query is executed in.
     */
    public SearchContext getSearchContext() {
        return searchContext;
    }

    /**
     * @param highlightSearchResultDescription - set to true if highlighting should be performed.
     */
    public void setHighlightSearchResultDescription(boolean highlightSearchResultDescription) {
        this.highlightSearchResultDescription = highlightSearchResultDescription;
    }

    /**
     * @return whether highlighting is enabled for the result of this query.
     */
    public boolean isHighlightSearchResultDescription() {
        return highlightSearchResultDescription;
    }

    /**
     * @return whether faceting is enabled for the result of this query.
     */
    public boolean useFacet() {
        return facetFields != null || facetQueries != null || dateRangeFacets != null;
    }

    /**
     * @return the fields the results of this query should be faceted by.
     */
    public List<String> getFacetFields() {
        if(facetFields == null){
            return Collections.emptyList();
        }
        return facetFields;
    }

    /**
     * @param facetFields - The indexed fields facet to use facets for.
     * @see <a href="http://wiki.apache.org/solr/SimpleFacetParameters#Facet_Fields_and_Facet_Queries">SimpleFacetParameters</a>
     */
    public void setFacetFields(List<String> facetFields) {
        this.facetFields = facetFields;
    }

    /**
     * @return the queries to use when creating facets for the results of this query.
     */
    public List<String> getFacetQueries() {
        if(facetQueries == null){
            return Collections.emptyList();
        }
        return facetQueries;
    }

    /**
     * @return the date range facets the results of this query should be faceted by.
     */
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
     * @see <a href="http://wiki.apache.org/solr/SimpleFacetParameters#Facet_Fields_and_Facet_Queries">SimpleFacetParameters</a>
     */
    public void setFacetQueries(List<String> facetQueries) {
        this.facetQueries = facetQueries;
    }

    /**
     * @param resultsPerPage - the number of results to return.
     */
    public void setResultsPerPage(Integer resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }

    /**
     * @return how many results should be returned for each page.
     */
    public Integer getResultsPerPage() {
        return resultsPerPage;
    }

    /**
     * @param pageNumber the index of the result page wanted.
     */
    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * @return the page index this query should specify.
     */
    public Integer getPageNumber() {
        return pageNumber;
    }

    /**
     * @param groupField - name of the field the results should be grouped by.
     */
    public void setGroupField(String groupField) {
        this.groupField = groupField;
    }

    /**
     * @return the name of the field the results should be grouped by.
     */
    public String getGroupField() {
        return groupField;
    }

    /**
     * @return whether grouping is enabled for the results of this query.
     */
    public boolean getResultsAreGrouped() {
        return getGroupField() != null || !getGroupQueries().isEmpty();
    }

    /**
     * @param query - the queries that should be used to group the results of this query.
     */
    public void setGroupQueries(String... query) {
        groupQueries = Arrays.asList(query);
    }

    /**
     * @return the queries that should be used to group the results of this query.
     */
    public List<String> getGroupQueries() {
        if((groupQueries == null)) return Collections.emptyList();
        return groupQueries;
    }

    /**
     * @return how many results that should be skiped when returning the result of this query.
     */
    public int getOffset() {
        return offset;
    }

    /**
     * @param offset - how many results that should be skiped when returning the result of this query.
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * @return whether fuzzy search is enabled for this query.
     * @see SearchQuery#setFuzzySearch
     */
    public boolean isFuzzySearch() {
        return fuzzySearch;
    }

    /**
     * Fuzzy search add ~ to the query terms, this tells Lucene to match terms within a certain edit distance to the
     * original term.
     * @see <a href="http://lucene.apache.org/core/2_9_4/queryparsersyntax.html#Fuzzy%20Searches">Fuzzy Searches</a>
     * @param fuzzySearch - whether or not to enable fuzzy search. Default is false.
     */
    public void setFuzzySearch(boolean fuzzySearch) {
        this.fuzzySearch = fuzzySearch;
    }

    /**
     * If set, adds boost query that adds boost to newer documents.
     */
    public boolean isBoostByPublishDate() {
        return boostByPublishDate;
    }

    /**
     * If enabled the results will be boosted by the date it was published, newer documents thus get higher score
     * than older documents.
     * @param boostByPublishDate - enable boosting by the field publishDate.
     */
    public void setBoostByPublishDate(boolean boostByPublishDate) {
        this.boostByPublishDate = boostByPublishDate;
    }

    /**
     * @return the language this query should target.
     * @see SearchQuery#setIndexedLanguage
     */
    public IndexedLanguage getIndexedLanguage() {
        return indexedLanguage;
    }

    /**
     * Norwegian and english are indexed differently. For instance title is indexed in the field title_no if
     * the content is norwegian and title_en if english.
     * By default <code>IndexLanguage.NO</code> is used, to search in the english fields use <code>IndexLanguage.EN</code>
     * @param indexedLanguage - the language this query should target.
     */
    public void setIndexedLanguage(IndexedLanguage indexedLanguage) {
        this.indexedLanguage = indexedLanguage;
    }

    /**
     * @return whether appending filters to the next, previous and page urls should be done.
     * @see SearchQuery#setAppendFiltersToPageUrls
     * @see no.kantega.search.api.search.SearchQuery#getFilterQueries()
     */
    public boolean isAppendFiltersToPageUrls() {
        return appendFiltersToPageUrls;
    }

    /**
     * @param appendFiltersToPageUrls if true the filter specified by the query should be appended to the
     *                                urls for the next, last and page urls.
     */
    public void setAppendFiltersToPageUrls(boolean appendFiltersToPageUrls) {
        this.appendFiltersToPageUrls = appendFiltersToPageUrls;
    }

    /**
     * @return the type this query is.
     * @see QueryType
     */
    public QueryType getQueryType() {
        return queryType;
    }

    /**
     * @param queryType for this query.
     * @see QueryType
     */
    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    /**
     * @see no.kantega.search.api.search.SearchQuery#setAdditionalQueryFields(java.util.List)
     * @return fields that should be queried in addition to the all-containing field.
     */
    public List<String> getAdditionalQueryFields() {
        return additionalQueryFields == null ? Collections.<String>emptyList() : additionalQueryFields;
    }

    /**
     * By default only one all-containing field is queried. This field uses «standard» analyzing, so in some cases it
     * is useful to add custom fields that are handled in some special matter. It is also possible to specify
     * that this query should use <code>QueryType.Lucene</code>, but then boosting and such is lost.
     * @param additionalQueryFields the field to query in addition to the all-containing field.
     */
    public void setAdditionalQueryFields(List<String> additionalQueryFields) {
        this.additionalQueryFields = additionalQueryFields;
    }

    /**
     * @see no.kantega.search.api.search.SearchQuery#setBoostFunctions(java.util.List)
     * @return boost functions to apply to the search results.
     */
    public List<String> getBoostFunctions() {
        return boostFunctions;
    }

    /**
     * Functions to use when calculating the final score and relevancy of the search results.
     * If boostByPublishDate is activated the publishdate-function will be added to the
     * final list of functions.
     * @see <a href="http://wiki.apache.org/solr/FunctionQuery">FunctionQuery</a>
     * @see no.kantega.search.api.search.SearchQuery#setBoostByPublishDate(boolean)
     * @param boostFunctions to apply to the search results.
     */
    public void setBoostFunctions(List<String> boostFunctions) {
        this.boostFunctions = boostFunctions;
    }

    /**
     * @see no.kantega.search.api.search.SearchQuery#setBoostQueries(java.util.List)
     * @return the boostqueries to apply to the search results.
     */
    public List<String> getBoostQueries() {
        return boostQueries;
    }

    /**
     * Specify queries in normal Lucene format that when match boost a given result.
     * e.g. title:something
     * The boost queries specified here is added to the default queries.
     * The default queries among others «title_${query.indexedLanguage}:${raw query}»
     * @param boostQueries to apply to the search results.
     */
    public void setBoostQueries(List<String> boostQueries) {
        this.boostQueries = boostQueries;
    }
}
