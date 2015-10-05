package no.kantega.publishing.cache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import no.kantega.publishing.api.multimedia.event.MultimediaEvent;
import no.kantega.publishing.api.multimedia.event.MultimediaEventListenerAdapter;
import no.kantega.publishing.common.data.Multimedia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Remove from ImageCache where key start with multimediaId.
 * Remove MultimediaCache where key is multimediaId.
 */
public class MultimediaCacheExpirator extends MultimediaEventListenerAdapter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private CacheManager cacheManager;

    @Override
    public void afterSetMultimedia(MultimediaEvent event) {
        evictMultimedia(event.getMultimedia());
    }

    @Override
    public void afterMoveMultimedia(MultimediaEvent event) {
        evictMultimedia(event.getMultimedia());
    }

    @Override
    public void afterDeleteMultimedia(MultimediaEvent event) {
        evictMultimedia(event.getMultimedia());
    }

    private void evictMultimedia(Multimedia multimedia) {
        log.debug("Evicting MultimediaCache and ImageCache for multimedia with id {}", multimedia.getId());
        Ehcache multimediaCache = cacheManager.getEhcache(CacheManagerFactory.CacheNames.MultimediaCache.name());
        Ehcache imageCache = cacheManager.getEhcache(CacheManagerFactory.CacheNames.ImageCache.name());

        multimediaCache.remove(multimedia.getId());

        @SuppressWarnings("unchecked")
        List<String> keys = (List<String>)imageCache.getKeys();
        keys.stream()
            .filter(key -> key.startsWith(multimedia.getId() + "-"))
            .forEach(imageCache::remove);
    }
}
