package com.smarthome.mobile.model;

public class Home {
    private String uid;
    private String email;
    private String address;

    public Home(String uid, String email, String address) {
        this.uid = uid;
        this.email = email;
        this.address = address;
    }

    public Home() {}

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
