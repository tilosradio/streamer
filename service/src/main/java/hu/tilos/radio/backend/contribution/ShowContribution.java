package hu.tilos.radio.backend.contribution;


public class ShowContribution {

    private String nick;


    private AuthorReference author;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public AuthorReference getAuthor() {
        return author;
    }

    public void setAuthor(AuthorReference author) {
        this.author = author;
    }
}
