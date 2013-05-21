package no.kantega.config;

import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentAliasDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class TestConfiguration {
    @Bean(name = "aksessSiteCache")
    public SiteCache getSiteCache(){
        return mock(SiteCache.class);
    }

    @Bean
    public ContentAliasDao getContentAliasDao(){
        ContentAliasDao mock = mock(ContentAliasDao.class);
        when(mock.getAllAliases()).thenReturn(asList("/alias/", "/alias2", "/alias/alias/"));
        return mock;
    }
}
