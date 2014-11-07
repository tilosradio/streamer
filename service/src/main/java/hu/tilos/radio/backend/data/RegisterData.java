package hu.tilos.radio.backend.data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RegisterData {
    @NotNull
    @Size(min = 3, max = 16)
    private String username;

    @NotNull
    @Size(min = 8, max = 16)
    private String password;

    @NotNull
    private String email;

    @NotNull
    private String captchaChallenge;

    @NotNull
    private String captchaResponse;

    public RegisterData() {
    }

    public RegisterData(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCaptchaChallenge() {
        return captchaChallenge;
    }

    public void setCaptchaChallenge(String captchaChallenge) {
        this.captchaChallenge = captchaChallenge;
    }

    public String getCaptchaResponse() {
        return captchaResponse;
    }

    public void setCaptchaResponse(String captchaResponse) {
        this.captchaResponse = captchaResponse;
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
}
