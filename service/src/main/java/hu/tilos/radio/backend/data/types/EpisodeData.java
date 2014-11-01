package hu.tilos.radio.backend.data.types;

import hu.tilos.radio.backend.data.types.ShowSimple;
import hu.tilos.radio.backend.data.types.TextData;

import java.util.Date;

/**
 * Json transfer object for episodes;
 */
public class EpisodeData {

    private int id;

    private Date plannedFrom;

    private Date plannedTo;

    private Date realFrom;

    private Date realTo;

    private ShowSimple show;

    private TextData text;

    private String m3uUrl;
    /**
     * false if generated from scheduling true if comes from real record.
     */
    private boolean persistent = false;


    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public void setRealTo(Date realTo) {
        this.realTo = realTo;
    }

    public Date getRealTo() {
        return realTo;
    }

    public ShowSimple getShow() {
        return show;
    }

    public void setShow(ShowSimple show) {
        this.show = show;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public boolean isPersistent() {
        return persistent;
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
}
