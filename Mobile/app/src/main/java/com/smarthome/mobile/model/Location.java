package com.smarthome.mobile.model;

import java.util.List;

public class Location {
    private int id;
    private String name;
    private List<Device> devices;
    private Home home;
    private boolean isExpanded = false;

    public Location(int id, String name, List<Device> devices, Home home) {
        this.id = id;
        this.name = name;
        this.devices = devices;
        this.home = home;
    }

    public Location() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public Home getHome() {
        return home;
    }

    public void setHome(Home home) {
        this.home = home;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
