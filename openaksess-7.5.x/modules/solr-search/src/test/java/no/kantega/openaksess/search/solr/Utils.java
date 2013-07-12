package no.kantega.openaksess.search.solr;

import no.kantega.search.api.search.SearchContext;

public class Utils {
    public static SearchContext getDummySearchContext() {
        return new SearchContext() {
            public String getSearchUrl() {
                return "";
            }
        };
    }
}
