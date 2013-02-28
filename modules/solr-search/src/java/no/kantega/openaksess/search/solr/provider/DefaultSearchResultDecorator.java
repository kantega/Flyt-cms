package no.kantega.openaksess.search.solr.provider;

import no.kantega.search.api.provider.SearchResultDecorator;
import no.kantega.search.api.search.SearchQuery;
import no.kantega.search.api.search.SearchResult;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

@Component
public class DefaultSearchResultDecorator implements SearchResultDecorator<SearchResult> {
    public static final String DEFAULT_INDEXED_CONTENT_TYPE = "default";

    @Override
    public Collection<String> handledindexedContentTypes() {
        return Arrays.asList(DEFAULT_INDEXED_CONTENT_TYPE);
    }

    @Override
    public SearchResult decorate(Map<String, Object> resultMap, String title, String description, SearchQuery query) {
       return new SearchResult((Integer) resultMap.get("id"),
                (Integer) resultMap.get("securityId"),
                (String) resultMap.get("indexedContentType"),
                title,
                description,
                (String) resultMap.get("url"),
                (Integer) resultMap.get("parentId"));
    }
}
