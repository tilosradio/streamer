package hu.tilos.radio.backend.data;

import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.data.types.AuthorWithContribution;

public class UserInfo {

    private String username;

    private String email;

    private Role role;

    private int id;

    private AuthorWithContribution author;

    public AuthorWithContribution getAuthor() {
        return author;
    }

    public void setAuthor(AuthorWithContribution author) {
        this.author = author;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
