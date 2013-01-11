package no.kantega.publishing.common.ao;

import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.Content;

public interface ContentDao {
    Content getContent(ContentIdentifier cid, boolean isAdminMode);
}
