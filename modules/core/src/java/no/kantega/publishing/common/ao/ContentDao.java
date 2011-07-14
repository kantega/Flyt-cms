package no.kantega.publishing.common.ao;

import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;

public interface ContentDao {
    Content getContent(ContentIdentifier cid, boolean isAdminMode);
}
