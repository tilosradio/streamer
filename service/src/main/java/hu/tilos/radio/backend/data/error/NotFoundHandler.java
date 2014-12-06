package hu.tilos.radio.backend.data.error;

import hu.tilos.radio.backend.data.response.ErrorResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundHandler implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException exception) {
        String errorMessage = "Nincs ilyen rekord";
        if (exception.getMessage() != null) {
            errorMessage = exception.getMessage();
        }
        return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse(errorMessage)).build();
    }
}
