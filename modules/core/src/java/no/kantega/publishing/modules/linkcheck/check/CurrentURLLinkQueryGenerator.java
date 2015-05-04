package no.kantega.publishing.modules.linkcheck.check;

import no.kantega.publishing.common.util.database.dbConnectionFactory;

import java.util.Date;

public class CurrentURLLinkQueryGenerator implements LinkQueryGenerator {
    private final String driver;
    private int currentContentId;
    private final Date notCheckedSince;
    public CurrentURLLinkQueryGenerator( Date notCheckedSince,int currentContentId) {
        this.notCheckedSince = notCheckedSince;
        this.driver = dbConnectionFactory.getDriverName();
        this.currentContentId = currentContentId;
    }

    @Override
    public String getQuery() {
        return "select link.Id As Id, link.url As url from link,linkoccurrence where link.Id = linkoccurrence.LinkId and linkoccurrence.ContentId = '" + currentContentId + "'";
    }

    @Override
    public Date getNotCheckedSince() {
        return notCheckedSince;
    }
}
