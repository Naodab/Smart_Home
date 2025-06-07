package com.smarthome.mobile.dto.request;

public class ChangeDeviceRequest {
    private int id;
    private String status;
    private int personId;

    public ChangeDeviceRequest(int id, String status, int personId) {
        this.id = id;
        this.status = status;
        this.personId = personId;
    }

    public ChangeDeviceRequest() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }
}
