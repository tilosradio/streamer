package hu.tilos.radio.backend.data.response;

public class ErrorResponse extends Response {

    public ErrorResponse(String message) {
        super(true, message);
    }

    public ErrorResponse() {
        super(true, "");
    }
}
