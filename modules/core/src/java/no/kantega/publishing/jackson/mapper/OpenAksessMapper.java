package no.kantega.publishing.jackson.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import no.kantega.publishing.jackson.mapper.serializer.SearchQuerySerializer;
import no.kantega.search.api.search.SearchQuery;

public class OpenAksessMapper extends ObjectMapper {
    public OpenAksessMapper() {
        SimpleModule module = new SimpleModule("OpenAksessModule");
        module.addSerializer(SearchQuery.class, new SearchQuerySerializer());
        this.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.registerModule(module);
    }
}
