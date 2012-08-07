package no.kantega.openaksess.search.solr.search;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gdata.util.common.base.Pair;
import no.kantega.search.api.retrieve.DocumentRetriever;
import no.kantega.search.api.search.*;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static no.kantega.openaksess.search.solr.index.SolrDocumentIndexer.getLanguageSuffix;

@Component
public class SolrSearcher implements Searcher {

    @Autowired
    private SolrServer solrServer;

    private Map<String, DocumentRetriever> documentRetrievers;
    private final Pattern facetQuerySplitPattern = Pattern.compile("^(\\w):(.*)");

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
        SolrDocumentList results = queryResponse.getResults();
        SearchResponse searchResponse = new SearchResponse(query, results.getNumFound(), queryResponse.getQTime(), addSearchResults(query, queryResponse, results));

        setSpellResponse(searchResponse, queryResponse);

        addFacetResults(searchResponse, queryResponse);
        return searchResponse;
    }

    private SolrQuery createSearchParams(SearchQuery query) {
        SolrQuery params = new SolrQuery(query.getOriginalQuery());
        setFilterQueryIfPresent(query, params);

        Integer resultsPerPage = query.getResultsPerPage();
        params.setRows(resultsPerPage);
        params.setStart(query.getPageNumber() * resultsPerPage);
        params.set("spellcheck", "on");

        setHighlighting(query, params);

        addFacetQueryInformation(query, params);
        return params;
    }

    public List<String> suggest(SearchQuery query) {
        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set("qt", "/suggest");
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
        Multimap<String,Pair<String, Number>> facets = ArrayListMultimap.create();
        List<FacetField> facetFields = queryResponse.getFacetFields();
        if (facetFields != null) {
            for(FacetField facetField : facetFields){
                for(FacetField.Count count : facetField.getValues()){
                    facets.put(facetField.getName(), new Pair<String, Number>(count.getName(), count.getCount()));
                }
            }
        }
        List<RangeFacet> facetRanges = queryResponse.getFacetRanges();
        if(facetRanges != null){
            for(RangeFacet facetRange : facetRanges){
                List<RangeFacet.Count> counts = facetRange.getCounts();
                for (RangeFacet.Count count : counts) {
                    facets.put(facetRange.getName(), new Pair<String, Number>(count.getValue(), count.getCount()));
                }
            }
        }

        Map<String, Integer> facetQuery = queryResponse.getFacetQuery();
        if(facetQuery != null){
            for (Map.Entry<String, Integer> facetQueryEntry : facetQuery.entrySet()) {
                String facetQueryString = facetQueryEntry.getKey();
                String[] facetFieldAndValue = facetQuerySplitPattern.split(facetQueryString);

                throwIfNotLenghtTwo(facetQueryString, facetFieldAndValue);
                facets.put(facetFieldAndValue[0], new Pair<String, Number>(facetFieldAndValue[1], facetQueryEntry.getValue()));
            }
        }
        searchResponse.setFacets(facets.asMap());
    }

    private void throwIfNotLenghtTwo(String facetQueryString, String[] facetFieldAndValue) {
        if(facetFieldAndValue.length != 2){
            throw new IllegalStateException(String.format("Splitting of facet query %s into field and query did not yield expected result." +
                    " Expected values of length 2, got %d: %s", facetQueryString, facetFieldAndValue.length, facetFieldAndValue.toString()));
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
        return new LazyObjectLoadingSearchResult((Integer) result.getFieldValue("id"),
                (Integer) result.getFieldValue("securityId"),
                (String) result.getFieldValue("indexedContentType"),
                (String) result.getFieldValue("title_" + languageSuffix),
                description,
                (String) result.getFieldValue("author"),
                (String) result.getFieldValue("url"));
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

    @Autowired
    public void setDocumentTransformers(List<DocumentRetriever> documentRetrieverList){
        documentRetrievers  = new HashMap<String, DocumentRetriever>();
        for (DocumentRetriever documentTransformer : documentRetrieverList) {
            documentRetrievers.put(documentTransformer.getSupportedContentType(), documentTransformer);
        }
    }

    private class LazyObjectLoadingSearchResult extends SearchResult {
        private final DocumentRetriever documentRetriever;
        public LazyObjectLoadingSearchResult(int id, int securityId, String indexedContentType, String title, String description, String author, String url) {
            super(id, securityId, indexedContentType, title, description, author, url);
            documentRetriever = documentRetrievers.get(indexedContentType);
            if(documentRetriever == null){
                throw new IllegalStateException("Document retriever is null");
            }
        }

        @Override
        public Object getDocument() {
            return documentRetriever.getObjectById(getId());
        }
    }
}
