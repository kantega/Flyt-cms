package no.kantega.publishing.api.xmlcache;

import java.util.List;

public interface XmlCache {

    public XMLCacheEntry getXMLFromCache(String id);

    public void storeXMLInCache(XMLCacheEntry cacheEntry);

    public List<XMLCacheEntry> getSummary();
}
