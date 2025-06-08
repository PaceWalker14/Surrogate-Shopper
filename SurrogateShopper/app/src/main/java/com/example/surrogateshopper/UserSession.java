package com.example.surrogateshopper;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {
    private static final String PREF_NAME = "UserSessionPrefs";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";

    private static UserSession instance;
    private SharedPreferences prefs;

    private UserSession(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized UserSession getInstance(Context context) {
        if (instance == null) {
            instance = new UserSession(context);
        }
        return instance;
    }

    public void setUser(String username, String userId) {
        prefs.edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_USER_ID, userId)
                .apply();
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}

