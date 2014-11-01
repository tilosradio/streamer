package hu.radio.tilos.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "scheduling")
public class Scheduling {

    @Id
    private int id;

    @Basic
    int weekDay;

    @Basic
    int hourFrom;

    @Basic
    int minFrom;

    @Basic
    int duration;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date validFrom;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date validTo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date base;

    @Basic
    private int weekType;

    @ManyToOne()
    @JoinColumn(name = "radioshow_id", referencedColumnName = "id")
    private Show show;

    public int getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(int weekDay) {
        this.weekDay = weekDay;
    }

    public int getHourFrom() {
        return hourFrom;
    }

    public void setHourFrom(int hourFrom) {
        this.hourFrom = hourFrom;
    }

    public int getMinFrom() {
        return minFrom;
    }

    public void setMinFrom(int minFrom) {
        this.minFrom = minFrom;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public Date getBase() {
        return base;
    }

    public void setBase(Date base) {
        this.base = base;
    }

    public int getWeekType() {
        return weekType;
    }

    public void setWeekType(int weekType) {
        this.weekType = weekType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }
}
