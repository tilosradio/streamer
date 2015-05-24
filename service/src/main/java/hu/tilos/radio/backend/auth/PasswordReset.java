package hu.tilos.radio.backend.auth;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class PasswordReset {

    @NotNull
    private String token;

    private String email;

    @NotNull
    @Size(min = 8, max = 16)
    private String password;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
