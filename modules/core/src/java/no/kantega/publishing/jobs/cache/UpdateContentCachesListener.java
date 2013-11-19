package no.kantega.publishing.jobs.cache;

import no.kantega.publishing.cache.CacheManagerFactory;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.event.ContentEvent;
import no.kantega.publishing.event.ContentEventListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

public class UpdateContentCachesListener extends ContentEventListenerAdapter {
    private Cache contentCache;
    private Cache contentListCache;
    private Cache contentUrlCache;
    private Cache contentIdentifierCache;
    private Cache aliasCache;

    @Override
    public void contentSaved(ContentEvent event) {
        aliasCache.clear();
        contentCache.clear();
        contentIdentifierCache.clear();
        contentListCache.clear();
        contentUrlCache.clear();
    }

    @Autowired
    public void setCacheManager(CacheManager cacheManager) {
        aliasCache = cacheManager.getCache(CacheManagerFactory.CacheNames.AliasCache.name());
        contentCache = cacheManager.getCache(CacheManagerFactory.CacheNames.ContentCache.name());
        contentIdentifierCache = cacheManager.getCache(CacheManagerFactory.CacheNames.ContentIdentifierCache.name());
        contentListCache = cacheManager.getCache(CacheManagerFactory.CacheNames.ContentListCache.name());
        contentUrlCache = cacheManager.getCache(CacheManagerFactory.CacheNames.ContentUrlCache.name());
    }
}
