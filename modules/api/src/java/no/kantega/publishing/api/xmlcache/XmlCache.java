package no.kantega.publishing.api.xmlcache;

import java.util.List;

public interface XmlCache {

    XMLCacheEntry getXMLFromCache(String id);

    void storeXMLInCache(XMLCacheEntry cacheEntry);

    /**
     * @return All entries in the XmlCache, without document field set.
     */
    List<XMLCacheEntry> getSummary();
}
