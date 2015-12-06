package hu.tilos.radio.backend.contribution;

import hu.tilos.radio.backend.data.input.ObjectReference;

public class ContributionToSave {

    private String nick;

    private ObjectReference show;

    private ObjectReference author;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public ObjectReference getShow() {
        return show;
    }

    public void setShow(ObjectReference show) {
        this.show = show;
    }

    public ObjectReference getAuthor() {
        return author;
    }

    public void setAuthor(ObjectReference author) {
        this.author = author;
    }
}
