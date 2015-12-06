package hu.tilos.radio.backend.status;

public class IceSource {

    private String server_name;

    private String listenurl;

    private String server_description;

    public String getServer_description() {
        return server_description;
    }

    public void setServer_description(String server_description) {
        this.server_description = server_description;
    }

    public String getListenurl() {
        return listenurl;
    }

    public void setListenurl(String listenurl) {
        this.listenurl = listenurl;
    }

    public String getServer_name() {
        return server_name;
    }

    public void setServer_name(String server_name) {
        this.server_name = server_name;
    }
}
