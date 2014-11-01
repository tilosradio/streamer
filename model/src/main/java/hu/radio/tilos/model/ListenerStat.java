package hu.radio.tilos.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "listener_stat")
public class ListenerStat {

    @Id
    private int id;

    @Column
    private int type;

    @Column
    private int count;

    @Column
    private Date date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ListenerStat{" +
                "id=" + id +
                ", type=" + type +
                ", count=" + count +
                ", date=" + date +
                '}';
    }
}
