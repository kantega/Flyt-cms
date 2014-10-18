package no.kantega.publishing.configuration;

import no.kantega.publishing.api.runtime.ServerType;
import no.kantega.publishing.common.Aksess;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServertypeConfig {

    @Bean
    public ServerType serverType(){
        return Aksess.getServerType();
    }
}
