package no.kantega.publishing.common.util;

import no.kantega.publishing.api.content.ContentAO;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

public class PrettyURLEncoderUtil {
    private static final Logger log = LoggerFactory.getLogger(PrettyURLEncoderUtil.class);

    @Autowired
    private ContentAO contentAO;

    @Cacheable("ContentUrlCache")
    public String getUrl(int associationId) {
        String title;
        try {
            title = StringUtils.defaultString(contentAO.getTitleByAssociationId(associationId), "");
        } catch (ContentNotFoundException e) {
            log.error(e.getMessage());
            title = "Unknown+title";
        }
        return PrettyURLEncoder.createContentUrl(associationId, title);
    }
}
