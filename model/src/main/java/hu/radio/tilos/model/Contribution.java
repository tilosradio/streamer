package hu.radio.tilos.model;

import javax.persistence.*;

@Entity()
@Table(name = "contribution")
public class Contribution {

    @Id
    private int id;

    @Basic
    @Column
    private String nick;

    @ManyToOne()
    @JoinColumn(name = "radioshow_id")
    private Show show;

    @ManyToOne()
    @JoinColumn(name = "author_id")
    private Author author;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
