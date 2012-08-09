package no.kantega.openaksess.search.taglib.label.resolver;

import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class LocationLabelResolver implements LabelResolver {
    public String handledPrefix() {
        return "location";
    }

    public String resolveLabel(String key) {
        int mostSpecificAssociation = getMostSpecificId(key);
        String retVal = null;
        ContentIdentifier cid = new ContentIdentifier();
        cid.setAssociationId(mostSpecificAssociation);
        Content content = ContentAO.getContent(cid, false);
        if (content != null) {
            retVal = content.getTitle();
        }
        return retVal;
    }

    private Integer getMostSpecificId(String key) {
        String idAfterLastSlash = StringUtils.substringAfterLast(key, "/");
        return Integer.parseInt(idAfterLastSlash);
    }
}
