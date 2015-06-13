package hu.tilos.radio.backend.bookmark;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class BookmarkToSave {

    @NotNull
    private Date from;

    @NotNull
    private Date to;

    @NotNull
    private String title;

    @NotNull
    private String episodeRef;

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

    public String getEpisodeRef() {
        return episodeRef;
    }

    public void setEpisodeRef(String episodeRef) {
        this.episodeRef = episodeRef;
    }
}
