package com.smarthome.mobile.dto.response;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("id")
    private String id;

    @SerializedName("email")
    private String email;

    @SerializedName("address")
    private String address;

    @SerializedName("token")
    private TokenResponse tokens;

    public LoginResponse() {}

    public LoginResponse(String id, String email, String address, TokenResponse tokens) {
        this.id = id;
        this.email = email;
        this.address = address;
        this.tokens = tokens;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public TokenResponse getTokens() {
        return tokens;
    }

    public void setTokens(TokenResponse tokens) {
        this.tokens = tokens;
    }
}
