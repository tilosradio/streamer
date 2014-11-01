package hu.radio.tilos.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity()
@Table(name = "textcontent")
public class TextContent implements EntityWithTag{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Basic
    @Column
    private String title;

    @Basic
    @Column
    private String type;

    @Basic
    @Column
    private String format;

    @Basic
    @Column
    private String content;

    @Basic
    @Column
    private String alias;

    @ManyToMany(mappedBy = "taggedTexts")
    private List<Tag> tags = new ArrayList<Tag>();

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
