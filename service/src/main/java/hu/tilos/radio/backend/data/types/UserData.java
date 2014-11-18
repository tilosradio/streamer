package hu.tilos.radio.backend.data.types;


import hu.tilos.radio.backend.data.types.RoleData;

public class UserData {

    private int id;

    private String username;

    private String email;

    private RoleData role;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public RoleData getRole() {
        return role;
    }

    public void setRole(RoleData role) {
        this.role = role;
    }
}
