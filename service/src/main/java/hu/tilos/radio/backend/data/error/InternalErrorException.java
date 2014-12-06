package hu.tilos.radio.backend.data.error;

public class InternalErrorException extends RuntimeException {

    public InternalErrorException() {
    }

    public InternalErrorException(String message) {
        super(message);
    }

    public InternalErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
