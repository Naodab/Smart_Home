package com.smarthome.mobile.model;

import androidx.annotation.NonNull;

import com.smarthome.mobile.enums.Type;
import com.smarthome.mobile.enums.Status;

public class Device {
    private int id;
    private String name;
    private Status status;
    private Type type;
    private Location location;

    public Device() {}

    public Device(int id, String name, Status status, Type type, Location location) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.type = type;
        this.location = location;
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

    public Location getRoom() {
        return location;
    }

    public void setRoom(Location location) {
        this.location = location;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getType().getVietnamese() + ": " + this.getName();
    }
}
