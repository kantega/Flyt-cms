package no.kantega.publishing.cache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.management.ManagementService;
import org.apache.log4j.Logger;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;

import javax.management.MBeanServer;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public class CacheManagerFactory extends EhCacheManagerFactoryBean {

    public enum CacheNames {ContentCache, ContentListCache, SiteMapCache, XmlCache, FacetLabelCache, ContentUrlCache, ContentIdentifierCache, ImageCache, UserCache}
    private Logger log = Logger.getLogger(getClass());

    @Override
    public void afterPropertiesSet() throws IOException, CacheException {
        super.afterPropertiesSet();
        log.info("Registering cachemanager as MBean");
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ManagementService.registerMBeans(getObject(), mBeanServer, false, false, false, true, true);
    }
}
