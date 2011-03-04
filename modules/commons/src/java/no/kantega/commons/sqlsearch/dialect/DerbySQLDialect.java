package no.kantega.commons.sqlsearch.dialect;

import no.kantega.commons.sqlsearch.resultlimit.ResultLimitorStrategy;
import no.kantega.commons.sqlsearch.resultlimit.ResultLimitorStrategyAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DerbySQLDialect implements SQLDialect {
    public ResultLimitorStrategy getResultLimitorStrategy() {
        return new ResultLimitorStrategyAdapter();
    }

    public String getDateAsString(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return "'" + df.format(date) + "'";
    }
}

