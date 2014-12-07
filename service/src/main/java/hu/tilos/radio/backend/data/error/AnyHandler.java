package hu.tilos.radio.backend.data.error;


import hu.tilos.radio.backend.data.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class AnyHandler implements ExceptionMapper<Exception> {

    private static final Logger LOG = LoggerFactory.getLogger(AnyHandler.class);

    @Override
    public Response toResponse(Exception exception) {
        LOG.error(exception.getMessage(), exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).entity(new ErrorResponse(
                "Valami hiba történt (" + exception.getMessage() + "). Ha nem tudod miért, vagy nem javul a helyzet, fordulj a rendszergazdához")).build();
    }
}
