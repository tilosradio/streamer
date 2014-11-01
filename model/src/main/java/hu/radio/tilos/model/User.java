package hu.radio.tilos.model;

import javax.persistence.*;

@Entity()
@Table(name = "user")
@NamedQuery(name = "user.byUsername", query = "SELECT u FROM User u WHERE u.username = :username")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Basic
    @Column(name = "role_id")
    private Role role;

    @Basic
    @Column
    private String username;

    @Basic
    @Column
    private String password;

    @Basic
    @Column
    private String email;

    @Basic
    @Column
    private String salt;

    @OneToOne(mappedBy = "user")
    private Author author;

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
