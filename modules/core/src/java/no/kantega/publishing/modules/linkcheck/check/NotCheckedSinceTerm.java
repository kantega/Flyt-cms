package no.kantega.publishing.modules.linkcheck.check;

import no.kantega.publishing.common.util.database.dbConnectionFactory;

import java.util.Date;

public class NotCheckedSinceTerm implements LinkQueryGenerator {
    private final Date notCheckedSince;
    private final int maxLinksPerDay;
    private final String driver;

    public NotCheckedSinceTerm(Date notCheckedSince, int maxLinksPerDay) {
        this.notCheckedSince = notCheckedSince;
        this.maxLinksPerDay = maxLinksPerDay;
        driver = dbConnectionFactory.getDriverName();

    }
    @Override
    public Date getNotCheckedSince() {
        return notCheckedSince;
    }

    public int getMaxLinksPerDay() {
        return maxLinksPerDay;
    }

    @Override
    public String getQuery() {
        StringBuilder query = new StringBuilder();
        if (driver.contains("jtds")) {
            query.append("select top ").append(maxLinksPerDay);
        } else {
            query.append("select");
        }

        query.append(" Id, url from link where lastchecked is null or lastchecked < ?");

        if (driver.contains("mysql") || driver.contains("postgresql")) {
            // Only limit if not using join
            query.append(" limit 0,").append(maxLinksPerDay);
        } else if(driver.contains("oracle")){
            query.append("AND ROWNUM <= ").append(maxLinksPerDay);
        }
        return query.toString();
    }
}
