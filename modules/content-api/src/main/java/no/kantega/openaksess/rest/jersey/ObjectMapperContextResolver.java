package no.kantega.openaksess.rest.jersey;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * @author Kristian Myrhaug
 * @since 2015-08-11
 */
@Provider
@Produces("application/json")
@Consumes("application/json")
public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

    private ObjectMapper objectMapper;

    @Override
    public ObjectMapper getContext(Class<?> type) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            objectMapper.enable(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector(TypeFactory.defaultInstance()));
        }
        return objectMapper;
    }
}
