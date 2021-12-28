package no.kantega.openaksess.search.solr.index;

import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.index.DocumentIndexer;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.cos.COSName;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static no.kantega.search.api.util.FieldUtils.getLanguageSuffix;
import static org.apache.commons.lang3.StringUtils.defaultString;

@Component
public class SolrDocumentIndexer implements DocumentIndexer {
    private final Logger log  = LoggerFactory.getLogger(getClass());

    @Autowired
    private SolrClient solrServer;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Async
    public void indexDocument(IndexableDocument document) {
        try {
            File fileContent = document.getFileContent();
            SolrInputDocument solrParams = getSolrParams(document);
            solrParams.addField("expandMacros", "false");
            if (fileContent == null) {
                UpdateResponse add = solrServer.add(solrParams);
                log.debug("Response from Solr: {}", add);
            } else {
                ContentStreamUpdateRequest contentStreamUpdateRequest = new ContentStreamUpdateRequest("/update/extract");
                contentStreamUpdateRequest.setParams(getStreamParams(document));
                contentStreamUpdateRequest.addFile(fileContent, StringUtils.substringAfterLast(fileContent.getName(), "."));

                try {
                    NamedList<Object> request = solrServer.request(contentStreamUpdateRequest);
                    log.debug("Response from Solr: {}", request);
                } catch (Exception e) {
                    log.error("Error when submitting index query", e);
                } finally {
                    boolean deletedFileContent = fileContent.delete();
                    if(!deletedFileContent){
                        log.error("Could not delete file {}", fileContent.getAbsolutePath());
                    }
                    // Remove when memory leak is fixed http://www.searchworkings.org/forum/-/message_boards/view_message/510823
                    COSName.clearResources();
                }

            }
        } catch (Exception e) {
            log.error("Error when submitting index query", e);
            throw new IllegalStateException(e);
        }

    }

    public void indexDocumentAndCommit(IndexableDocument document) {
        indexDocument(document);
        commit();
    }

    public void commit() {
        try {
            UpdateResponse commit = solrServer.commit();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void deleteByUid(List<String> uids) {
        log.info("Deleting by uid:" + uids);
        try {
            for (String uid : uids) {
                UpdateResponse updateResponse = solrServer.deleteByQuery("uid:" + uid, 0);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void deleteAllDocuments() {
        log.info("Deleting all documents");
        try {
            solrServer.deleteByQuery("*:*");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void deleteByDocType(String docType) {
        log.info("Deleting by indexedContentType:" + docType);
        try {
            solrServer.deleteByQuery("indexedContentType:" + docType);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void optimize() {
        try {
            solrServer.optimize();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private ModifiableSolrParams getStreamParams(IndexableDocument document) {
        ModifiableSolrParams streamParams = new ModifiableSolrParams();
        String languageSuffix = getLanguageSuffix(document.getLanguage());
        streamParams.add("fmap.content", "text_" + languageSuffix);
        // map stream_name such that it is ignored, otherwise names like .pdf6291048212804771660indexer ends up in index.
        streamParams.add("stream_name", "stream_name_ignored");

        streamParams.add("literal.contentStatus", defaultString(document.getContentStatus()));
        streamParams.add("literal.indexedContentType", document.getContentType());
        streamParams.add("literal.language", document.getLanguage());
        streamParams.add("literal.description_" + languageSuffix, defaultString(document.getDescription()));
        streamParams.add("literal.id", document.getId());
        streamParams.add("literal.parentId", String.valueOf(document.getParentId()));
        streamParams.add("literal.securityId", String.valueOf(document.getSecurityId()));
        streamParams.add("literal.uid", document.getUId());
        streamParams.add("literal.siteId", String.valueOf(document.getSiteId()));
        streamParams.add("literal.title_" + languageSuffix, document.getTitle());
        streamParams.add("literal.visibilityStatus", defaultString(document.getVisibility()));

        for(Map.Entry<String, Object> attributeEntry : document.getAttributes().entrySet()){
            streamParams.add(new ModifiableSolrParams());
            Object value = attributeEntry.getValue();
            if (value != null) {
                if (value instanceof Collection) {
                    for(Object o: (Collection)value){
                        streamParams.add("literal." + attributeEntry.getKey(), getStringValue(o));
                    }
                } else {
                    streamParams.add("literal." + attributeEntry.getKey(), getStringValue(value));
                }
            } else {
                log.warn("uid: " + document.getUId() + "." + attributeEntry.getKey() + " had value null!");
            }
        }
        return streamParams;
    }

    private String getStringValue(Object value) {
        if(value instanceof String){
            return (String) value;
        } else if (Date.class.isAssignableFrom(value.getClass())){
            LocalDateTime dateTime = LocalDateTime.ofInstant(((Date)value).toInstant(), ZoneId.systemDefault());
            return dateTime.format(dateTimeFormatter);
        } else {
            return String.valueOf(value);
        }
    }

    private SolrInputDocument getSolrParams(IndexableDocument document) {
        String language = document.getLanguage();
        String languageSuffix = getLanguageSuffix(language);

        SolrInputDocument solrInputDocument = new SolrInputDocument();
        solrInputDocument.addField("contentStatus", document.getContentStatus());
        solrInputDocument.addField("indexedContentType", document.getContentType());
        solrInputDocument.addField("description_" + languageSuffix, document.getDescription());
        solrInputDocument.addField("id", document.getId());
        solrInputDocument.addField("parentId", document.getParentId());
        solrInputDocument.addField("securityId", String.valueOf(document.getSecurityId()));
        solrInputDocument.addField("uid", document.getUId());
        solrInputDocument.addField("language", language);
        solrInputDocument.addField("siteId", document.getSiteId());
        solrInputDocument.addField("title_" + languageSuffix, document.getTitle());
        solrInputDocument.addField("visibilityStatus", document.getVisibility());

        for(Map.Entry<String, Object> attributeEntry : document.getAttributes().entrySet()){
            solrInputDocument.addField(attributeEntry.getKey(), attributeEntry.getValue());
        }
        return solrInputDocument;
    }
}
