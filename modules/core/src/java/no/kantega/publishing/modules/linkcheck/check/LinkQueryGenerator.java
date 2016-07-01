package no.kantega.publishing.modules.linkcheck.check;

import java.util.List;

import static java.util.Collections.emptyList;

public interface LinkQueryGenerator {
    Query getQuery();

    class Query {
        public final String query;
        public final List<Object> params;

        public Query(String query, List<Object> params) {
            this.query = query;
            this.params = params;
        }

        public Query(String query) {
            this(query, emptyList());
        }

        @Override
        public String toString() {
            return "Query{" +
                    "query='" + query + '\'' +
                    ", params=" + params +
                    '}';
        }
    }
}
