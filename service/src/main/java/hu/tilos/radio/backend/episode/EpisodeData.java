package hu.tilos.radio.backend.episode;


import hu.tilos.radio.backend.bookmark.BookmarkData;
import hu.tilos.radio.backend.episode.util.EpisodeUtil;
import hu.tilos.radio.backend.show.ShowSimple;
import hu.tilos.radio.backend.tag.TagData;
import hu.tilos.radio.backend.text.TextData;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Json transfer object for episodes;
 */
public class EpisodeData {

    private String id;

    private Date plannedFrom;

    private Date plannedTo;

    private Date realFrom;

    private Date realTo;

    private ShowSimple show;

    private TextData text;

    private String m3uUrl;

    private boolean extra;

    private boolean original = true;

    private Set<TagData> tags = new HashSet<>();

    private Set<BookmarkData> bookmarks = new HashSet<>();

    /**
     * false if generated from scheduling true if comes from real record.
     */
    private boolean persistent = false;

    private String url;

    public Set<TagData> getTags() {
        return tags;
    }

    public void setTags(Set<TagData> tags) {
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getPlannedFrom() {
        return plannedFrom;
    }

    public void setPlannedFrom(Date plannedFrom) {
        this.plannedFrom = plannedFrom;
        updateUrl();
    }

    private void updateUrl() {
        if (plannedFrom != null && getShow() != null) {
            url = "/episode/" + getShow().getAlias() + "/" + EpisodeUtil.YYYY_MM_DD.format(plannedFrom);
        } else {
            url = "";
        }
    }

    public long getLengthInSec(){
        return (plannedTo.getTime() - plannedFrom.getTime()) /1000;
    }

    public Date getPlannedTo() {
        return plannedTo;
    }

    public void setPlannedTo(Date plannedTo) {
        this.plannedTo = plannedTo;
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

    public ShowSimple getShow() {
        return show;
    }

    public void setShow(ShowSimple show) {
        this.show = show;
        updateUrl();
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public TextData getText() {
        return text;
    }

    public void setText(TextData text) {
        this.text = text;
    }

    public String getM3uUrl() {
        return m3uUrl;
    }

    public void setM3uUrl(String m3uUrl) {
        this.m3uUrl = m3uUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isExtra() {
        return extra;
    }

    public void setExtra(boolean extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "EpisodeData{" +
                "plannedFrom=" + plannedFrom +
                ", plannedTo=" + plannedTo +
                ", show=" + (show != null ? show.getName() : "null") +
                ", text=" + (text != null ? text.getTitle() : "null") +
                '}';
    }

    public Set<BookmarkData> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(Set<BookmarkData> bookmarks) {
        this.bookmarks = bookmarks;
    }

    public boolean isOriginal() {
        return original;
    }

    public void setOriginal(boolean original) {
        this.original = original;
    }

}
