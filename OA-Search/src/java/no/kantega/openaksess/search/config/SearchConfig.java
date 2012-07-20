package no.kantega.openaksess.search.config;

import no.kantega.openaksess.search.index.IndexUpdater;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.event.ContentEventListener;
import no.kantega.publishing.event.ContentListenerList;
import no.kantega.publishing.security.SecuritySession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SearchConfig {

    @Autowired
    private IndexUpdater indexUpdater;

    @Bean
    public ContentManagementService getContentManagementService(){
        return new ContentManagementService(SecuritySession.createNewAdminInstance());
    }

    @Bean
    public ContentListenerList getSearchContentListenerList(){
        ContentListenerList contentListenerList = new ContentListenerList();
        contentListenerList.setListeners(Arrays.asList( (ContentEventListener) indexUpdater));
        return contentListenerList;
    }
}
