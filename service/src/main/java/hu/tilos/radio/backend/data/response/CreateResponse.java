package hu.tilos.radio.backend.data.response;

public class CreateResponse {

    private boolean success;

    private String id;

    public CreateResponse(boolean success) {
        this.success = success;
    }

    public CreateResponse(String id) {
        this.id = id;
        success = true;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getId() {
        return id;
    }

    public void setId(String ref) {
        this.id = ref;
    }
}
