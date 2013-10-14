package no.kantega.config;

import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentAliasDao;
import no.kantega.publishing.api.content.ContentIdentifierDao;
import no.kantega.publishing.common.cache.TemplateConfigurationCache;
import no.kantega.publishing.common.templates.XMLFileInputStreamSource;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.common.util.templates.ContentTemplateReader;
import no.kantega.publishing.common.util.templates.TemplateConfigurationValidator;
import no.kantega.publishing.common.util.templates.XStreamTemplateConfigurationFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;

import javax.sql.DataSource;
import java.lang.reflect.Field;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class TestConfiguration {

    @Bean
    public TemplateConfigurationCache getTemplateConfiguration(){
        XStreamTemplateConfigurationFactory factory = new XStreamTemplateConfigurationFactory();
        factory.setInputStreamSource(new XMLFileInputStreamSource("test-templateconfig-valid.xml"));

        TemplateConfigurationCache templateConfigurationCache = new TemplateConfigurationCache();
        templateConfigurationCache.setConfigurationFactory(factory);

        ContentTemplateReader contentTemplateReader = new ContentTemplateReader();
        contentTemplateReader.setContentTemplateResourceLoader(new DefaultResourceLoader());
        templateConfigurationCache.setContentTemplateReader(contentTemplateReader);
        templateConfigurationCache.setConfigurationValidator(new TemplateConfigurationValidator());
        return templateConfigurationCache;
    }

    @Bean
    public SiteCache getSiteCache(){
        return mock(SiteCache.class);
    }

    @Bean
    public ContentAliasDao getContentAliasDao(){
        ContentAliasDao mock = mock(ContentAliasDao.class);
        when(mock.getAllAliases()).thenReturn(asList("/alias/", "/alias2", "/alias/alias/"));
        return mock;
    }

    @Bean
    public ContentIdentifierDao getContentIdentifierDao(){
        return mock(ContentIdentifierDao.class);
    }

    @Bean
    public Integer configureDbConnectionFactory(DataSource dataSource) throws IllegalAccessException, NoSuchFieldException {
        Field ds = dbConnectionFactory.class.getDeclaredField("ds");
        ds.setAccessible(true);
        ds.set(null, dataSource);
        return 42;
    }
}
