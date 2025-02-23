package com.smarthome.mobile.model;

public class UserAuthentication {
    private String uid;
    private String email;

    public UserAuthentication(String uid, String email) {
        this.uid = uid;
        this.email = email;
    }

    public UserAuthentication() {}

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
