package no.kantega.search.api.search.context;

import no.kantega.search.api.search.SearchContext;

import javax.servlet.http.HttpServletRequest;

public interface SearchContextCreator {

    /**
     * Creates a SearchContext based on the request
     * @param request - current request
     * @return a SearchContext based on the current request.
     */
    public SearchContext getSearchContext(HttpServletRequest request);
}