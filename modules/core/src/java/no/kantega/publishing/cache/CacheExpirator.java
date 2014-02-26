package no.kantega.publishing.cache;

import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.event.ContentEvent;
import no.kantega.publishing.event.ContentEventListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;

/**
 * ContentEventListener that flushes caches on events.
 */
public class CacheExpirator extends ContentEventListenerAdapter {
    private Cache contentCache;
    private Cache contentListCache;
    private Cache contentUrlCache;
    private Cache contentIdentifierCache;
    private Cache aliasCache;
    private Cache sitemapCache;


    @Override
    public void associationUpdated(ContentEvent event) {
        // Pages could have been copied, safest just to flush everything.
        removeAllContent();
        removeContentListsAndSiteMaps();
    }

    @Override
    public void associationCopied(ContentEvent event) {
        // Pages could have been copied, safest just to flush everything.
        removeAllContent();
        removeContentListsAndSiteMaps();
    }

    @Override
    public void associationAdded(ContentEvent event) {
        // Pages could have been copied, safest just to flush everything.
        removeAllContent();
        removeContentListsAndSiteMaps();
    }

    @Override
    public void setAssociationsPriority(ContentEvent contentEvent) {
        // Pages reordered, all lists and sitemaps should be flushed
        removeContentListsAndSiteMaps();
    }

    @Override
    public void contentStatusChanged(ContentEvent event) {
        removeContentFromCache(event);
    }

    @Override
    public void contentActivated(ContentEvent event) {
        removeContentFromCache(event);
    }

    @Override
    public void contentDeleted(ContentEvent event) {
        removeContentFromCache(event);
    }

    @Override
    public void contentSaved(ContentEvent event) {
        removeContentFromCache(event);
    }

    @Override
    public void contentExpired(ContentEvent event) {
        removeContentFromCache(event);
    }

    @Override
    public void newContentPublished(ContentEvent event) {
        removeContentFromCache(event);
    }

    @Override
    public void associationDeleted(ContentEvent event) {
        Association a = event.getAssociation();
        if (a != null) {
            int id = a.getAssociationId();
            contentCache.evict(id);
            contentUrlCache.evict(id);
        }
    }

    private void removeContentFromCache(ContentEvent event) {
        aliasCache.clear();
        contentIdentifierCache.clear();
        contentListCache.clear();
        sitemapCache.clear();
    }

    private void removeContentListsAndSiteMaps() {
        contentListCache.clear();
        sitemapCache.clear();
    }

    private void removeAllContent() {
        aliasCache.clear();
        contentCache.clear();
        contentIdentifierCache.clear();
        contentListCache.clear();
        contentUrlCache.clear();
        sitemapCache.clear();
    }

    @Autowired
    public void setCacheManager(org.springframework.cache.CacheManager cacheManager) {
        aliasCache = cacheManager.getCache(CacheManagerFactory.CacheNames.AliasCache.name());
        contentCache = cacheManager.getCache(CacheManagerFactory.CacheNames.ContentCache.name());
        contentIdentifierCache = cacheManager.getCache(CacheManagerFactory.CacheNames.ContentIdentifierCache.name());
        contentListCache = cacheManager.getCache(CacheManagerFactory.CacheNames.ContentListCache.name());
        contentUrlCache = cacheManager.getCache(CacheManagerFactory.CacheNames.ContentUrlCache.name());
        sitemapCache =  cacheManager.getCache(CacheManagerFactory.CacheNames.SiteMapCache.name());
    }
}
