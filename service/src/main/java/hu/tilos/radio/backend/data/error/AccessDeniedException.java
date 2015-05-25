package hu.tilos.radio.backend.data.error;

import javax.ws.rs.ext.Provider;

@Provider
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String s) {
        super(s);
    }
}
