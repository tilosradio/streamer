package hu.tilos.radio.backend.data;

public class
        UpdateResponse {

    private boolean success;

    private int id;

    public UpdateResponse(boolean success) {
        this.success = success;
    }

    public UpdateResponse(int id) {
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
