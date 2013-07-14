package no.kantega.publishing.common.util;

import no.kantega.publishing.content.api.ContentAO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

public class PrettyURLEncoderUtil {

    @Autowired
    private ContentAO contentAO;

    @Cacheable("ContentUrlCache")
    public String getUrl(int associationId) {
        String title = StringUtils.defaultString(contentAO.getTitleByAssociationId(associationId), "");
        return PrettyURLEncoder.createContentUrl(associationId, title);
    }
}
