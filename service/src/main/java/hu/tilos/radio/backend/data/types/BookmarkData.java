package hu.tilos.radio.backend.data.types;

import hu.tilos.radio.backend.data.types.ShowNamed;

import java.util.Date;

public class BookmarkData {

    public int id;

    public String title;

    public String content;

    public Date realFrom;

    public Date realTo;

    public ShowNamed show;

    public boolean fullEpisode;

    public boolean selected;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getRealFrom() {
        return realFrom;
    }

    public void setRealFrom(Date realFrom) {
        this.realFrom = realFrom;
    }

    public Date getRealTo() {
        return realTo;
    }

    public void setRealTo(Date realTo) {
        this.realTo = realTo;
    }

    public ShowNamed getShow() {
        return show;
    }

    public void setShow(ShowNamed show) {
        this.show = show;
    }

    public boolean isFullEpisode() {
        return fullEpisode;
    }

    public void setFullEpisode(boolean fullEpisode) {
        this.fullEpisode = fullEpisode;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
