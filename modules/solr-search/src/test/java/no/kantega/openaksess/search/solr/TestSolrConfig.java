package no.kantega.openaksess.search.solr;

import no.kantega.publishing.spring.RuntimeMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestSolrConfig {

    @Bean(name = "runtimeMode")
    public RuntimeMode getRuntimeMode(){
        return RuntimeMode.PRODUCTION;
    }
}
