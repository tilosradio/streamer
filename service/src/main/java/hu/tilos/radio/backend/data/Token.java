package hu.tilos.radio.backend.data;

import hu.radio.tilos.model.Role;

public class Token {

    private String username;

    private Role role;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
