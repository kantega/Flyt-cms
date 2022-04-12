package no.kantega.openaksess.search.solr.config;

import no.kantega.search.api.IndexableDocumentCustomizer;
import no.kantega.search.api.provider.DocumentTransformer;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Configuration
public class SolrConfiguration {

    private final Logger log = LoggerFactory.getLogger(SolrConfiguration.class);

    @Value("${appDir}/solr")
    private File solrHome;

    @Value("${disableUpdateSolrHome:false}")
    private boolean disableUpdateSolrHome;

    @Value("${httpSolrServerUrl:}")
    private String httpSolrServerUrl;

    @Value("${cloudSolrServer:}")
    private String cloudSolrServer;

    private final List<DocumentTransformer<?>> transformers;

    private final List<IndexableDocumentCustomizer<?>> customizers;

    public SolrConfiguration(List<DocumentTransformer<?>> transformers, List<IndexableDocumentCustomizer<?>> customizers) {
        this.transformers = transformers;
        this.customizers = customizers;
    }

    @Bean(destroyMethod = "close")
    public SolrClient getSolrServer() throws IOException, SAXException, ParserConfigurationException, URISyntaxException {
        if(isNotBlank(cloudSolrServer)){
            log.info("Using CloudSolrServer " + cloudSolrServer);
            return new CloudSolrClient.Builder(List.of(cloudSolrServer.split(","))).build();
        } else if(isNotBlank(httpSolrServerUrl)){
            log.info("Using HttpSolrServer " + httpSolrServerUrl);
            return new Http2SolrClient.Builder(httpSolrServerUrl).build();
        } else {
            log.info("Using EmbeddedSolrServer");
            return null;
        }
    }

    @Bean
    public Map<DocumentTransformer<?>, Collection<IndexableDocumentCustomizer<?>>> mapCustomizersToTransformers(){

        List<DocumentTransformer<?>> documentTransformers = transformers == null ? Collections.<DocumentTransformer<?>>emptyList() : transformers;
        List<IndexableDocumentCustomizer<?>> documentCustomizers = customizers == null ? Collections. <IndexableDocumentCustomizer<?>>emptyList() : customizers;

        return IndexableDocumentCustomizerPostProcessor.mapCustomizersToTransformers(documentTransformers, documentCustomizers);
    }
}
