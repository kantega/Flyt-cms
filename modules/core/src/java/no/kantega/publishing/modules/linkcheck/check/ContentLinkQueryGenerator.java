package no.kantega.publishing.modules.linkcheck.check;

public class ContentLinkQueryGenerator implements LinkQueryGenerator {
    private int contentId;
    public ContentLinkQueryGenerator(int contentId) {
        this.contentId = contentId;
    }

    @Override
    public String getQuery() {
        return "select link.Id As Id, link.url As url from link,linkoccurrence where link.Id = linkoccurrence.LinkId and linkoccurrence.ContentId = '" + contentId + "'";
    }
}
