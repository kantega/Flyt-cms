package no.kantega.publishing.cache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.management.ManagementService;
import no.kantega.publishing.wro.xmlmerge.XmlMerger;
import org.apache.log4j.Logger;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.context.ServletContextAware;

import javax.management.MBeanServer;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;

public class CacheManagerFactory extends EhCacheManagerFactoryBean implements ServletContextAware {

    private ServletContext servletContext;

    public enum CacheNames {ContentCache, ContentListCache, SiteMapCache, XmlCache, FacetLabelCache, ContentUrlCache, ContentIdentifierCache, ImageCache, UserCache}
    private Logger log = Logger.getLogger(getClass());

    private static final String OA_XML_CONFIG_FILE = "/WEB-INF/ehcache-oa.xml";
    private static final String PROJECT_XML_CONFIG_FILE = "/WEB-INF/ehcache-project.xml";

    @Override
    public void afterPropertiesSet() throws IOException, CacheException {
        InputStream is = XmlMerger.merge(OA_XML_CONFIG_FILE, PROJECT_XML_CONFIG_FILE, servletContext);
        setConfigLocation(new InputStreamResource(is));

        super.afterPropertiesSet();
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
}
