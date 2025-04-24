package com.smarthome.mobile.dto.response;

public class ChangeDeviceResponse {
    private boolean success;

    public ChangeDeviceResponse(boolean success) {
        this.success = success;
    }

    public ChangeDeviceResponse() {}

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
