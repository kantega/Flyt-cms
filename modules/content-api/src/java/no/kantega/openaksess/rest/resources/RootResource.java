package no.kantega.openaksess.rest.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author Kristian Myrhaug
 * @since 2015-08-11
 */
@Path("/")
@Consumes("application/json")
@Produces("application/json")
public class RootResource {

    @GET
    public String get() {
        return "Funny stuff";
    }
}
