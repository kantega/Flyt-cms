package no.kantega.openaksess.search.solr.search;

import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import no.kantega.openaksess.search.solr.provider.DefaultSearchResultDecorator;
import no.kantega.search.api.provider.SearchResultDecorator;
import no.kantega.search.api.search.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.*;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.DisMaxParams;
import org.apache.solr.common.params.GroupParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.search.ExtendedDismaxQParserPlugin;
import org.apache.solr.search.QParserPlugin;
import org.apache.solr.search.QueryParsing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

import static no.kantega.search.api.util.FieldUtils.getLanguageSuffix;


@Component
public class SolrSearcher implements Searcher {
    private final Logger log  = LoggerFactory.getLogger(getClass());

    @Value("${search.boostByPublishDateQuery:recip(ms(NOW/HOUR,publishDate),3.16e-11,1,1)}")
    private String boostByPublishDateQuery;

    @Autowired
    private SolrServer solrServer;

    private Map<String, SearchResultDecorator<?>> resultDecoratorMap;
    private SearchResultDecorator<?> defaultSearchResultDecorator;

    private final Pattern boundary = Pattern.compile("\\s");

    @Value("#{runtimeMode?.name() == 'DEVELOPMENT'}")
    private boolean includeDebugInfo = false;

    @Autowired
    private SearchResultFilter resultFilter;


    public SearchResponse search(SearchQuery query) {
        try {
            SolrQuery params = createSearchParams(query);

            QueryResponse queryResponse = solrServer.query(params);

            SearchResponse searchReponse = createSearchReponse(query, queryResponse);

            resultFilter.filterSearchResponse(searchReponse);

            return searchReponse;
        } catch (SolrServerException e) {
            throw new IllegalStateException("Error when searching", e);
        }
    }

    private SearchResponse createSearchReponse(SearchQuery query, QueryResponse queryResponse) {
        SearchResponse searchResponse;
        if (query.getResultsAreGrouped()) {
            searchResponse = createGroupSearchResponse(query, queryResponse);
        } else {
            SolrDocumentList results = queryResponse.getResults();
            searchResponse = new SearchResponse(query, results.getNumFound(), queryResponse.getQTime(), addSearchResults(query, queryResponse, results));
        }

        setSpellResponse(searchResponse, queryResponse);

        addFacetResults(searchResponse, queryResponse);

        if(includeDebugInfo){
            addDebugInfo(searchResponse, queryResponse);
        }

        return searchResponse;
    }

    private SearchResponse createGroupSearchResponse(SearchQuery query, QueryResponse queryResponse) {
        SearchResponse searchResponse;GroupResponse groupResponse = queryResponse.getGroupResponse();
        List<GroupCommand> values = groupResponse.getValues();
        List<GroupResultResponse> groupResultResponses = new ArrayList<>(values.size());

        int matches = 0;
        for (GroupCommand value : values) {
            List<Group> groups = value.getValues();
            matches = value.getMatches();


            for (Group group : groups) {
                String groupValue = group.getGroupValue();
                SolrDocumentList result = group.getResult();
                long numFound = result.getNumFound();
                groupResultResponses.add(new GroupResultResponse(groupValue, numFound, addSearchResults(query, queryResponse, result)));
            }

        }
        searchResponse = new SearchResponse(query, matches, queryResponse.getQTime(), groupResultResponses);
        return searchResponse;
    }

    private SolrQuery createSearchParams(SearchQuery query) {
        SolrQuery solrQuery = new SolrQuery(addFuzzyTermsIfSet(query));
        setFilterQueryIfPresent(query, solrQuery);

        Integer resultsPerPage = query.getResultsPerPage();
        solrQuery.setRows(resultsPerPage);
        solrQuery.setStart(query.getOffset() + query.getPageNumber() * resultsPerPage);
        solrQuery.set("spellcheck", "on");

        setHighlighting(query, solrQuery);

        addFacetQueryInformation(query, solrQuery);

        addResultGrouping(query, solrQuery);
        solrQuery.add("fl", Joiner.on(",").join(query.getResultFields()));

        // Define what fields the search result hits will contain
        List<String> resultFields = new ArrayList<>();
        resultFields.add("uid");
        resultFields.add("id");
        resultFields.add("associationId");
        resultFields.add("parentId");
        resultFields.add("securityId");
        resultFields.add("title_*");
        resultFields.add("description*");
        resultFields.add("indexedContentType");
        resultFields.add("author");
        resultFields.add("url");
        resultFields.add("language");

        // Adding custom result fields
        resultFields.addAll(query.getResultFields());

        solrQuery.add("fl", resultFields.toArray(new String[resultFields.size()]));

        if (query.getQueryType() == QueryType.Default) {
            solrQuery.add(QueryParsing.DEFTYPE, ExtendedDismaxQParserPlugin.NAME);

            solrQuery.add( DisMaxParams.BF, getBoostFunctions(query));

            String field = "all_text_" + query.getIndexedLanguage().code;
            solrQuery.add(DisMaxParams.QF, getQueryFields(query, field));
            solrQuery.add(DisMaxParams.PF, field);
            solrQuery.add(DisMaxParams.PF2, field);
            solrQuery.add(DisMaxParams.PF3, field);
            solrQuery.add(DisMaxParams.PS, "10");
            solrQuery.add(DisMaxParams.MM, "1");

            solrQuery.add(DisMaxParams.BQ, getBoostQueries(query.getBoostQueries(), query.getOriginalQuery(), query.getIndexedLanguage().code));
        } else {
            solrQuery.add(QueryParsing.DEFTYPE, QParserPlugin.DEFAULT_QTYPE);
        }

        if (includeDebugInfo) {
            solrQuery.setShowDebugInfo(true);
        }

        return solrQuery;
    }

    private String[] getBoostFunctions(SearchQuery query) {
        List<String> boostFunctions = new ArrayList<>(query.getBoostFunctions());
        if (query.isBoostByPublishDate()) {
            boostFunctions.add( boostByPublishDateQuery );
        }
        return boostFunctions.toArray(new String[boostFunctions.size()]);
    }

    private String[] getQueryFields(SearchQuery query, String allTextField) {
        List<String> additionalQueryFields = query.getAdditionalQueryFields();
        int size = additionalQueryFields.size() + 1;
        List<String> queryFields = new ArrayList<>(size);

        queryFields.addAll(additionalQueryFields);
        queryFields.add(allTextField);
        return queryFields.toArray(new String[size]);
    }

    private String[] getBoostQueries(List<String> additionalBoostQueries, String query, String language) {
        String[] terms = boundary.split(query);
        List<String> boostQueries = new ArrayList<>(additionalBoostQueries.size() + terms.length * 6);
        boostQueries.addAll(additionalBoostQueries);

        for (String term : terms) {
            String escapedTerm = ClientUtils.escapeQueryChars(term);
            boostQueries.add("all_text_unanalyzed:" + escapedTerm);
            boostQueries.add("title_" + language + ":" + escapedTerm);
            boostQueries.add("altTitle_" + language + ":" + escapedTerm);
            boostQueries.add("description_" + language + ":" + escapedTerm);
            boostQueries.add("keywords:" + escapedTerm);
            boostQueries.add("topics:" + escapedTerm);
        }

        return boostQueries.toArray(new String[boostQueries.size()]);
    }

    public List<String> suggest(SearchQuery query) {
        return suggest(query, "/suggest");
    }

    @Override
    public List<String> spell(SearchQuery query) {
        return suggest(query, "/spellcheck");
    }

    private List<String> suggest(SearchQuery query, String handler) {
        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set("qt", handler);
        params.set("q", query.getOriginalQuery());
        Integer resultsPerPage = query.getResultsPerPage();
        params.set("rows", resultsPerPage);
        params.set("start", query.getPageNumber() * resultsPerPage);
        params.set("spellcheck", "on");
        try {
            QueryResponse queryResponse = solrServer.query(params);
            return  getSpellSuggestions(queryResponse.getSpellCheckResponse());
        } catch (SolrServerException e) {
            throw new IllegalStateException("Error when searching", e);
        }
    }

    private void addResultGrouping(SearchQuery query, SolrQuery solrQuery) {
        if (query.getResultsAreGrouped()) {
            solrQuery.set(GroupParams.GROUP, true);
            String groupField = query.getGroupField();
            if (groupField != null) {
                solrQuery.set(GroupParams.GROUP_FIELD, groupField);
            }

            List<String> groupQueries = query.getGroupQueries();
            if(!groupQueries.isEmpty()){
                solrQuery.set(GroupParams.GROUP_QUERY, groupQueries.toArray(new String[groupQueries.size()]));
            }
            Integer resultsPerPage = query.getResultsPerPage();
            solrQuery.set(GroupParams.GROUP_LIMIT, resultsPerPage);
            solrQuery.set(GroupParams.GROUP_OFFSET, query.getPageNumber() * resultsPerPage);
        }
    }

    private void setFilterQueryIfPresent(SearchQuery query, SolrQuery params) {
        List<String> filterQuery = query.getFilterQueries();
        if(filterQuery != null){
            params.setFilterQueries(filterQuery.toArray(new String[filterQuery.size()]));
        }
    }

    private void addFacetQueryInformation(SearchQuery query, SolrQuery params) {
        boolean useFacet = query.useFacet();
        params.setFacet(useFacet);
        if (useFacet) {
            params.setFacetMinCount(1);
            for(String field : query.getFacetFields()){
                params.addFacetField(field);
            }
            for(String facetquery : query.getFacetQueries()){
                params.addFacetQuery(facetquery);
            }
            for (DateRange dateRange : query.getDateRangeFacets()){
                params.addDateRangeFacet(dateRange.getField(), dateRange.getFrom(), dateRange.getTo(), dateRange.getGap());
            }
            for(String facetQuery : query.getFacetQueries()){
                params.addFacetQuery(facetQuery);
            }
        }
    }

    private void addFacetResults(SearchResponse searchResponse, QueryResponse queryResponse) {
        SearchQuery query = searchResponse.getQuery();
        Multimap<String,FacetResult> facets = ArrayListMultimap.create();
        List<FacetField> facetFields = queryResponse.getFacetFields();
        if (facetFields != null) {
            for(FacetField facetField : facetFields){
                for(FacetField.Count count : facetField.getValues()){
                    String facetFieldName = facetField.getName();
                    String facetFieldValue = count.getName();
                    long facetCount = count.getCount();

                    addFacetResult(query, facets, facetFieldName, facetFieldValue, facetCount);
                }
            }
        }
        List<RangeFacet> facetRanges = queryResponse.getFacetRanges();
        if(facetRanges != null){
            for(RangeFacet facetRange : facetRanges){
                List<RangeFacet.Count> counts = facetRange.getCounts();
                for (RangeFacet.Count count : counts) {
                    String facetFieldName = facetRange.getName();
                    String facetFieldValue = count.getValue();
                    int facetCount = count.getCount();

                    addFacetResult(query, facets, facetFieldName, facetFieldValue, facetCount);
                }
            }
        }

        Map<String, Integer> facetQuery = queryResponse.getFacetQuery();
        if(facetQuery != null){
            for (Map.Entry<String, Integer> facetQueryEntry : facetQuery.entrySet()) {
                String facetQueryString = facetQueryEntry.getKey();
                String[] facetFieldAndValue = splitOnFirstColon(facetQueryString);

                throwIfNotLenghtTwo(facetQueryString, facetFieldAndValue);
                String facetFieldName = facetFieldAndValue[0];
                String facetFieldValue = facetFieldAndValue[1];
                Integer facetCount = facetQueryEntry.getValue();

                addFacetResult(query, facets, facetFieldName, facetFieldValue, facetCount);
            }
        }
        searchResponse.setFacets(facets.asMap());
    }

    private String[] splitOnFirstColon(String facetQueryString) {
        int firstColon = facetQueryString.indexOf(':');

        return new String[]{facetQueryString.substring(0, firstColon), facetQueryString.substring(firstColon + 1)};
    }

    private boolean addFacetResult(SearchQuery query, Multimap<String, FacetResult> facets, String facetFieldName, String facetFieldValue, Number facetCount) {
        return facets.put(facetFieldName, new FacetResult(facetFieldName, facetFieldValue, facetCount, QueryStringGenerator.getFacetUrl(facetFieldName, facetFieldValue, query, query.isAppendFiltersToPageUrls())));
    }

    private void throwIfNotLenghtTwo(String facetQueryString, String[] facetFieldAndValue) {
        if(facetFieldAndValue.length != 2){
            throw new IllegalStateException(String.format("Splitting of facet query %s into field and query did not yield expected result." +
                    " Expected values of length 2, got %d: %s", facetQueryString, facetFieldAndValue.length, ArrayUtils.toString(facetFieldAndValue)));
        }
    }

    private void setHighlighting(SearchQuery query, SolrQuery params) {
        params.setHighlight(query.isHighlightSearchResultDescription());
        String lang = query.getIndexedLanguage().code;
        params.set("hl.fl", "all_text_" + lang, "title_" + lang);
        params.set("hl.useFastVectorHighlighter", true);
    }

    private List<SearchResult> addSearchResults(SearchQuery query, QueryResponse queryResponse, SolrDocumentList results) {
        List<SearchResult> searchResults = new ArrayList<>(results.size());
        for (SolrDocument result : results) {
            try {
                searchResults.add(createSearchResult(result, queryResponse, query));
            } catch (Exception e) {
                log.error("Error adding result for document:" + result, e);
            }
        }
        return searchResults;
    }

    private void setSpellResponse(SearchResponse searchResponse, QueryResponse queryResponse) {
        SpellCheckResponse spellCheckResponse = queryResponse.getSpellCheckResponse();
        if(spellCheckResponse != null && !spellCheckResponse.isCorrectlySpelled()){
            List<String> suggestionStrings = new ArrayList<>();
            // Remove duplicate suggestions caused by searching for both exact and fuzzy words
            for (String s : getSpellSuggestions(spellCheckResponse)) {
                if (!suggestionStrings.contains(s)) {
                    suggestionStrings.add(s);
                }
            }
            searchResponse.setSpellSuggestions(suggestionStrings);
        }
    }

    private List<String> getSpellSuggestions(SpellCheckResponse spellCheckResponse) {
        List<String> suggestionStrings = new ArrayList<>();
        if (spellCheckResponse != null) {
            List<SpellCheckResponse.Suggestion> suggestions = spellCheckResponse.getSuggestions();
            for (SpellCheckResponse.Suggestion suggestion : suggestions) {
                suggestionStrings.addAll(suggestion.getAlternatives());
            }
        }
        return suggestionStrings;
    }

    private SearchResult createSearchResult(SolrDocument result, QueryResponse queryResponse, SearchQuery query) {
        String indexedContentType = (String) result.getFieldValue("indexedContentType");
        String language = (String) result.getFieldValue("language");
        String languageSuffix = getLanguageSuffix(language);
        TitleAndDescription titleAndDescription = getHighlightedTitleAndDescriptionIfEnabled(result, queryResponse, query, languageSuffix);

        SearchResultDecorator<?> decorator = resultDecoratorMap.get(indexedContentType);
        if(decorator == null){
            decorator = defaultSearchResultDecorator;
        }

        return decorator.decorate(result, titleAndDescription.title, titleAndDescription.description, query);
    }

    private TitleAndDescription getHighlightedTitleAndDescriptionIfEnabled(SolrDocument result, QueryResponse queryResponse, SearchQuery query, String languageSuffix) {
        TitleAndDescription titleAndDescription;
        if(query.isHighlightSearchResultDescription()){
            Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
            Map<String, List<String>> thisResult = highlighting.get((String) result.getFieldValue("uid"));
            if(thisResult != null && !thisResult.isEmpty()){
                String description = highlightedValueOrDefault(result, thisResult, "all_text_" + languageSuffix, "description_" + languageSuffix);
                String titleFieldName = "title_" + languageSuffix;
                String title = highlightedValueOrDefault(result, thisResult, titleFieldName, titleFieldName);
                titleAndDescription = new TitleAndDescription(title, description);
            } else {
                titleAndDescription = notHighlightedTitleAndDescription(result, languageSuffix);
            }
        } else {
            titleAndDescription = notHighlightedTitleAndDescription(result, languageSuffix);

        }
        return titleAndDescription;
    }

    private String highlightedValueOrDefault(SolrDocument result, Map<String, List<String>> thisResult, String fieldKey, String fallbackField) {
        List<String> highlightedValue = thisResult.get(fieldKey);
        String fieldValue;
        if (highlightedValue != null) {
            fieldValue = highlightedValue.get(0);
        } else {
            fieldValue = (String) result.getFirstValue(fallbackField);
        }
        return fieldValue;
    }

    private TitleAndDescription notHighlightedTitleAndDescription(SolrDocument result, String languageSuffix) {
        String title = (String) result.getFieldValue("title_" + languageSuffix);
        String description = (String) result.getFieldValue("description_" + languageSuffix);
        return new TitleAndDescription(title, description);
    }

    private void addDebugInfo(SearchResponse searchResponse, QueryResponse queryResponse) {
        searchResponse.setDebugInformation(queryResponse.getDebugMap().toString());
    }

    @Autowired
    public void setResultDecorators(Collection<SearchResultDecorator<?>> resultDecorators) {
        this.resultDecoratorMap = new HashMap<>();
        for (SearchResultDecorator<?> resultDecorator : resultDecorators) {
            for(String handledindexedContentType : resultDecorator.handledindexedContentTypes()){
                throwIfIsHandledAlready(handledindexedContentType);

                if (handledindexedContentType.equals(DefaultSearchResultDecorator.DEFAULT_INDEXED_CONTENT_TYPE)) {
                    defaultSearchResultDecorator = resultDecorator;
                } else {
                    resultDecoratorMap.put(handledindexedContentType, resultDecorator);
                }
            }
        }
    }

    private void throwIfIsHandledAlready(String handledindexedContentType) {
        if(resultDecoratorMap.containsKey(handledindexedContentType)){
            throw new IllegalStateException("Several SearchResultDecorators handle indexedContentType " +
                    handledindexedContentType + " only one is allowed");
        }
    }

    private static class TitleAndDescription {
        final String title;
        final String description;

        public TitleAndDescription(String title, String description) {
            this.title = title;
            this.description = description;
        }
    }

    private String addFuzzyTermsIfSet(SearchQuery query) {
        String queryString = query.getOriginalQuery();
        if(query.isFuzzySearch()){
            StringBuilder fuzzyQuery = new StringBuilder();
            for (String token : boundary.split(queryString)) {
                // Search for exact string and fuzzy string since search for only fuzzy string does not return exact matches
                fuzzyQuery.append("(");
                fuzzyQuery.append(token);
                fuzzyQuery.append(" OR ");
                fuzzyQuery.append(token);
                fuzzyQuery.append("~) ");
            }
            queryString = fuzzyQuery.toString();
        }
        return queryString;
    }
}
