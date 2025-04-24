package com.smarthome.mobile.dto.request;

public class RefreshRequest {
    private String refresh;

    public RefreshRequest(String refresh) {
        this.refresh = refresh;
    }

    public RefreshRequest() {}

    public String getRefresh() {
        return refresh;
    }

    public void setRefresh(String refresh) {
        this.refresh = refresh;
    }
}
