package hu.tilos.radio.backend.data.error;

import hu.tilos.radio.backend.data.response.ErrorResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AnyHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Ismeretlen hiba történt. Ha nem javult a helyzet fordulj a rendszergazdához")).build();
    }
}
