package hu.tilos.radio.backend.data.types;

public class Contribution {

    private String nick;

    private ShowSimple show;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public ShowSimple getShow() {
        return show;
    }

    public void setShow(ShowSimple show) {
        this.show = show;
    }
}
