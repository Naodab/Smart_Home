package com.smarthome.mobile.model;

import com.smarthome.mobile.enums.Status;

import java.util.Date;

public class History {
    private int id;
    private Status status;
    private Date time;
    private Device device;
    private Person person;

    public History(int id, Status status, Date time, Device device, Person person) {
        this.id = id;
        this.status = status;
        this.time = time;
        this.device = device;
        this.person = person;
    }

    public History() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
