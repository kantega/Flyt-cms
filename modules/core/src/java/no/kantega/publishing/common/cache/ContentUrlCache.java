package no.kantega.publishing.common.cache;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.NeedsRefreshException;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.util.PrettyURLEncoder;

public class ContentUrlCache {
    private Cache urlCache;

    public ContentUrlCache() {
        urlCache = new Cache(true, true, true, false, null, 1000);
    }

    public String getUrl(int associationId) {
        String url;
        String key = "" + associationId;
        try {
            url = (String)urlCache.getFromCache(key);
        } catch (NeedsRefreshException e) {
            String title = ContentAO.getTitleByAssociationId(associationId);
            if (title == null) {
                urlCache.cancelUpdate(key);
                return null;
            }
            url = PrettyURLEncoder.createContentUrl(associationId, title);
            urlCache.putInCache(key, url);
        }
        return null;
    }

    public void flushEntry(int associationId) {
        urlCache.flushEntry("" + associationId);
    }
}
