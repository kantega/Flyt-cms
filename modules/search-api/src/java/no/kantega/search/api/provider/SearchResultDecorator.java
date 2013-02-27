package no.kantega.search.api.provider;

import no.kantega.search.api.search.SearchQuery;
import no.kantega.search.api.search.SearchResult;

import java.util.Collection;
import java.util.Map;

/**
 *
 * @param <SR>
 */
public interface SearchResultDecorator<SR extends SearchResult> {
    /**
     * @return the indexedContentTypes this decorator supports.
     */
    public Collection<String> handledindexedContentTypes();

    /**
     *
     *
     * @param resultMap
     * @param description
     *@param query @return
     */
    public SR decorate(Map<String, Object> resultMap, String description, SearchQuery query);
}
