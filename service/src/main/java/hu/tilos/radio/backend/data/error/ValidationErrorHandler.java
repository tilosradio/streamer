package hu.tilos.radio.backend.data.error;

import hu.tilos.radio.backend.data.response.ErrorResponse;

import javax.enterprise.context.Dependent;
import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class ValidationErrorHandler implements ExceptionMapper<ValidationException> {

    @Override
    public Response toResponse(ValidationException exception) {
        StringBuilder builder = new StringBuilder();
        for (ConstraintViolation<java.lang.Object> validationError : exception.getValidationErrors()) {
            builder.append(validationError.getPropertyPath() + " " + validationError.getMessage() + "\n");
        }
        return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("Hibás felhasználói adatok: " + builder.toString())).build();
    }
}
