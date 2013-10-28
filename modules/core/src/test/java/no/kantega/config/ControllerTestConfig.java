package no.kantega.config;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentAliasDao;
import no.kantega.publishing.api.content.ContentIdentifierDao;
import no.kantega.publishing.api.services.security.PermissionAO;
import no.kantega.publishing.api.xmlcache.XmlCache;
import no.kantega.publishing.client.ContentRequestDispatcher;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.cache.TemplateConfigurationCache;
import no.kantega.publishing.common.templates.XMLFileInputStreamSource;
import no.kantega.publishing.common.traffic.TrafficLogger;
import no.kantega.publishing.common.util.templates.ContentTemplateReader;
import no.kantega.publishing.common.util.templates.TemplateConfigurationValidator;
import no.kantega.publishing.common.util.templates.XStreamTemplateConfigurationFactory;
import no.kantega.publishing.content.api.ContentAO;
import no.kantega.publishing.content.api.ContentIdHelper;
import no.kantega.publishing.eventlog.EventLog;
import no.kantega.publishing.security.realm.SecurityRealm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;

import java.util.Properties;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class ControllerTestConfig {

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

    @Bean(name = "aksessSiteCache")
    public SiteCache getSiteCache(){
        return mock(SiteCache.class);
    }

    @Bean
    public ContentRequestDispatcher ContentRequestDispatcher(){
        return mock(ContentRequestDispatcher.class);
    }

    @Bean
    public SecurityRealm securityRealm(){
        return mock(SecurityRealm.class);
    }

    @Bean
    public ContentIdentifierDao contentIdentifierDao(){
        return mock(ContentIdentifierDao.class);
    }

    @Bean
    public ContentAliasDao contentAliasDao(){
        ContentAliasDao contentAliasDao = mock(ContentAliasDao.class);
        when(contentAliasDao.getAllAliases()).thenReturn(asList("/alias/", "/alias/alias/"));
        return contentAliasDao;
    }

    @Bean
    public CacheManager cacheManager(){
        CacheManager cacheManager = mock(CacheManager.class);
        Cache cache = mock(Cache.class);
        when(cacheManager.getCache(isA(String.class))).thenReturn(cache);
        return cacheManager;
    }

    @Bean
    public EventLog eventLog(){
        return mock(EventLog.class);
    }

    @Bean
    public TrafficLogger trafficLogger(){
        return mock(TrafficLogger.class);
    }

    @Bean
    public XmlCache xmlCache(){
        return mock(XmlCache.class);
    }

    @Bean
    public ContentAO contentAO(){
        return mock(ContentAO.class);
    }

    @Bean
    public ContentIdHelper contentIdHelper(){
        return mock(ContentIdHelper.class);
    }

    @Bean
    public String setAksess(){
        Aksess.setConfiguration(new no.kantega.commons.configuration.Configuration(new Properties()));
        return "Aksess.java";
    }

    @Bean
    public PermissionAO permissionAO(){
        return mock(PermissionAO.class);
    }
}
