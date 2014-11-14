package hu.radio.tilos.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity()
@Table(name = "author")
public class Author {

    @Id
    private int id;

    @Basic
    @Column
    private String name;

    @Basic
    @Column
    private String alias;

    @Basic
    @Column
    private String photo;

    @Basic
    @Column
    private String avatar;

    @Basic
    @Column
    private String introduction;

    @Basic
    @Column
    private String email;

    @OneToOne
    private User user;

    @OneToMany
    @JoinTable(
            name = "author_url",
            joinColumns = {@JoinColumn(name = "author_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "url_id", referencedColumnName = "id")})
    private List<Url> urls = new ArrayList<Url>();

    @OneToMany(mappedBy = "author")
    private List<Contribution> contributions = new ArrayList<Contribution>();

    public List<Contribution> getContributions() {
        return contributions;
    }

    public void setContributions(List<Contribution> contributions) {
        this.contributions = contributions;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Url> getUrls() {
        return urls;
    }

    public void setUrls(List<Url> urls) {
        this.urls = urls;
    }
}
