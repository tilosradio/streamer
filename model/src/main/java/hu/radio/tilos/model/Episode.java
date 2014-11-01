package hu.radio.tilos.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "episode")
public class Episode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date plannedFrom;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date plannedTo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date realFrom;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date realTo;

    @ManyToOne()
    @JoinColumn(name = "radioshow_id", referencedColumnName = "id")
    Show show;

    @OneToOne()
    @JoinColumn(insertable = true, updatable = true, name = "textcontent_id", referencedColumnName = "id")
    TextContent text;

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

    public Date getRealTo() {
        return realTo;
    }

    public void setRealTo(Date realTo) {
        this.realTo = realTo;
    }

    public void setRealTo(Timestamp realTo) {
        this.realTo = realTo;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public TextContent getText() {
        return text;
    }

    public void setText(TextContent text) {
        this.text = text;
    }

}
