package no.kantega.config;

import no.kantega.publishing.client.ContentRequestDispatcher;
import no.kantega.publishing.security.realm.SecurityRealm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class ControllerTestConfig {

    @Bean
    public ContentRequestDispatcher ContentRequestDispatcher(){
        return mock(ContentRequestDispatcher.class);
    }

    @Bean
    public SecurityRealm securityRealm(){
        return mock(SecurityRealm.class);
    }


}
