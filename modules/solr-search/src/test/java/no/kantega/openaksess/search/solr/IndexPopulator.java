package no.kantega.openaksess.search.solr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

@Component
public class IndexPopulator {

    @Autowired
    private SolrServer solrServer;

    @PostConstruct
    public void populateIndex() throws IOException, SolrServerException {
        List<Map<String, String>> result =
                new ObjectMapper().readValue(getClass().getResourceAsStream("/content.json"), ArrayList.class);

        solrServer.add(createInputDocuments(result));
        solrServer.commit();
    }

    private Collection<SolrInputDocument> createInputDocuments(List<Map<String, String>> result) {
        Collection<SolrInputDocument> documents = Collections2.transform(result, new Function<Map<String, String>, SolrInputDocument>() {
            public SolrInputDocument apply(@Nullable Map<String, String> stringStringMap) {
                Map<String, SolrInputField> transformedMap = new HashMap<String, SolrInputField>();
                for (Map.Entry<String, String> stringStringEntry : stringStringMap.entrySet()) {
                    String key = stringStringEntry.getKey();
                    SolrInputField value = new SolrInputField(key);
                    value.setValue(stringStringEntry.getValue(), 1);
                    transformedMap.put(key, value);
                }
                return new SolrInputDocument(transformedMap);
            }
        });


        return documents;
    }
}
