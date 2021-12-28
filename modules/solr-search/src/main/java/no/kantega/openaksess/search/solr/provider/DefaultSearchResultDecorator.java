package no.kantega.openaksess.search.solr.provider;

import no.kantega.search.api.provider.SearchResultDecorator;
import no.kantega.search.api.search.SearchQuery;
import no.kantega.search.api.search.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

@Component
public class DefaultSearchResultDecorator implements SearchResultDecorator<SearchResult> {
    public static final String DEFAULT_INDEXED_CONTENT_TYPE = "default";
    private static final Logger log = LoggerFactory.getLogger(DefaultSearchResultDecorator.class);

    @Override
    public Collection<String> handledindexedContentTypes() {
        return Arrays.asList(DEFAULT_INDEXED_CONTENT_TYPE);
    }

    @Override
    public SearchResult decorate(Map<String, Object> resultMap, String title, String description, SearchQuery query) {
        SearchResult result =  new SearchResult((Integer) resultMap.get("id"),
                (Integer) resultMap.get("securityId"),
                (String) resultMap.get("indexedContentType"),
                title,
                description,
                (String) resultMap.get("url"),
                (Integer) resultMap.get("parentId"));

        Map<String, Object> resultFields = result.getResultFields();


        for (String resultFieldsItem: query.getResultFields()){
            try {
                resultFields.put(resultFieldsItem, resultMap.get(resultFieldsItem));
            } catch (Exception e){
                log.error("Unable to decorate result field " + resultFieldsItem);
            }
        }

        return result;
    }
}
