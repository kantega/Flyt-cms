package no.kantega.openaksess.rest.jersey;

import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

/**
 * @author Kristian Myrhaug
 * @since 2015-08-11
 */
public class ResourceConfig extends org.glassfish.jersey.server.ResourceConfig {

    public ResourceConfig() {
        register(RequestContextFilter.class);
        register(ThrowableMapper.class);
        register(FaultMapper.class);
        register(new Binder());
        register(new ObjectMapperContextResolver());
        packages(
                "no.kantega.openaksess.rest.resources");
    }


}
