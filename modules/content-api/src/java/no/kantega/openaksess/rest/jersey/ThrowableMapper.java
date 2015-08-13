package no.kantega.openaksess.rest.jersey;

import no.kantega.openaksess.rest.representation.FaultTransferObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kristian Myrhaug
 * @since 2015-06-24
 */
@Provider
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class ThrowableMapper implements ExceptionMapper<Throwable> {

    private static Logger logger = LoggerFactory.getLogger(ThrowableMapper.class);

    @Override
    public Response toResponse(Throwable throwable) {
        logger.error("Throwable caught", throwable);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new FaultTransferObject(getMessages(throwable))).build();
    }

    protected List<String> getMessages(Throwable throwable) {
        List<String> messages = null;
        String message = null;
        while (throwable != null) {
            message = throwable.getMessage();
            if (message != null) {
                if (messages == null) {
                    messages = new ArrayList<>();
                }
                messages.add(message);
            }
            throwable = throwable.getCause();
        }
        return messages;
    }
}

