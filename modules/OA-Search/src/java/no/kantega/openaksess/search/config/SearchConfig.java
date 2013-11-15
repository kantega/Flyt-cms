package no.kantega.openaksess.search.config;

import no.kantega.openaksess.search.provider.transformer.AttachmentTransformer;
import no.kantega.openaksess.search.provider.transformer.ContentTransformer;
import no.kantega.publishing.common.data.enums.ObjectType;
import no.kantega.search.api.retrieve.IndexableContentTypeToObjectTypeMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SearchConfig {
    @Bean
    public IndexableContentTypeToObjectTypeMapping getContentIndexableContentTypeToObjectTypeMapping(){
        return new IndexableContentTypeToObjectTypeMapping(AttachmentTransformer.HANDLED_DOCUMENT_TYPE, ObjectType.ASSOCIATION);
    }

    @Bean
    public IndexableContentTypeToObjectTypeMapping getAttachmentIndexableContentTypeToObjectTypeMapping(){
        return new IndexableContentTypeToObjectTypeMapping(ContentTransformer.HANDLED_DOCUMENT_TYPE, ObjectType.ASSOCIATION);
    }
}
