package no.kantega.publishing.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.event.ContentEvent;
import no.kantega.publishing.event.ContentEventListenerAdapter;

/**
 *
 */
public class CacheExpirator extends ContentEventListenerAdapter {

    private CacheManager cacheManager;

    @Override
    public void associationUpdated(ContentEvent event) {
        // Pages could have been copied, safest just to flush everything.
        removeAllContent();
        removeAllContentListsAndSiteMaps();
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

    private Cache getContentCache() {
        return cacheManager.getCache(CacheManagerFactory.CacheNames.ContentCache.name());
    }

    private void removeContentFromCache(ContentEvent event) {
        // Remove the content that was changed
        final Cache contentCache = getContentCache();

        for(Association a : event.getContent().getAssociations())  {
            Object key = a.getAssociationId();
            contentCache.remove(key);
        }
        removeAllContentListsAndSiteMaps();


    }

    private void removeAllContentListsAndSiteMaps() {
        // Flush the content list cache
        cacheManager.getCache(CacheManagerFactory.CacheNames.ContentListCache.name()).removeAll();
        // Flush the site map cache
        cacheManager.getCache(CacheManagerFactory.CacheNames.SiteMapCache.name()).removeAll();
    }

    private void removeAllContent() {
        // Remove the content that was changed
        final Cache contentCache = getContentCache();
        contentCache.removeAll();
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
}
