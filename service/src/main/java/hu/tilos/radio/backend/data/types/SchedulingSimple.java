package hu.tilos.radio.backend.data.types;

import java.util.Date;

public class SchedulingSimple {

    int id;

    int weekDay;

    int hourFrom;

    int minFrom;

    int duration;

    private Date validFrom;

    private Date validTo;

    private Date base;

    private int weekType;

    private String text;

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

