package com.smarthome.mobile.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.smarthome.mobile.dto.response.LoginResponse;

public class SessionManager {
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private static final String PREF_NAME = "AppPrefs";
    private static final String USER_TOKEN  = "UserToken";
    private static final String USER_REFRESH_TOKEN  = "UserRefresh";
    private static final String USER_EMAIL = "UserEmail";
    private static final String USER_ADDRESS =  "UserAddress";
    private static final String USER_ID = "UserID";
    private static final String PERSON_ID = "PersonID";

    public SessionManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveAuthToken(String token) {
        editor.putString(USER_TOKEN, token);
        editor.apply();
    }

    public void saveAuthRefresh(String refresh) {
        editor.putString(USER_REFRESH_TOKEN, refresh);
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

    public void saveUserId(int id) {
        editor.putInt(USER_ID, id);
        editor.apply();
    }

    public void savePersonID(int id) {
        editor.putInt(PERSON_ID, id);
        editor.apply();
    }

    public int fetchPersonID() {
        return sharedPreferences.getInt(PERSON_ID, 1);
    }

    public String fetchAuthToken() {
        return sharedPreferences.getString(USER_TOKEN, null);
    }

    public String fetchUserEmail() {
        return sharedPreferences.getString(USER_EMAIL, null);
    }

    public String fetchUserAddress() {
        return sharedPreferences.getString(USER_ADDRESS, null);
    }

    public int fetchUserID() {
        return sharedPreferences.getInt(USER_ID, 0);
    }

    public String fetchUserRefreshToken() {
        return sharedPreferences.getString(USER_REFRESH_TOKEN, null);
    }

    public void saveAuthData(LoginResponse response) {
        saveAuthToken(response.getTokens().getAccess());
        saveAuthRefresh(response.getTokens().getRefresh());
        saveUserAddress(response.getAddress());
        saveUserEmail(response.getEmail());
        saveUserId(response.getId());
    }

    public void clear() {
        editor.remove(USER_TOKEN);
        editor.remove(USER_ADDRESS);
        editor.remove(USER_EMAIL);
        editor.remove(USER_REFRESH_TOKEN);
        editor.apply();
    }
}
