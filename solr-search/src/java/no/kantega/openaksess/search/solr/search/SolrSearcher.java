package no.kantega.openaksess.search.solr.search;

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
import java.util.List;

@Component
public class SolrSearcher implements Searcher {

    @Autowired
    private SolrServer solrServer;

    //private Map<String, DocumentTransformer> documentTransformers;

    public SearchResponse search(String query) {
        SearchResponse searchResponse = new SearchResponse();
        try {
            QueryResponse queryResponse = solrServer.query(new SolrQuery(query));
            SolrDocumentList results = queryResponse.getResults();
            searchResponse.setNumberOfHits(results.size());
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

        return new SearchResult((Integer) result.getFieldValue("id"),
                (String) result.getFieldValue("indexedContentType"),
                (String) result.getFieldValue("title_no"),
                (String) result.getFieldValue("description_no"),
                (String) result.getFieldValue("author"),
                (String) result.getFieldValue("url"));
    }

    public List<String> suggest(String query) {
        return null;
    }

  /*  @Autowired
    public void setDocumentTransformers(List<DocumentTransformer> transformerList){
        documentTransformers  = new HashMap<String, DocumentTransformer>();
        for (DocumentTransformer documentTransformer : transformerList) {
            documentTransformers.put(documentTransformer.getSupportedContentType(), documentTransformer);
        }
    }*/
}
