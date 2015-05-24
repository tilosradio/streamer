package hu.tilos.radio.backend.data.error;

import hu.tilos.radio.backend.data.response.ErrorResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String s) {
        super(s);
    }
}
