package no.kantega.publishing.cache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.management.ManagementService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.net.URL;

/**
 *
 */
public class CacheManagerFactory extends AbstractFactoryBean {

    public enum CacheNames {ContentCache, ContentListCache, SiteMapCache, XmlCache}
    private Logger log = Logger.getLogger(getClass());

    @Override
    public Class getObjectType() {
        return CacheManager.class;
    }

    @Override
    protected Object createInstance() throws Exception {
        final URL configurationUrl = getClass().getResource("ehcache.xml");
        final CacheManager cacheManager = new CacheManager(configurationUrl);
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ManagementService.registerMBeans(cacheManager, mBeanServer, false, false, false, true, true);
        return cacheManager;
    }
}
