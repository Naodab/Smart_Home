package com.smarthome.mobile.network;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private static final String PREF_NAME = "AppPrefs";
    private static final String USER_TOKEN  = "UserToken";
    private static final String USER_EMAIL = "UserEmail";
    private static final String USER_ID = "UserId";
    private static final String USER_ADDRESS =  "UserAddress";

    public SessionManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveAuthToken(String token) {
        editor.putString(USER_TOKEN, token);
        editor.apply();
    }

    public void saveUserEmail(String email) {
        editor.putString(USER_EMAIL, email);
        editor.apply();
    }

    public void saveUserAddress(String address) {
        editor.putString(USER_ADDRESS, address);
        editor.apply();
    }

    public void saveUserId(String id) {
        editor.putString(USER_TOKEN, id);
        editor.apply();
    }

    public String fetchAuthToken() {
        return sharedPreferences.getString(USER_TOKEN, null);
    }

    public String fetchUserId() {
        return sharedPreferences.getString(USER_ID, null);
    }

    public String fetchUserEmail() {
        return sharedPreferences.getString(USER_EMAIL, null);
    }

    public String fetchUserAddress() {
        return sharedPreferences.getString(USER_ADDRESS, null);
    }

    public void clear() {
        editor.remove(USER_TOKEN);
        editor.remove(USER_ID);
        editor.remove(USER_ADDRESS);
        editor.remove(USER_EMAIL);
        editor.apply();
    }
}
