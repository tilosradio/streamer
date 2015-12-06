package hu.tilos.radio.backend;

import hu.tilos.radio.backend.user.UserInfo;


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
