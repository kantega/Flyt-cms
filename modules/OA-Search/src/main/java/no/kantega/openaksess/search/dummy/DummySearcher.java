package no.kantega.openaksess.search.dummy;

import no.kantega.search.api.search.SearchQuery;
import no.kantega.search.api.search.SearchResponse;
import no.kantega.search.api.search.Searcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Collections.emptyList;

@Component
public class DummySearcher implements Searcher {
    private static final Logger log = LoggerFactory.getLogger(DummySearcher.class);

    @Override
    public SearchResponse search(SearchQuery query) {
        log.warn("returning empty result for search({}})", query);
        return new SearchResponse(query, 0L, 0, emptyList());
    }

    @Override
    public List<String> suggest(SearchQuery query) {
        log.warn("returning empty result for suggest({}})", query);
        return emptyList();
    }

    @Override
    public List<String> spell(SearchQuery query) {
        log.warn("returning empty result for spell({}})", query);
        return emptyList();
    }
}
