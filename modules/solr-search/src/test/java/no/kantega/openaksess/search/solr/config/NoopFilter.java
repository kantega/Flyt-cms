package no.kantega.openaksess.search.solr.config;

import no.kantega.search.api.search.SearchResponse;
import no.kantega.search.api.search.SearchResultFilter;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class NoopFilter implements SearchResultFilter{
    @Override
    public void filterSearchResponse(SearchResponse searchResponse) {

    }
}
