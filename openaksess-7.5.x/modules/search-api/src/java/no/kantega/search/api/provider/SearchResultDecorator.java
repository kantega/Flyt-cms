package no.kantega.search.api.provider;

import no.kantega.search.api.search.SearchQuery;
import no.kantega.search.api.search.SearchResult;

import java.util.Collection;
import java.util.Map;

/**
 * Decorator of particular types of search results.
 * This can be used to add extra information to each result.
 * There should be only one SearchResultDecorator for any one indexedContentType.
 * @param <SR> the custom class used ot decorate the search result.
 */
public interface SearchResultDecorator<SR extends SearchResult> {
    /**
     * @return the indexedContentTypes this decorator supports.
     */
    public Collection<String> handledindexedContentTypes();

    /**
     * Create an instance of SR based on the values of the search result.
     * @param resultMap containing the values stored in the index.
     * @param title - Either the value stored in the title field in the index, or the highlighted text.
     * @param description - Either the value stored in the descripton field in the index, or the highlighted text.
     * @param query that resulted in the particular result.
     */
    public SR decorate(Map<String, Object> resultMap,String title, String description, SearchQuery query);
}
