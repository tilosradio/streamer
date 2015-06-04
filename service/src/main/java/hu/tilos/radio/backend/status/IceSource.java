package hu.tilos.radio.backend.status;

public class IceSource {

    private String server_name;

    private String listenurl;

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
