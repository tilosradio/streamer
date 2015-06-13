package hu.tilos.radio.backend;

import hu.tilos.radio.backend.user.UserInfo;

import javax.enterprise.context.RequestScoped;

/**
 * Please note that this is a request scoped bean based on token authentication.
 */
@RequestScoped
public class Session {
    public Session() {
    }

    public Session(UserInfo currentUser) {
        this.currentUser = currentUser;
    }

    private UserInfo currentUser;

    public UserInfo getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserInfo currentUser) {
        this.currentUser = currentUser;
    }
}
