package no.kantega.openaksess.search.solr.search;

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
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.GroupParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

import static no.kantega.search.api.util.FieldUtils.getLanguageSuffix;


@Component
public class SolrSearcher implements Searcher {

    private final String DESCRIPTION_HIHLIGHTING_FIELD = "all_text_unanalyzed";
    @Autowired
    private SolrServer solrServer;

    private Map<String, SearchResultDecorator> resultDecoratorMap;
    private SearchResultDecorator defaultSearchResultDecorator;

    private final Pattern boundary = Pattern.compile("\\s");

    public SearchResponse search(SearchQuery query) {
        try {
            SolrQuery params = createSearchParams(query);

            QueryResponse queryResponse = solrServer.query(params);

            return createSearchReponse(query, queryResponse);
        } catch (SolrServerException e) {
            throw new IllegalStateException("Error when searching", e);
        }
    }

    private SearchResponse createSearchReponse(SearchQuery query, QueryResponse queryResponse) {
        SearchResponse searchResponse = null;
        if (!query.getResultsAreGrouped()) {
            SolrDocumentList results = queryResponse.getResults();
            searchResponse = new SearchResponse(query, results.getNumFound(), queryResponse.getQTime(), addSearchResults(query, queryResponse, results));
        } else {
            GroupResponse groupResponse = queryResponse.getGroupResponse();
            List<GroupCommand> values = groupResponse.getValues();
            for (GroupCommand value : values) {
                List<Group> groups = value.getValues();
                int matches = value.getMatches();

                List<GroupResultResponse> groupResultResponses = new ArrayList<>(groups.size());
                for (Group group : groups) {
                    String groupValue = group.getGroupValue();
                    SolrDocumentList result = group.getResult();
                    long numFound = result.getNumFound();
                    groupResultResponses.add(new GroupResultResponse(groupValue, numFound, addSearchResults(query, queryResponse, result)));
                }

                searchResponse = new SearchResponse(query, matches, queryResponse.getQTime(), groupResultResponses);
            }
        }

        setSpellResponse(searchResponse, queryResponse);

        addFacetResults(searchResponse, queryResponse);

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

        return solrQuery;
    }

    private String addFuzzyTermsIfSet(SearchQuery query) {
        String queryString = query.getOriginalQuery();
        if(query.isFuzzySearch()){
            StringBuilder fuzzyQuery = new StringBuilder();
            for (String token : boundary.split(queryString)) {
                fuzzyQuery.append(token);
                fuzzyQuery.append("~ ");
            }
            queryString = fuzzyQuery.toString();
        }
        return queryString;
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
        int firstColon = facetQueryString.indexOf(":");

        return new String[]{facetQueryString.substring(0, firstColon), facetQueryString.substring(firstColon + 1)};
    }

    private boolean addFacetResult(SearchQuery query, Multimap<String, FacetResult> facets, String facetFieldName, String facetFieldValue, Number facetCount) {
        return facets.put(facetFieldName, new FacetResult(facetFieldName, facetFieldValue, facetCount, QueryStringGenerator.getFacetUrl(facetFieldName, facetFieldValue, query)));
    }

    private void throwIfNotLenghtTwo(String facetQueryString, String[] facetFieldAndValue) {
        if(facetFieldAndValue.length != 2){
            throw new IllegalStateException(String.format("Splitting of facet query %s into field and query did not yield expected result." +
                    " Expected values of length 2, got %d: %s", facetQueryString, facetFieldAndValue.length, ArrayUtils.toString(facetFieldAndValue)));
        }
    }

    private void setHighlighting(SearchQuery query, SolrQuery params) {
        params.setHighlight(query.isHighlightSearchResultDescription());
        params.setHighlightSimplePre("<em class=\"highlight\">");
        params.setHighlightSimplePost("</em>");
        params.set("hl.fl", DESCRIPTION_HIHLIGHTING_FIELD, "title_no", "title_en");
        params.set("hl.useFastVectorHighlighter", true);
    }

    private List<SearchResult> addSearchResults(SearchQuery query, QueryResponse queryResponse, SolrDocumentList results) {
        List<SearchResult> searchResults = new ArrayList<>();
        for (SolrDocument result : results) {
            searchResults.add(createSearchResult(result, queryResponse, query));
        }
        return searchResults;
    }

    private void setSpellResponse(SearchResponse searchResponse, QueryResponse queryResponse) {
        SpellCheckResponse spellCheckResponse = queryResponse.getSpellCheckResponse();
        if(spellCheckResponse != null && !spellCheckResponse.isCorrectlySpelled()){
            List<String> suggestionStrings = getSpellSuggestions(spellCheckResponse);
            searchResponse.setSpellSuggestions(suggestionStrings);
        }
    }

    private List<String> getSpellSuggestions(SpellCheckResponse spellCheckResponse) {
        List<SpellCheckResponse.Suggestion> suggestions = spellCheckResponse.getSuggestions();
        List<String> suggestionStrings = new ArrayList<>();
        for (SpellCheckResponse.Suggestion suggestion : suggestions) {
            suggestionStrings.addAll(suggestion.getAlternatives());
        }
        return suggestionStrings;
    }

    private SearchResult createSearchResult(SolrDocument result, QueryResponse queryResponse, SearchQuery query) {
        String indexedContentType = (String) result.getFieldValue("indexedContentType");
        String language = (String) result.getFieldValue("language");
        String languageSuffix = getLanguageSuffix(language);
        TitleAndDescription titleAndDescription = getHighlightedTitleAndDescriptionIfEnabled(result, queryResponse, query, languageSuffix);

        SearchResultDecorator decorator = resultDecoratorMap.get(indexedContentType);
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
            if(thisResult != null){
                String description = highlightedValueOrDefault(result, thisResult, DESCRIPTION_HIHLIGHTING_FIELD);
                String title = highlightedValueOrDefault(result, thisResult, "title_" + languageSuffix);
                titleAndDescription = new TitleAndDescription(title, description);
            } else {
                titleAndDescription = notHighlightedTitleAndDescription(result, languageSuffix);
            }
        } else {
            titleAndDescription = notHighlightedTitleAndDescription(result, languageSuffix);

        }
        return titleAndDescription;
    }

    private String highlightedValueOrDefault(SolrDocument result, Map<String, List<String>> thisResult, String titleKey) {
        List<String> titleValue = thisResult.get(titleKey);
        String title;
        if (titleValue != null) {
            title = titleValue.get(0);
        } else {
            title = (String) result.getFirstValue(titleKey);
        }
        return title;
    }

    private TitleAndDescription notHighlightedTitleAndDescription(SolrDocument result, String languageSuffix) {
        TitleAndDescription titleAndDescription;
        String description = (String) result.getFieldValue("description_" + languageSuffix);
        String title = (String) result.getFieldValue("title_" + languageSuffix);
        titleAndDescription = new TitleAndDescription(title, description);
        return titleAndDescription;
    }

    @Autowired
    public void setResultDecorators(Collection<SearchResultDecorator> resultDecorators) {
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

    private class TitleAndDescription {
        final String title;
        final String description;

        public TitleAndDescription(String title, String description) {
            this.title = title;
            this.description = description;
        }
    }
}
