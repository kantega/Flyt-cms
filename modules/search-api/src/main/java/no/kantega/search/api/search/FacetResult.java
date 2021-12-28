package no.kantega.search.api.search;

public class FacetResult {
    private final String name;
    private final String value;
    private final Number count;
    private final String url;

    /**
     * @param name - the field this is a facet of.
     * @param value - the value of this facet.
     * @param count - the number of results in this facet.
     * @param url - the url to choose this facet.
     */
    public FacetResult(String name, String value, Number count, String url) {
        this.name = name;
        this.value = value;
        this.count = count;
        this.url = url;
    }

    /**
     *
     * @return the field this is a facet of.
     */
    public String getName() {
        return name;
    }

    /**
     * @return the value of this facet.
     */
    public String getValue() {
        return value;
    }

    /**
     * @return the number of results in this facet.
     */
    public Number getCount() {
        return count;
    }

    /**
     * @return the url to choose this facet.
     */
    public String getUrl() {
        return url;
    }
}
