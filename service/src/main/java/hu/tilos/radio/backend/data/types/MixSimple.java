package hu.tilos.radio.backend.data.types;

import hu.radio.tilos.model.type.MixCategory;
import hu.radio.tilos.model.type.MixType;

public class MixSimple {

    private int id;

    private String author;

    private String title;

    private String link;

    private MixCategory category;

    private MixType type;

    private String date;

    private boolean withContent;

    private ShowSimple show;

    public ShowSimple getShow() {
        return show;
    }

    public void setShow(ShowSimple show) {
        this.show = show;
    }

    public boolean isWithContent() {
        return withContent;
    }

    public void setWithContent(boolean withContent) {
        this.withContent = withContent;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public MixCategory getCategory() {
        return category;
    }

    public void setCategory(MixCategory category) {
        this.category = category;
    }

    public MixType getType() {
        return type;
    }

    public void setType(MixType type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
