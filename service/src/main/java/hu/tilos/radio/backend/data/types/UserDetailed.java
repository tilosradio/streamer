package hu.tilos.radio.backend.data.types;


import hu.radio.tilos.model.Role;

import java.util.Date;

public class UserDetailed {

    private String id;

    private String username;

    private String email;

    private Role role;

    private Date passwordChangeTokenCreated;

    private String passwordChangeToken;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Date getPasswordChangeTokenCreated() {
        return passwordChangeTokenCreated;
    }

    public void setPasswordChangeTokenCreated(Date passwordChangeTokenCreated) {
        this.passwordChangeTokenCreated = passwordChangeTokenCreated;
    }

    public String getPasswordChangeToken() {
        return passwordChangeToken;
    }

    public void setPasswordChangeToken(String passwordChangeToken) {
        this.passwordChangeToken = passwordChangeToken;
    }
}
