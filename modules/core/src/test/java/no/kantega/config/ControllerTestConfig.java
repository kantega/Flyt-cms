package no.kantega.config;

import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentAliasDao;
import no.kantega.publishing.api.content.ContentIdentifierDao;
import no.kantega.publishing.client.ContentRequestDispatcher;
import no.kantega.publishing.security.realm.SecurityRealm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class ControllerTestConfig {

    @Bean(name = "aksessSiteCache")
    public SiteCache getSiteCache(){
        return mock(SiteCache.class);
    }

    @Bean
    public ContentRequestDispatcher ContentRequestDispatcher(){
        return mock(ContentRequestDispatcher.class);
    }

    @Bean
    public SecurityRealm securityRealm(){
        return mock(SecurityRealm.class);
    }

    @Bean
    public ContentIdentifierDao contentIdentifierDao(){
        return mock(ContentIdentifierDao.class);
    }

    @Bean
    public ContentAliasDao contentAliasDao(){
        return mock(ContentAliasDao.class);
    }

}
