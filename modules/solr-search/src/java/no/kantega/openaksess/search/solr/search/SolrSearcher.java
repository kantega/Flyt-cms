package no.kantega.openaksess.search.solr.search;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static no.kantega.openaksess.search.solr.index.SolrDocumentIndexer.getLanguageSuffix;

@Component
public class SolrSearcher implements Searcher {

    @Autowired
    private SolrServer solrServer;

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

                List<GroupResultResponse> groupResultResponses = new ArrayList<GroupResultResponse>(groups.size());
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
        params.setHighlightSimplePre("<span class=\"highlight\"/>");
        params.setHighlightSimplePost("</span>");
        params.set("hl.fl", "description*");
    }

    private List<SearchResult> addSearchResults(SearchQuery query, QueryResponse queryResponse, SolrDocumentList results) {
        List<SearchResult> searchResults = new ArrayList<SearchResult>();
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
        List<String> suggestionStrings = new ArrayList<String>();
        for (SpellCheckResponse.Suggestion suggestion : suggestions) {
            suggestionStrings.addAll(suggestion.getAlternatives());
        }
        return suggestionStrings;
    }

    private SearchResult createSearchResult(SolrDocument result, QueryResponse queryResponse, SearchQuery query) {
        String language = (String) result.getFieldValue("language");
        String languageSuffix = getLanguageSuffix(language);
        String description = getHighlightedDescriptionIfEnabled(result, queryResponse, query, languageSuffix);
        return new SearchResult((Integer) result.getFieldValue("id"),
                (Integer) result.getFieldValue("securityId"),
                (String) result.getFieldValue("indexedContentType"),
                (String) result.getFieldValue("title_" + languageSuffix),
                description,
                (String) result.getFieldValue("author"),
                (String) result.getFieldValue("url"),
                (Integer) result.getFieldValue("parentId"));
    }

    private String getHighlightedDescriptionIfEnabled(SolrDocument result, QueryResponse queryResponse, SearchQuery query, String languageSuffix) {
        String fieldname = "description_" + languageSuffix;
        if(query.isHighlightSearchResultDescription()){
            Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
            Map<String, List<String>> uid = highlighting.get((String) result.getFieldValue("uid"));
            if(uid != null){
                List<String> highlightedDescription = uid.get(fieldname);
                if(highlightedDescription != null && !highlightedDescription.isEmpty()){
                    return highlightedDescription.get(0);
                }
            }
        }
        return (String) result.getFieldValue(fieldname);
    }
}
