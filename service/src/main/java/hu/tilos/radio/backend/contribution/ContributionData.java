package hu.tilos.radio.backend.data.types;

public class ContributionData {

    WithId  show;

    WithId author;

    String nick;

    public WithId getShow() {
        return show;
    }

    public void setShow(WithId show) {
        this.show = show;
    }

    public WithId getAuthor() {
        return author;
    }

    public void setAuthor(WithId author) {
        this.author = author;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
