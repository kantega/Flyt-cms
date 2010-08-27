package no.kantega.publishing.jobs.cache;

import no.kantega.publishing.common.cache.ContentIdentifierCache;
import no.kantega.publishing.common.cache.ContentUrlCache;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.event.ContentEvent;
import no.kantega.publishing.event.ContentEventListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;

public class UpdateContentCachesListener extends ContentEventListenerAdapter {
    private ContentUrlCache contentUrlCache;

    @Override
    public void contentSaved(ContentEvent event) {
        Association a = event.getContent().getAssociation();
        if (a != null) {
            contentUrlCache.flushEntry(a.getId());
            ContentIdentifierCache.reloadCache();
        }
    }

    @Autowired
    public void setContentUrlCache(ContentUrlCache contentUrlCache) {
        this.contentUrlCache = contentUrlCache;
    }
}
