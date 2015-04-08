package hu.tilos.radio.backend.data.types;

public class Contribution {

    private String nick;

    private ShowReference show;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public ShowReference getShow() {
        return show;
    }

    public void setShow(ShowReference show) {
        this.show = show;
    }
}
