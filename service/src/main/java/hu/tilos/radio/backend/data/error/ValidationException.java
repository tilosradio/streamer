package hu.tilos.radio.backend.data.error;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class ValidationException extends RuntimeException {

    Set<ConstraintViolation<Object>> validationErrors;

    public ValidationException(Set<ConstraintViolation<Object>> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public Set<ConstraintViolation<Object>> getValidationErrors() {
        return validationErrors;
    }

    @Override
    public String getMessage() {
        String errors = "";
        for (ConstraintViolation violation : validationErrors) {
            errors += violation.getPropertyPath() + " " + violation.getMessage() + "; ";
        }
        return (super.getMessage() == null ? "" : super.getMessage() + ": ") + errors;

    }
}
