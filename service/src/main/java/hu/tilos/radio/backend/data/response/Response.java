package hu.tilos.radio.backend.data.response;

public class Response {

    private boolean error = false;

    private String message;

    public Response() {
    }

    public Response(boolean error, String message) {
        this.error = error;
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
