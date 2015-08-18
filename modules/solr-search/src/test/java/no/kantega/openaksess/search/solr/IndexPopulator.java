package no.kantega.openaksess.search.solr;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class IndexPopulator {

    @Autowired
    private SolrClient solrServer;

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void populateIndex() throws IOException, SolrServerException {
        try(InputStream inputStream = getClass().getResourceAsStream("/content.json")) {
            List<Map<String, String>> result =
                    new ObjectMapper().readValue(inputStream, ArrayList.class);
            solrServer.add(createInputDocuments(result));
            solrServer.commit();
        }
    }

    private Collection<SolrInputDocument> createInputDocuments(List<Map<String, String>> result) {
        return result.stream()
                .map(resultObject -> {
                    Map<String, SolrInputField> transformedMap = new HashMap<>();
                    for (Map.Entry<String, String> stringStringEntry : resultObject.entrySet()) {
                        String key = stringStringEntry.getKey();
                        SolrInputField value = new SolrInputField(key);
                        value.setValue(stringStringEntry.getValue(), 1);
                        transformedMap.put(key, value);
                    }
                    return new SolrInputDocument(transformedMap);
                }).collect(Collectors.toList());
    }
}
