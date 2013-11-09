package no.kantega.openaksess.search.solr.config;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.core.CoreContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static no.kantega.openaksess.search.solr.config.SolrConfigInitializer.initSolrConfigIfAbsent;
import static org.apache.commons.lang.StringUtils.isNotBlank;

@Configuration
public class SolrConfiguration {

    @Value("${appDir}/solr")
    private File solrHome;

    @Value("${disableUpdateSolrHome:false}")
    private boolean disableUpdateSolrHome;

    @Value("httpSolrServerUrl:")
    private String httpSolrServerUrl;

    @Value("httpSolrServerUrl:")
    private String cloudSolrServer;

    @Bean(destroyMethod = "shutdown")
    public SolrServer getSolrServer() throws IOException, SAXException, ParserConfigurationException, URISyntaxException {
        if(isNotBlank(cloudSolrServer)){
            return new CloudSolrServer(cloudSolrServer);
        } else if(isNotBlank(httpSolrServerUrl)){
            return new HttpSolrServer(httpSolrServerUrl);
        } else {
            File solrConfigFile = initSolrConfigIfAbsent(solrHome, disableUpdateSolrHome);
            CoreContainer container = CoreContainer.createAndLoad(solrHome.getAbsolutePath(), solrConfigFile);

            return new EmbeddedSolrServer(container, "oacore");
        }
    }
}
