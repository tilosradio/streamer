package hu.tilos.radio.backend.data.error;

import hu.tilos.radio.backend.data.response.ErrorResponse;

import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundHandler implements ExceptionMapper<NoResultException> {
    @Override
    public Response toResponse(NoResultException exception) {
        return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Nincs ilyen rekord")).build();
    }
}
