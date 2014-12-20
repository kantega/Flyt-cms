package no.kantega.openaksess.search.taglib.label.resolver;

import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.content.api.ContentAO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationLabelResolver implements LabelResolver {

    @Autowired
    private SiteCache siteCache;

    @Autowired
    private ContentAO contentAO;

    public String handledPrefix() {
        return "location";
    }

    public String resolveLabel(String key) {
        String retVal = null;
        if(isDirectlyUnderRoot(key)){
            String siteIdString = StringUtils.substringAfter(key, "/");
            int siteId = Integer.parseInt(siteIdString);
            Site site = siteCache.getSiteById(siteId);
            if (site != null) {
                retVal = site.getName();
            }
        }else {
            int mostSpecificAssociation = getMostSpecificId(key);
            ContentIdentifier cid =  ContentIdentifier.fromAssociationId(mostSpecificAssociation);
            Content content = contentAO.getContent(cid, false);
            if (content != null) {
                retVal = content.getTitle();
            }
        }
        return retVal;
    }

    private Integer getMostSpecificId(String key) {
        String idAfterLastSlash = StringUtils.substringAfterLast(key, "/");
        return Integer.parseInt(idAfterLastSlash);
    }

    private boolean isDirectlyUnderRoot(String key) {
        return key.length() == 2;
    }
}
