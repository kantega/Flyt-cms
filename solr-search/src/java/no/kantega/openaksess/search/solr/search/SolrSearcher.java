package no.kantega.openaksess.search.solr.search;

import no.kantega.search.api.search.SearchResult;
import no.kantega.search.api.search.Searcher;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SolrSearcher implements Searcher {

    @Autowired
    private SolrServer solrServer;

    public SearchResult search(String query) {
        SearchResult searchResult = new SearchResult();
        try {
            QueryResponse queryResponse = solrServer.query(new SolrQuery(query));
            SolrDocumentList results = queryResponse.getResults();
            searchResult.setNumberOfHits(results.size());
        } catch (SolrServerException e) {
            e.printStackTrace();
        }

        return searchResult;

    }

    public List<String> suggest(String query) {
        return null;
    }
}
