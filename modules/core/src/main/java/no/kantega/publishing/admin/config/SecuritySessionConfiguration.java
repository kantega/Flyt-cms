package no.kantega.publishing.admin.config;

import no.kantega.publishing.security.SecuritySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class SecuritySessionConfiguration {
    private final Logger log = LoggerFactory.getLogger(SecuritySessionConfiguration.class);

    @Bean
    @Scope("request")
    public SecuritySession securitySession(HttpServletRequest request){
        log.debug("Getting SecuritySession");
        return SecuritySession.getInstance(request);
    }
}
