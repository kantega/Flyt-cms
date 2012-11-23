package no.kantega.openaksess.jackson.mapper.serializer;

import no.kantega.search.api.search.SearchQuery;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

import java.io.IOException;

/**
 * Custom serializer for SearchQuery.
 * If Jackson tries to automatically serialize it, it tries to serialize the SearchContext,
 * leading to the following chain:
 * no.kantega.search.api.search.SearchResponse["query"]->no.kantega.search.api.search.SearchQuery["searchContext"]->no.kantega.openaksess.search.security.AksessSearchContext["securitySession"]->no.kantega.publishing.security.SecuritySession["realm"]->no.kantega.publishing.security.realm.SecurityRealm["roleManager"]->no.kantega.security.api.impl.dbuser.role.DbUserRoleManager["jdbcTemplate"]->org.springframework.jdbc.core.JdbcTemplate["dataSource"]->org.apache.commons.dbcp.BasicDataSource["logWriter"])
 */
public class SearchQuerySerializer extends SerializerBase<SearchQuery> {
    public SearchQuerySerializer() {
        super(SearchQuery.class);
    }

    @Override
    public void serialize(SearchQuery value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeObjectField("originalQuery", value.getOriginalQuery());
        jgen.writeObjectField("filterQueries", value.getFilterQueries());
        jgen.writeObjectField("facetFields", value.getFacetFields());
        jgen.writeObjectField("facetQueries", value.getFacetQueries());
        jgen.writeObjectField("dateRangeFacets", value.getDateRangeFacets());
        jgen.writeObjectField("resultsPerPage", value.getResultsPerPage());
        jgen.writeObjectField("pageNumber", value.getPageNumber());
        jgen.writeObjectField("groupField", value.getGroupField());
        jgen.writeObjectField("groupQueries", value.getGroupQueries());
        jgen.writeEndObject();
    }
}
