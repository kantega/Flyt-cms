package no.kantega.config;

import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentTemplateAO;
import no.kantega.publishing.common.cache.TemplateConfigurationCache;
import no.kantega.publishing.common.data.ContentTemplate;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.common.util.templates.ContentTemplateReader;
import no.kantega.publishing.common.util.templates.TemplateConfigurationValidator;
import no.kantega.publishing.common.util.templates.XStreamTemplateConfigurationFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;

import javax.sql.DataSource;
import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class TestConfiguration {

    @Bean
    public TemplateConfigurationCache getTemplateConfiguration(){
        XStreamTemplateConfigurationFactory factory = new XStreamTemplateConfigurationFactory();
        factory.setTemplateConfig(new ClassPathResource("test-templateconfig-valid.xml"));

        TemplateConfigurationCache templateConfigurationCache = new TemplateConfigurationCache();
        templateConfigurationCache.setConfigurationFactory(factory);

        ContentTemplateReader contentTemplateReader = new ContentTemplateReader();
        contentTemplateReader.setContentTemplateResourceLoader(new DefaultResourceLoader());
        templateConfigurationCache.setContentTemplateReader(contentTemplateReader);
        templateConfigurationCache.setConfigurationValidator(new TemplateConfigurationValidator());
        return templateConfigurationCache;
    }

    @Bean(name = "aksessSiteCache")
    public SiteCache getSiteCache(){
        return mock(SiteCache.class);
    }

    @Bean
    public Integer configureDbConnectionFactory(DataSource dataSource) throws IllegalAccessException, NoSuchFieldException {
        Field ds = dbConnectionFactory.class.getDeclaredField("ds");
        ds.setAccessible(true);
        ds.set(null, dataSource);
        return 42;
    }

    @Bean
    public ContentTemplateAO contentTemplateAO(){
        ContentTemplateAO mock = mock(ContentTemplateAO.class);
        when(mock.getTemplateById(anyInt())).thenReturn(new ContentTemplate());
        return mock;
    }
}
