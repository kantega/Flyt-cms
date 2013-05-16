package no.kantega.publishing.jobs.cache;

import no.kantega.publishing.cache.CacheManagerFactory;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.event.ContentEvent;
import no.kantega.publishing.event.ContentEventListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

public class UpdateContentCachesListener extends ContentEventListenerAdapter {
    private Cache contentUrlCache;
    private Cache contentIdentifierCache;
    private int id;

    @Override
    public void contentSaved(ContentEvent event) {
        Association a = event.getContent().getAssociation();
        if (a != null) {
            id = a.getId();
            contentUrlCache.evict(id);
            contentIdentifierCache.evict(id);
        }
    }

    @Autowired
    public void setCacheManager(CacheManager cacheManager) {
        contentUrlCache = cacheManager.getCache(CacheManagerFactory.CacheNames.ContentUrlCache.name());
        contentIdentifierCache = cacheManager.getCache(CacheManagerFactory.CacheNames.ContentIdentifierCache.name());
    }
}
