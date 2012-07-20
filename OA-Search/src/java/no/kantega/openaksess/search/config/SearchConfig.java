package no.kantega.openaksess.search.config;

import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.security.SecuritySession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SearchConfig {

    @Bean
    public ContentManagementService getContentManagementService(){
        return new ContentManagementService(SecuritySession.createNewAdminInstance());
    }
}
