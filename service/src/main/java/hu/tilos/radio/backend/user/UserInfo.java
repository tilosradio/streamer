package hu.tilos.radio.backend.user;

import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.author.AuthorWithContribution;

import java.util.ArrayList;
import java.util.List;

public class UserInfo {

    private String username;

    private String email;

    private Role role;

    private String id;

    private AuthorWithContribution author;

    private List<String> permissions = new ArrayList<>();

    public UserInfo() {
    }

    public UserInfo(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

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

    public String getId() {
        return id;
    }
}
