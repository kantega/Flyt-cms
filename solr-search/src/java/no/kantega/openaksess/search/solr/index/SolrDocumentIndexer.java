package no.kantega.openaksess.search.solr.index;

import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.index.DocumentIndexer;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class SolrDocumentIndexer implements DocumentIndexer {
    @Autowired
    private SolrServer solrServer;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public void indexDocument(IndexableDocument document) {
        try {
            File fileContent = document.getFileContent();
            SolrInputDocument solrParams = getSolrParams(document);
            if (fileContent == null) {
                UpdateResponse add = solrServer.add(solrParams);
            } else {
                ContentStreamUpdateRequest contentStreamUpdateRequest = new ContentStreamUpdateRequest("/update/extract");
                contentStreamUpdateRequest.setParams(getStreamParams(document));
                contentStreamUpdateRequest.addFile(fileContent, StringUtils.substringAfterLast(fileContent.getName(), "."));
                contentStreamUpdateRequest.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);
                NamedList<Object> request = solrServer.request(contentStreamUpdateRequest);
            }
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void indexDocumentAndCommit(IndexableDocument document) {
        indexDocument(document);
        commit();
    }

    public void commit() {
        try {
            UpdateResponse commit = solrServer.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteById(List<String> uids) {
        try {
            UpdateResponse updateResponse = solrServer.deleteById(uids);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void optimize() {
        try {
            solrServer.optimize();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ModifiableSolrParams getStreamParams(IndexableDocument document) {
        ModifiableSolrParams streamParams = new ModifiableSolrParams();

        streamParams.add("literal.contentStatus", document.getContentStatus());
        streamParams.add("literal.indexedContentType", document.getContentType());
        streamParams.add("literal.description_no", document.getDescription());
        streamParams.add("literal.id", document.getId());
        streamParams.add("literal.uid", document.getUId());
        streamParams.add("literal.siteId", String.valueOf(document.getSiteId()));
        streamParams.add("literal.title_no", document.getTitle());
        streamParams.add("literal.visibilityStatus", document.getVisibility());

        for(Map.Entry<String, Object> attributeEntry : document.getAttributes().entrySet()){
            streamParams.add("literal." + attributeEntry.getKey(), getStringValue(attributeEntry.getValue()));
        }
        return streamParams;
    }

    private String getStringValue(Object value) {
        if(value instanceof String){
            return (String) value;
        } else if (Date.class.isAssignableFrom(value.getClass())){
            return dateFormat.format(value);
        }
        return "";
    }

    private SolrInputDocument getSolrParams(IndexableDocument document) {
        String language = document.getLanguage();

        SolrInputDocument solrInputDocument = new SolrInputDocument();
        solrInputDocument.addField("contentStatus", document.getContentStatus());
        solrInputDocument.addField("indexedContentType", document.getContentType());
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
