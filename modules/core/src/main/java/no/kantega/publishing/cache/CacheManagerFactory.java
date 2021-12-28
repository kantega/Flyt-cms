package no.kantega.publishing.cache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;
import net.sf.ehcache.config.DiskStoreConfiguration;
import net.sf.ehcache.management.ManagementService;
import no.kantega.publishing.wro.xmlmerge.XmlMerger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;

import javax.management.MBeanServer;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.UUID;

/**
 * Based on EhCacheManagerFactoryBean
 * @see org.springframework.cache.ehcache.EhCacheManagerFactoryBean
 */
public class CacheManagerFactory implements ServletContextAware, FactoryBean<CacheManager>, InitializingBean, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(CacheManagerFactory.class);

    private ServletContext servletContext;

    private CacheManager cacheManager;

    public enum CacheNames {AliasCache, ContentCache, ContentIdentifierCache, ContentListCache, ContentUrlCache,
        FacetLabelCache, ImageCache, MultimediaCache, SiteMapCache, UserCache, XmlCache}

    private static final String OA_XML_CONFIG_FILE = "/WEB-INF/ehcache-oa.xml";
    private static final String PROJECT_XML_CONFIG_FILE = "/WEB-INF/ehcache-project.xml";

    @Override
    public void afterPropertiesSet() throws IOException, CacheException {
        InputStream is = XmlMerger.merge(OA_XML_CONFIG_FILE, PROJECT_XML_CONFIG_FILE, servletContext);

        Configuration configuration = ConfigurationFactory.parseConfiguration(is);
        String path = System.getProperty("java.io.tmpdir") + servletContext.getContextPath() + "/ehcache";
        configuration.addDiskStore(new DiskStoreConfiguration().path(path));
        configuration.setName(configuration.getName() + servletContext.getContextPath() + UUID.randomUUID());
        cacheManager = new CacheManager(configuration);

        createMBean();
    }

    private void createMBean() {
        log.info("Registering cachemanager as MBean");
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ManagementService.registerMBeans(getObject(), mBeanServer, false, true, true, true, true);
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public CacheManager getObject() {
        return this.cacheManager;
    }

    public Class<? extends CacheManager> getObjectType() {
        return (this.cacheManager != null ? this.cacheManager.getClass() : CacheManager.class);
    }

    public boolean isSingleton() {
        return true;
    }

    public void destroy() {
        log.info("Shutting down EhCache CacheManager");
        this.cacheManager.shutdown();
    }

}
