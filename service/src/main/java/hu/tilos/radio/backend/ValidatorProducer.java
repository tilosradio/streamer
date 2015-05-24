package hu.tilos.radio.backend;


import javax.inject.Provider;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;



public class ValidatorProducer implements Provider<Validator> {

    private final Validator validator;

    public ValidatorProducer() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

    }


    @Override
    public Validator get() {
        return validator;
    }
}
