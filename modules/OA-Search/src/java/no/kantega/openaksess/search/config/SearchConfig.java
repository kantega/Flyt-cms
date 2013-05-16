package no.kantega.openaksess.search.config;

import no.kantega.openaksess.search.index.update.IndexUpdater;
import no.kantega.openaksess.search.provider.transformer.AttachmentTransformer;
import no.kantega.openaksess.search.provider.transformer.ContentTransformer;
import no.kantega.publishing.common.data.enums.ObjectType;
import no.kantega.publishing.event.ContentEventListener;
import no.kantega.publishing.event.ContentListenerList;
import no.kantega.search.api.retrieve.IndexableContentTypeToObjectTypeMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SearchConfig {

    @Autowired
    private IndexUpdater indexUpdater;

    @Bean
    public ContentListenerList getSearchContentListenerList(){
        ContentListenerList contentListenerList = new ContentListenerList();
        contentListenerList.setListeners(Arrays.asList( (ContentEventListener) indexUpdater));
        return contentListenerList;
    }

    @Bean
    public IndexableContentTypeToObjectTypeMapping getContentIndexableContentTypeToObjectTypeMapping(){
        return new IndexableContentTypeToObjectTypeMapping(AttachmentTransformer.HANDLED_DOCUMENT_TYPE, ObjectType.ASSOCIATION);
    }

    @Bean
    public IndexableContentTypeToObjectTypeMapping getAttachmentIndexableContentTypeToObjectTypeMapping(){
        return new IndexableContentTypeToObjectTypeMapping(ContentTransformer.HANDLED_DOCUMENT_TYPE, ObjectType.ASSOCIATION);
    }
}
