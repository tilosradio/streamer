package hu.tilos.radio.backend;

import hu.tilos.radio.backend.data.error.ValidationException;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

public class ObjectValidator {

    @Inject
    private Validator validator;

    public void validate(Object o) {
        Set<ConstraintViolation<Object>> validationErrors = validator.validate(o);
        if (validationErrors.size() > 0) {
            throw new ValidationException(validationErrors);
        }
    }
}
