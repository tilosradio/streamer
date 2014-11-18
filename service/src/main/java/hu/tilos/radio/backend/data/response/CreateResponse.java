package hu.tilos.radio.backend.data.response;

public class CreateResponse{

    private boolean success;

    private int id;

    public CreateResponse(boolean success) {
        this.success = success;
    }

    public CreateResponse(int id) {
        this.id = id;
        success = true;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
