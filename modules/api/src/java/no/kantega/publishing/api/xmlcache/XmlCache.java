package no.kantega.publishing.api.xmlcache;

import java.util.List;

public interface XmlCache {

    public XMLCacheEntry getXMLFromCache(String id);

    public void storeXMLInCache(XMLCacheEntry cacheEntry);

    /**
     * @return All entries in the XmlCache, without document field set.
     */
    public List<XMLCacheEntry> getSummary();
}
