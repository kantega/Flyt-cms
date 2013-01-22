package no.kantega.publishing.common.util;

import no.kantega.publishing.common.ao.ContentAO;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.Cacheable;

public class PrettyURLEncoderUtil {

    @Cacheable("ContentUrlCache")
    public String getUrl(int associationId) {
        String title = StringUtils.defaultString(ContentAO.getTitleByAssociationId(associationId), "");
        return PrettyURLEncoder.createContentUrl(associationId, title);
    }
}
