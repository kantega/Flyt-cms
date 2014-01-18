package no.kantega.openaksess.search.solr;

import no.kantega.openaksess.search.provider.transformer.AttachmentTransformer;
import no.kantega.openaksess.search.provider.transformer.ContentTransformer;
import no.kantega.publishing.content.api.ContentAO;
import no.kantega.publishing.topicmaps.ao.TopicDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class TestConfig {

    @Bean
    public ContentTransformer getContentTransformer(){
        return new ContentTransformer();
    }

    @Bean
    public AttachmentTransformer getAttachmentTransformer(){
        return new AttachmentTransformer();
    }

    @Bean
    public ContentAO getContentAO(){
        return mock(ContentAO.class);
    }

    @Bean
    public TopicDao getTopicDao(){
        return mock(TopicDao.class);
    }

}
