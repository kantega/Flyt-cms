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
    private Cache aliasCache;

    @Override
    public void contentSaved(ContentEvent event) {
        aliasCache.clear();
        contentUrlCache.clear();
        contentIdentifierCache.clear();
    }


    @Autowired
    public void setCacheManager(CacheManager cacheManager) {
        aliasCache = cacheManager.getCache(CacheManagerFactory.CacheNames.AliasCache.name());
        contentUrlCache = cacheManager.getCache(CacheManagerFactory.CacheNames.ContentUrlCache.name());
        contentIdentifierCache = cacheManager.getCache(CacheManagerFactory.CacheNames.ContentIdentifierCache.name());
    }
}
