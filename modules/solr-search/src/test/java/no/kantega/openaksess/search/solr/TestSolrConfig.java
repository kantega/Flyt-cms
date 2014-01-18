package no.kantega.openaksess.search.solr;

import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.spring.RuntimeMode;
import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.IndexableDocumentCustomizer;
import no.kantega.search.api.IndexableDocumentCustomizerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestSolrConfig {

    @Bean(name = "runtimeMode")
    public RuntimeMode getRuntimeMode(){
        return RuntimeMode.PRODUCTION;
    }

    @Bean
    public IndexableDocumentCustomizer<Content> getContentCustomizer(){
        return new IndexableDocumentCustomizerAdapter<Content>(Content.class) {
            @Override
            public IndexableDocument customizeIndexableDocument(Content originalObject, IndexableDocument indexableDocument) {
                indexableDocument.addAttribute("ContentAttribute", "Thar be cuztomize!");
                return indexableDocument;
            }
        };
    }

    @Bean
    public IndexableDocumentCustomizer<Attachment> getAttachmentCustomizer(){
        return new IndexableDocumentCustomizerAdapter<Attachment>(Attachment.class) {
            @Override
            public IndexableDocument customizeIndexableDocument(Attachment originalObject, IndexableDocument indexableDocument) {
                indexableDocument.addAttribute("AttachmentAttribute", "wow. such customize. much attachment.");
                return indexableDocument;
            }
        };
    }
}
