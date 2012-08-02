package no.kantega.openaksess.search.solr.search;

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

import static no.kantega.openaksess.search.solr.index.SolrDocumentIndexer.getLanguageSuffix;

@Component
public class SolrSearcher implements Searcher {

    @Autowired
    private SolrServer solrServer;

    private Map<String, DocumentRetriever> documentRetrievers;

    public SearchResponse search(SearchQuery query) {
        try {
            SolrQuery params = new SolrQuery(query.getFullQuery());
            Integer resultsPerPage = query.getResultsPerPage();
            params.setRows(resultsPerPage);
            params.setStart(query.getPageNumber() * resultsPerPage);
            params.set("spellcheck", "on");

            setHighlighting(query, params);

            addFacetQueryInformation(query, params);

            QueryResponse queryResponse = solrServer.query(params);

            SolrDocumentList results = queryResponse.getResults();
            SearchResponse searchResponse = new SearchResponse(query, queryResponse.getQTime(), addSearchResults(query, queryResponse, results));

            setSpellResponse(searchResponse, queryResponse);

            addFacetResults(searchResponse, queryResponse);

            return searchResponse;
        } catch (SolrServerException e) {
            throw new IllegalStateException("Error when searching", e);
        }
    }

    public List<String> suggest(SearchQuery query) {
        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set("qt", "/suggest");
        params.set("q", query.getFullQuery());
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
        List<FacetField> facetFields = queryResponse.getFacetFields();
        if (facetFields != null) {
            Map<String, List<Pair<String, Long>>> facetFieldsResult = new HashMap<String, List<Pair<String, Long>>>();
            for(FacetField facetField : facetFields){
                List<Pair<String, Long>> valuesAndCount = new ArrayList<Pair<String, Long>>();
                for(FacetField.Count count : facetField.getValues()){
                    valuesAndCount.add(new Pair<String, Long>(count.getName(), count.getCount()));
                }
                facetFieldsResult.put(facetField.getName(), valuesAndCount);
            }
            searchResponse.setFacetFields(facetFieldsResult);
        }
        List<RangeFacet> facetRanges = queryResponse.getFacetRanges();
        if(facetRanges != null){
            Map<String, List<Pair<String, Integer>>> rangeFacetResult = new HashMap<String, List<Pair<String, Integer>>>();
            for(RangeFacet facetRange : facetRanges){
                List<RangeFacet.Count> counts = facetRange.getCounts();
                List<Pair<String, Integer>> resultCounts = new ArrayList<Pair<String, Integer>>();
                for (RangeFacet.Count count : counts) {
                    resultCounts.add(new Pair<String, Integer>(count.getValue(), count.getCount()));
                }
                rangeFacetResult.put(facetRange.getName(), resultCounts);
            }
            searchResponse.setRangeFacet(rangeFacetResult);
        }

        Map<String, Integer> facetQuery = queryResponse.getFacetQuery();
        if(facetQuery != null){
            List<Pair<String, Integer>> facetQueryResults = new ArrayList<Pair<String, Integer>>();
            for (Map.Entry<String, Integer> facetQueryEntry : facetQuery.entrySet()) {
                facetQueryResults.add(new Pair<String, Integer>(facetQueryEntry.getKey(), facetQueryEntry.getValue()));
            }
            searchResponse.setFacetQuery(facetQueryResults);
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
            Map<String, List<String>> uid = queryResponse.getHighlighting().get((String)result.getFieldValue("uid"));
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
