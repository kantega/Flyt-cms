package no.kantega.search.api.search;

public interface SearchContext {
    /**
     * @return the base url the current search is executed from, i.e. without request parameters.
     */
    public String getSearchUrl();
}
