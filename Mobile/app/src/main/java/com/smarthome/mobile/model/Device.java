package com.smarthome.mobile.model;

import com.smarthome.mobile.enums.Type;
import com.smarthome.mobile.enums.Status;

public class Device {
    private int id;
    private String name;
    private Status status;
    private Type type;
    private Home home;

    public Device() {}

    public Device(int id, String name, Status status, Type type, Home home) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.type = type;
        this.home = home;
    }

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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Home getHome() {
        return home;
    }

    public void setHome(Home home) {
        this.home = home;
    }
}
