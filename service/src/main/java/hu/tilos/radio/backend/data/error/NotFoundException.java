package hu.tilos.radio.backend.data.error;


public class NotFoundException extends RuntimeException {
    public NotFoundException(String s) {
        super(s);
    }
}
