package hu.tilos.radio.backend.bookmark;

import hu.tilos.radio.backend.data.types.UserLink;

import java.util.Date;

public class BookmarkData {
    private Date from;
    private Date to;
    private String title;
    private UserLink creator = new UserLink();
    private String m3uUrl;
    private boolean selected;

    public long getLengthInSec(){
        return (to.getTime() - from.getTime()) /1000;
    }
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

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

    public UserLink getCreator() {
        return creator;
    }

    public void setCreator(UserLink creator) {
        this.creator = creator;
    }

    public void setM3uUrl(String m3uUrl) {
        this.m3uUrl = m3uUrl;
    }

    public String getM3uUrl() {
        return m3uUrl;
    }
}
