package hu.tilos.radio.backend.episode;

import hu.tilos.radio.backend.data.types.UserLink;

import java.util.Date;

public class BookmarkData {
    private Date from;
    private Date to;
    private String title;
    private UserLink author;

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UserLink getAuthor() {
        return author;
    }

    public void setAuthor(UserLink author) {
        this.author = author;
    }
}
