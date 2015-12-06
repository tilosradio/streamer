package hu.tilos.radio.backend.data.response;

public class OkResponse extends Response {
    public OkResponse() {
    }

    public OkResponse(String message) {
        super(false, message);
    }
}
