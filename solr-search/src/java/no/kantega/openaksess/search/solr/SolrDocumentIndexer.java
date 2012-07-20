package no.kantega.openaksess.search.solr;

import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.index.DocumentIndexer;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class SolrDocumentIndexer implements DocumentIndexer {
    @Autowired
    private SolrServer solrServer;

    public void indexDocument(IndexableDocument document) {
        try {
            solrServer.add(getSolrParams(document));
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private SolrInputDocument getSolrParams(IndexableDocument document) {
        String language = document.getLanguage();

        SolrInputDocument solrInputDocument = new SolrInputDocument();
        solrInputDocument.addField("contentStatus", document.getContentStatus());
        solrInputDocument.addField("contentType", document.getContentType());
        solrInputDocument.addField("description_no", document.getDescription());
        solrInputDocument.addField("id", document.getId());
        solrInputDocument.addField("uid", document.getUId());
        solrInputDocument.addField("language", language);
        solrInputDocument.addField("siteId", document.getSiteId());
        solrInputDocument.addField("title_no", document.getTitle());
        solrInputDocument.addField("visibilityStatus", document.getVisibility());

        for(Map.Entry<String, Object> attributeEntry : document.getAttributes().entrySet()){
            solrInputDocument.addField(attributeEntry.getKey(), attributeEntry.getValue());
        }
        return solrInputDocument;
    }
}
