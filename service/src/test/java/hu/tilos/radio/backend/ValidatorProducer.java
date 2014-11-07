package hu.tilos.radio.backend;

import javax.enterprise.inject.Produces;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;


public class ValidatorProducer {

    @Produces
    public Validator createValidator(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return validator;
    }
}
