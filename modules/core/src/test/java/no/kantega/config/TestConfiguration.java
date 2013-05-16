package no.kantega.config;

import no.kantega.publishing.api.cache.SiteCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class TestConfiguration {
    @Bean
    public SiteCache getSiteCache(){
        return mock(SiteCache.class);
    }
}
