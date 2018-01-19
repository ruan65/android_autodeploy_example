package ru.mess.messenger.models.authentication;

import ru.mess.messenger.models.User;

/**
 * Created by neliousness on 12/10/17.
 */

public class AuthorizationResponse {

    boolean authorized;
    User payload;

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public User getPayload() {
        return payload;
    }

    public void setPayload(User payload) {
        this.payload = payload;
    }

    public AuthorizationResponse(boolean authorized, User payload) {
        this.authorized = authorized;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "AuthorizationResponse{" +
                "authorized=" + authorized +
                ", payload=" + payload +
                '}';
    }
}
