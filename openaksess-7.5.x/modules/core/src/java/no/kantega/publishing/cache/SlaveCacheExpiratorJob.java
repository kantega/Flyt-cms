package no.kantega.publishing.cache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.publishing.api.xmlcache.XMLCacheEntry;
import no.kantega.publishing.api.xmlcache.XmlCache;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.AssociationAO;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.enums.ServerType;
import no.kantega.publishing.eventlog.Event;
import no.kantega.publishing.eventlog.EventLog;
import no.kantega.publishing.eventlog.EventLogEntry;
import no.kantega.publishing.eventlog.EventLogQuery;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class SlaveCacheExpiratorJob {


    private Logger log = Logger.getLogger(getClass());

    private long lastRun = System.currentTimeMillis();

    @Autowired
    private EventLog eventLog;

    @Autowired
    private XmlCache xmlCache;

    private CacheManager cacheManager;

    public void execute() {
        if (Aksess.getServerType() != ServerType.SLAVE) {
            log.error("This job should not run on server type " + Aksess.getServerType());
            return;
        }
        try {
            if(!Aksess.getConfiguration().getBoolean("caching.enabled", false)) {
                return;
            }
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }

        final Ehcache contentCache = cacheManager.getEhcache(CacheManagerFactory.CacheNames.ContentCache.name());
        final Ehcache contentListCache = cacheManager.getEhcache(CacheManagerFactory.CacheNames.ContentListCache.name());
        final Ehcache siteMapCache = cacheManager.getEhcache(CacheManagerFactory.CacheNames.SiteMapCache.name());
        // Find content that was changed

        long thisRun = System.currentTimeMillis();
        EventLogQuery eventLogQuery = new EventLogQuery().setFrom(new Date(lastRun));
        final List<EventLogEntry> entiresSinceLast = eventLog.getQueryResult(eventLogQuery);


        // Remove any Content objects (keyed by Association)
        for (EventLogEntry eventLogEntry : entiresSinceLast) {

            // If this was a move, flush everything
            if(Event.MOVE_CONTENT.equals(eventLogEntry.getEventName())) {
                contentCache.removeAll();
                // Flush the content list (search) cache
                contentListCache.removeAll();
                // Flush the site map cache
                siteMapCache.removeAll();
            }

            for(Association a : AssociationAO.getAssociationsByContentId(eventLogEntry.getSubjectId())) {
                final Object key = a.getAssociationId();
                log.debug("Expiring association " + a.getAssociationId());
                contentCache.remove(key);
            }
        }

        // Flush content list and site map if anything changed. Paranoid, but effective.
        if(entiresSinceLast.size() > 0) {
            log.debug("Flushing content list and site map caches");
            // Flush the content list (search) cache
            contentListCache.removeAll();
            // Flush the site map cache
            siteMapCache.removeAll();
        }

        // Flush any XML caches changed since last time
        for(XMLCacheEntry e : xmlCache.getSummary()) {
            if(e.getLastUpdated().getTime() > lastRun) {
                final Object key = e.getId();
                log.debug("Flushing XML cache " + key);
                cacheManager.getEhcache(CacheManagerFactory.CacheNames.XmlCache.name()).remove(key);
            }
        }


        lastRun = thisRun;
        
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
}