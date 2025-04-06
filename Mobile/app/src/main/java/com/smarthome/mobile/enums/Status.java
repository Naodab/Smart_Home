package com.smarthome.mobile.enums;

public enum Status {
    ON("bật"),
    OFF("tắt"),
    OPEN("mở"),
    CLOSE("đóng"),
    LOW("yếu"),
    MEDIUM("trung bình"),
    HIGH("mạnh")
    ;

    private final String vietnamese;

    Status(String vietnamese) {
        this.vietnamese = vietnamese;
    }

    public String getVietnamese() {
        return this.vietnamese;
    }
}
