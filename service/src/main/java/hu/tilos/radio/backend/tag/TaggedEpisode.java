package hu.tilos.radio.backend.tag;

import java.util.Date;

public class TaggedEpisode {

    private Date plannedFrom;

    private String showName;

    private String showAlias;

    private String title;

    public Date getPlannedFrom() {
        return plannedFrom;
    }

    public void setPlannedFrom(Date plannedFrom) {
        this.plannedFrom = plannedFrom;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getShowAlias() {
        return showAlias;
    }

    public void setShowAlias(String showAlias) {
        this.showAlias = showAlias;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
