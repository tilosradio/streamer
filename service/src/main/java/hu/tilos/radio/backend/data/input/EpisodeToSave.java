package hu.tilos.radio.backend.data.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import hu.tilos.radio.backend.show.ShowSimple;
import hu.tilos.radio.backend.tag.TagData;
import hu.tilos.radio.backend.data.types.TextData;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpisodeToSave {

    List<TagData> tags;

    private Date plannedFrom;

    private Date plannedTo;

    private Date realFrom;

    private Date realTo;

    private ShowSimple show;

    private TextData text;

    public List<TagData> getTags() {
        return tags;
    }

    public void setTags(List<TagData> tags) {
        this.tags = tags;
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

    public TextData getText() {
        return text;
    }

    public void setText(TextData text) {
        this.text = text;
    }
}
