package no.kantega.openaksess.rest.jersey;

import no.kantega.openaksess.rest.domain.Fault;
import no.kantega.openaksess.rest.representation.FaultTo;
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
public class FaultMapper implements ExceptionMapper<Fault> {

    private static Logger logger = LoggerFactory.getLogger(FaultMapper.class);

    @Override
    public Response toResponse(Fault throwable) {
        logger.error("Fault caught", throwable);
        return Response.status(throwable.getCode()).entity(new FaultTo(getMessages(throwable))).build();
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

