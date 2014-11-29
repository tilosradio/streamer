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

    private UserDetailed currentUser;

    public UserDetailed getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserDetailed currentUser) {
        this.currentUser = currentUser;
    }
}
