package no.kantega.publishing.modules.linkcheck.check;

import no.kantega.publishing.common.util.database.dbConnectionFactory;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
    public Query getQuery() {
        List<Object> params = new LinkedList<>();
        StringBuilder query = new StringBuilder();
        if (driver.contains("jtds")) {
            query.append("select top ?");
            params.add(maxLinksPerDay);
        } else {
            query.append("select");
        }

        query.append(" Id, url from link where lastchecked is null or lastchecked < ?");
        params.add(notCheckedSince);

        if (driver.contains("mysql") || driver.contains("postgresql")) {
            // Only limit if not using join
            query.append(" limit 0,?");
            params.add(maxLinksPerDay);
        } else if(driver.contains("oracle")){
            query.append("AND ROWNUM <= ?");
            params.add(maxLinksPerDay);
        }
        return new Query(query.toString(), params);
    }
}
