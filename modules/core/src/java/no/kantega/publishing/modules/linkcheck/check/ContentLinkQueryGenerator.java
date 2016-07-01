package no.kantega.publishing.modules.linkcheck.check;

import java.util.Collections;

public class ContentLinkQueryGenerator implements LinkQueryGenerator {
    private int contentId;
    public ContentLinkQueryGenerator(int contentId) {
        this.contentId = contentId;
    }

    @Override
    public Query getQuery() {
        return new Query("select link.Id As Id, link.url As url from link,linkoccurrence where link.Id = linkoccurrence.LinkId and linkoccurrence.ContentId = ?", Collections.<Object>singletonList(contentId));
    }
}
