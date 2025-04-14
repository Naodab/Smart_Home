package com.smarthome.mobile.enums;

public enum Status {
    ON("bật", "on"),
    OFF("tắt", "off"),
    OPEN("mở", "open"),
    CLOSE("đóng", "close"),
    STOP("dừng", "0"),
    LOW("yếu", "1"),
    MEDIUM("trung bình", "2"),
    HIGH("mạnh", "3")
    ;

    private final String vietnamese;
    private final String apiValue;

    Status(String vietnamese, String apiValue) {
        this.vietnamese = vietnamese;
        this.apiValue = apiValue;
    }

    public String getVietnamese() {
        return this.vietnamese;
    }

    public String toApiValue() {
        return this.apiValue;
    }

    public static Status fromApiValue(String apiValue) {
        if ("on".equals(apiValue))
            return Status.ON;
        else if ("off".equals(apiValue))
            return Status.OFF;
        else if ("open".equals(apiValue))
            return Status.OPEN;
        else if ("close".equals(apiValue))
            return Status.CLOSE;
        else if ("0".equals(apiValue))
            return Status.STOP;
        else if ("1".equals(apiValue))
            return Status.LOW;
        else if ("2".equals(apiValue))
            return Status.MEDIUM;
        return Status.HIGH;
    }
}
