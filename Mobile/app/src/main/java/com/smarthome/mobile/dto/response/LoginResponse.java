package com.smarthome.mobile.dto.response;

public class LoginResponse {
    private String id;
    private String token;
    private String email;
    private String address;

    public LoginResponse(String token, String email, String address, String id) {
        this.token = token;
        this.email = email;
        this.address = address;
        this.id = id;
    }

    public LoginResponse() {}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
