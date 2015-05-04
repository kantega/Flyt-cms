package no.kantega.publishing.modules.linkcheck.check;

import java.util.Date;

public interface LinkQueryGenerator {
    public String getQuery();

    public Date getNotCheckedSince();
}
