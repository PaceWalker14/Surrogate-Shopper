package com.example.surrogateshopper;

public class UserSession {
    private static UserSession instance;
    private String username;
    private String userId;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setUser(String username, String userId) {
        this.username = username;
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }

    public void clear() {
        username = null;
        userId = null;
    }
}

