package hu.tilos.radio.backend.episode;


import hu.tilos.radio.backend.data.types.ShowSimple;
import hu.tilos.radio.backend.data.types.TextData;
import hu.tilos.radio.backend.episode.util.EpisodeUtil;
import hu.tilos.radio.backend.tag.TagData;

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

    private Set<TagData> tags = new HashSet<>();

    /**
     * false if generated from scheduling true if comes from real record.
     */
    private boolean persistent = false;

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
        return "/episode/" + getShow().getAlias() + "/" + EpisodeUtil.YYYY_MM_DD.format(getPlannedFrom());
    }
    public void setUrl(String dummy){

    }
}
