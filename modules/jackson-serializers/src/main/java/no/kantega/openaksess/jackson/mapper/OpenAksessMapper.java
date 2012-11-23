package no.kantega.openaksess.jackson.mapper;

import no.kantega.openaksess.jackson.mapper.serializer.SearchQuerySerializer;
import no.kantega.search.api.search.SearchQuery;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ser.CustomSerializerFactory;

public class OpenAksessMapper extends ObjectMapper {
    public OpenAksessMapper() {
        CustomSerializerFactory sf = new CustomSerializerFactory();
        sf.addSpecificMapping(SearchQuery.class, new SearchQuerySerializer());
        this.setSerializerFactory(sf);
    }
}
