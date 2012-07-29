package no.kantega.openaksess.search.solr.search;

import no.kantega.search.api.retrieve.DocumentRetriever;
import no.kantega.search.api.search.SearchQuery;
import no.kantega.search.api.search.SearchResponse;
import no.kantega.search.api.search.SearchResult;
import no.kantega.search.api.search.Searcher;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
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
        SearchResponse searchResponse = new SearchResponse();
        try {
            SolrQuery params = new SolrQuery(query.getFullQuery());
            params.set("spellcheck", "on");

            params.setHighlight(query.isHighlightSearchResultDescription());
            params.setHighlightSimplePre("<span class=\"highlight\"/>");
            params.setHighlightSimplePost("</span>");
            params.set("hl.fl", "description*");

            QueryResponse queryResponse = solrServer.query(params);
            SolrDocumentList results = queryResponse.getResults();
            searchResponse.setQueryTime(queryResponse.getQTime());
            searchResponse.setQuery(query);

            List<SearchResult> searchResults = new ArrayList<SearchResult>();
            for (SolrDocument result : results) {
                searchResults.add(createSearchResult(result, queryResponse, query));
            }
            searchResponse.setDocumentHits(searchResults);
            setSpellResponse(searchResponse, queryResponse);
            return searchResponse;
        } catch (SolrServerException e) {
            throw new IllegalStateException("Error when searching", e);
        }
    }

    private void setSpellResponse(SearchResponse searchResponse, QueryResponse queryResponse) {
        SpellCheckResponse spellCheckResponse = queryResponse.getSpellCheckResponse();
        if(!spellCheckResponse.isCorrectlySpelled()){
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

    public List<String> suggest(SearchQuery query) {
        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set("qt", "/suggest");
        params.set("q", query.getFullQuery());
        params.set("spellcheck", "on");
        try {
            QueryResponse queryResponse = solrServer.query(params);
            return  getSpellSuggestions(queryResponse.getSpellCheckResponse());
        } catch (SolrServerException e) {
            throw new IllegalStateException("Error when searching", e);
        }
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
