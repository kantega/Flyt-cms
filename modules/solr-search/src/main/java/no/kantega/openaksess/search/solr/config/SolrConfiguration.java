package no.kantega.openaksess.search.solr.config;

import no.kantega.search.api.IndexableDocumentCustomizer;
import no.kantega.search.api.provider.DocumentTransformer;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.core.CoreContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import static no.kantega.openaksess.search.solr.config.SolrConfigInitializer.initSolrConfigIfAbsent;
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

    @Autowired(required = false)
    private List<DocumentTransformer<?>> transformers;

    @Autowired(required = false)
    private List<IndexableDocumentCustomizer<?>> customizers;

    @Bean(destroyMethod = "shutdown")
    public SolrClient getSolrServer() throws IOException, SAXException, ParserConfigurationException, URISyntaxException {
        if(isNotBlank(cloudSolrServer)){
            log.info("Using CloudSolrServer " + cloudSolrServer);
            return new CloudSolrClient(cloudSolrServer);
        } else if(isNotBlank(httpSolrServerUrl)){
            log.info("Using HttpSolrServer " + httpSolrServerUrl);
            return new HttpSolrClient(httpSolrServerUrl);
        } else {
            log.info("Using EmbeddedSolrServer");
            File solrConfigFile = initSolrConfigIfAbsent(solrHome, disableUpdateSolrHome);
            CoreContainer container = CoreContainer.createAndLoad(solrHome.toPath(), solrConfigFile.toPath());

            return new EmbeddedSolrServer(container, "oacore");
        }
    }

    @Bean
    public Map<DocumentTransformer<?>, Collection<IndexableDocumentCustomizer<?>>> mapCustomizersToTransformers(){

        List<DocumentTransformer<?>> documentTransformers = transformers == null ? Collections.<DocumentTransformer<?>>emptyList() : transformers;
        List<IndexableDocumentCustomizer<?>> documentCustomizers = customizers == null ? Collections. <IndexableDocumentCustomizer<?>>emptyList() : customizers;

        return IndexableDocumentCustomizerPostProcessor.mapCustomizersToTransformers(documentTransformers, documentCustomizers);
    }
}
