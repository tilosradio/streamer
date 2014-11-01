package hu.tilos.radio.backend.data.types;

import hu.radio.tilos.model.type.MixCategory;
import hu.radio.tilos.model.type.MixType;
import hu.tilos.radio.backend.data.EntitySelector;

public class MixData {

    private int id;

    private String author;

    private String title;

    private String file;

    private String link;

    private ShowSimple show;

    private MixType type;

    private MixCategory category;

    private String date;

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public ShowSimple getShow() {
        return show;
    }

    public void setShow(ShowSimple show) {
        this.show = show;
    }

    public MixType getType() {
        return type;
    }

    public void setType(MixType type) {
        this.type = type;
    }

    public MixCategory getCategory() {
        return category;
    }

    public void setCategory(MixCategory category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
