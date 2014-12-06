package hu.tilos.radio.backend;

import hu.radio.tilos.model.User;
import hu.tilos.radio.backend.data.UserInfo;
import hu.tilos.radio.backend.data.types.UserDetailed;

import javax.enterprise.context.RequestScoped;

/**
 * Please note that this is a request scoped bean based on token authentication.
 */
@RequestScoped
public class Session {

    private UserInfo currentUser;

    public UserInfo getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserInfo currentUser) {
        this.currentUser = currentUser;
    }
}
