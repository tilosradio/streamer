package hu.tilos.radio.backend.data.error;


import hu.tilos.radio.backend.data.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class InternalErrorHandler implements ExceptionMapper<InternalErrorException> {

    private static final Logger LOG = LoggerFactory.getLogger(InternalErrorHandler.class);

    @Override
    public Response toResponse(InternalErrorException exception) {
        LOG.error(exception.getMessage(), exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).entity(new ErrorResponse(exception.getMessage())).build();
    }
}
