package com.smarthome.mobile.model;

import java.util.List;

public class Home {
    private String uid;
    private String email;
    private String address;
    private List<Device> devices;
    private List<Person> people;

    public Home(String uid, String email, String address, List<Device> devices, List<Person> people) {
        this.uid = uid;
        this.email = email;
        this.address = address;
        this.devices = devices;
        this.people = people;
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

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public List<Person> getPeople() {
        return people;
    }

    public void setPeople(List<Person> people) {
        this.people = people;
    }
}
