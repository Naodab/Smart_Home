package com.smarthome.mobile.dto.response;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class TokenResponse {
    @SerializedName("access")
    private String access;

    @SerializedName("refresh")
    private String refresh;

    public TokenResponse(String access, String refresh) {
        this.access = access;
        this.refresh = refresh;
    }

    public TokenResponse() {}

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getRefresh() {
        return refresh;
    }

    public void setRefresh(String refresh) {
        this.refresh = refresh;
    }

    @NonNull
    @Override
    public String toString() {
        return "{'access': " + access + ", 'token': " + refresh + "}";
    }
}
