package no.kantega.config;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentAO;
import no.kantega.publishing.api.content.ContentAliasDao;
import no.kantega.publishing.api.content.ContentIdHelper;
import no.kantega.publishing.api.content.ContentIdentifierDao;
import no.kantega.publishing.api.security.RememberMeHandler;
import no.kantega.publishing.api.services.security.PermissionAO;
import no.kantega.publishing.api.xmlcache.XmlCache;
import no.kantega.publishing.client.ContentRequestDispatcher;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.cache.TemplateConfigurationCache;
import no.kantega.publishing.common.traffic.TrafficLogger;
import no.kantega.publishing.common.util.templates.ContentTemplateReader;
import no.kantega.publishing.common.util.templates.TemplateConfigurationValidator;
import no.kantega.publishing.common.util.templates.XStreamTemplateConfigurationFactory;
import no.kantega.publishing.eventlog.EventLog;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.security.login.PostLoginHandler;
import no.kantega.publishing.security.login.PostLoginHandlerFactory;
import no.kantega.publishing.security.realm.SecurityRealm;
import no.kantega.security.api.common.SystemException;
import no.kantega.security.api.identity.DefaultAuthenticatedIdentity;
import no.kantega.security.api.identity.IdentificationFailedException;
import no.kantega.security.api.identity.Identity;
import no.kantega.security.api.identity.IdentityResolver;
import no.kantega.security.api.profile.DefaultProfile;
import no.kantega.security.api.profile.ProfileManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class ControllerTestConfig {

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
    public ContentRequestDispatcher ContentRequestDispatcher(){
        return mock(ContentRequestDispatcher.class);
    }

    @Bean(name = "securityRealm")
    public SecurityRealm securityRealm() throws IdentificationFailedException, SystemException {
        SecurityRealm mock = mock(SecurityRealm.class);
        IdentityResolver identityResolver = mock(IdentityResolver.class);
        DefaultAuthenticatedIdentity identity = new DefaultAuthenticatedIdentity(identityResolver);
        when(identityResolver.getIdentity(any(HttpServletRequest.class))).thenReturn(identity);

        ProfileManager profileManager = mock(ProfileManager.class);
        DefaultProfile defaultProfile = new DefaultProfile();
        defaultProfile.setIdentity(identity);
        when(profileManager.getProfileForUser(any(Identity.class))).thenReturn(defaultProfile);
        when(mock.getProfileManager()).thenReturn(profileManager);

        when(mock.getIdentityResolver()).thenReturn(identityResolver);
        return mock;
    }

    @Bean
    public ContentIdentifierDao contentIdentifierDao(){
        return mock(ContentIdentifierDao.class);
    }

    @Bean
    public ContentAliasDao contentAliasDao(){
        ContentAliasDao contentAliasDao = mock(ContentAliasDao.class);
        when(contentAliasDao.getAllAliases()).thenReturn(new HashSet<>(asList("/alias/", "/alias/alias/")));
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
        Properties properties = new Properties();
        properties.put("security.realm", "securityRealm");
        Aksess.setConfiguration(new no.kantega.commons.configuration.Configuration(properties));
        Aksess.loadConfiguration();
        return "Aksess.java";
    }

    @Bean
    public PermissionAO permissionAO(){
        return mock(PermissionAO.class);
    }

    @Bean
    public RememberMeHandler rememberMeHandler(){
        return new RememberMeHandler() {
            @Override
            public void rememberUser(HttpServletResponse response, String username, String domain) {

            }

            @Override
            public Identity getRememberedIdentity(HttpServletRequest request) {
                return null;
            }

            @Override
            public void forgetUser(HttpServletRequest request, HttpServletResponse response) {

            }
        };
    }

    @Bean
    public PostLoginHandlerFactory postLoginHandlerFactory() throws ConfigurationException {
        PostLoginHandlerFactory postLoginHandlerFactory = mock(PostLoginHandlerFactory.class);
        when(postLoginHandlerFactory.newInstance()).thenReturn(new PostLoginHandler() {
            @Override
            public void handlePostLogin(User user, HttpServletRequest request) throws no.kantega.commons.exception.SystemException {

            }
        });
        return postLoginHandlerFactory;
    }
}
