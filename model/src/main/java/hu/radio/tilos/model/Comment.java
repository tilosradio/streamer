package hu.radio.tilos.model;

import hu.radio.tilos.model.type.CommentStatus;
import hu.radio.tilos.model.type.CommentType;

import javax.persistence.*;
import java.util.Date;


@Entity()
@Table(name = "comment")
@NamedQuery(name = "comment.byTypeIdentifierStatusAuthor", query = "SELECT c FROM Comment c WHERE c.type = :type AND c.identifier = :identifier AND (c.status = :status OR c.author = :author) ORDER BY c.created")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private Comment parent;

    @Column
    private CommentType type;

    @Column
    private int identifier;

    @Column
    CommentStatus status = CommentStatus.NEW;

    @Column
    private String comment;

    @Column
    private Date moment;

    @ManyToOne
    private User author;

    @Column
    private Date created;

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public CommentStatus getStatus() {
        return status;
    }

    public void setStatus(CommentStatus status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Comment getParent() {
        return parent;
    }

    public void setParent(Comment parent) {
        this.parent = parent;
    }

    public CommentType getType() {
        return type;
    }

    public void setType(CommentType type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getMoment() {
        return moment;
    }

    public void setMoment(Date moment) {
        this.moment = moment;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
