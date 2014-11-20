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
}
