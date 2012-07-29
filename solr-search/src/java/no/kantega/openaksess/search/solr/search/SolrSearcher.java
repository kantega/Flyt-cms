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
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SolrSearcher implements Searcher {

    @Autowired
    private SolrServer solrServer;

    private Map<String, DocumentRetriever> documentRetrievers;

    public SearchResponse search(SearchQuery query) {
        SearchResponse searchResponse = new SearchResponse();
        try {
            QueryResponse queryResponse = solrServer.query(new SolrQuery(query.getFullQuery()));
            SolrDocumentList results = queryResponse.getResults();
            searchResponse.setQueryTime(queryResponse.getQTime());
            searchResponse.setQuery(query);

            List<SearchResult> searchResults = new ArrayList<SearchResult>();
            for (SolrDocument result : results) {
                searchResults.add(createSearchResult(result));
            }
            searchResponse.setDocumentHits(searchResults);
        } catch (SolrServerException e) {
            e.printStackTrace();
        }

        return searchResponse;

    }

    private SearchResult createSearchResult(SolrDocument result) {

        return new LazyObjectLoadingSearchResult((Integer) result.getFieldValue("id"),
                (Integer) result.getFieldValue("securityId"),
                (String) result.getFieldValue("indexedContentType"),
                (String) result.getFieldValue("title_no"),
                (String) result.getFieldValue("description_no"),
                (String) result.getFieldValue("author"),
                (String) result.getFieldValue("url"));
    }

    public List<String> suggest(SearchQuery query) {
        return null;
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
