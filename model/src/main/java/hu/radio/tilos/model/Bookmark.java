package hu.radio.tilos.model;

import hu.radio.tilos.model.type.MixCategory;
import hu.radio.tilos.model.type.MixType;

import javax.persistence.*;
import java.util.Date;

@Entity()
@Table(name = "bookmark")
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Basic
    @Column(length = 160)
    private String title;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start")
    private Date realFrom;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end")
    private Date realTo;

    @ManyToOne()
    @JoinColumn(name = "radioshow_id", referencedColumnName = "id")
    private Show show;

    @ManyToOne()
    @JoinColumn(name = "episode_id", referencedColumnName = "id")
    private Episode episode;

    @Basic
    @Column(name = "full_episode", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean fullEpisode = false;

    @Basic
    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean selected = false;

    @Basic
    @Column()
    private int karma;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User author;

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

    public int getKarma() {
        return karma;
    }

    public void setKarma(int karma) {
        this.karma = karma;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public Episode getEpisode() {
        return episode;
    }

    public void setEpisode(Episode episode) {
        this.episode = episode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
