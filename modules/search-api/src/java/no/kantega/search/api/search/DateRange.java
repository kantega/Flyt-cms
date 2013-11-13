package no.kantega.search.api.search;

import java.util.Date;

public class DateRange {
    private final String field;
    private final String gap;
    private final Date from;
    private final Date to;

    /**
     * @param field to create date range from
     * @param from the date to start
     * @param to the date to end
     * @param gap spacing between facets. As seen in DateMathParser. Example 1DAY.
     * @see org.apache.solr.util.DateMathParser
     */
    public DateRange(String field, Date from, Date to, String gap) {
        this.field = field;
        this.gap = gap;
        this.from = from;
        this.to = to;
    }

    /**
     * @return the field this DateRange should apply to.
     */
    public String getField() {
        return field;
    }

    /**
     * @return the spacing between each facet.
     * @see org.apache.solr.util.DateMathParser
     */
    public String getGap() {
        return gap;
    }

    /**
     * @return the start of this range.
     */
    public Date getFrom() {
        return from;
    }

    /**
     * @return the end of this range.
     */
    public Date getTo() {
        return to;
    }
}
