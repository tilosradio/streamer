package hu.tilos.radio.backend.data.response;

public class CreateResponse {

    private boolean success;

    private String ref;

    public CreateResponse(boolean success) {
        this.success = success;
    }

    public CreateResponse(String id) {
        this.ref = id;
        success = true;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getId() {
        return ref;
    }

    public void setId(String ref) {
        this.ref = ref;
    }
}
