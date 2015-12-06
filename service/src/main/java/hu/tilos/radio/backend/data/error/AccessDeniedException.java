package hu.tilos.radio.backend.data.error;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String s) {
        super(s);
    }
}
