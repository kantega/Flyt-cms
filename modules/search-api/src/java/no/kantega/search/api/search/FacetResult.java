package no.kantega.search.api.search;

public class FacetResult {
    private final String name;
    private final String value;
    private final Number count;
    private final String url;

    public FacetResult(String name, String value, Number count, String url) {
        this.name = name;
        this.value = value;
        this.count = count;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public Number getCount() {
        return count;
    }

    public String getUrl() {
        return url;
    }
}
