package com.smarthome.mobile.enums;

public enum Type {
    LIGHT("Đèn"),
    DOOR("Cửa"),
    FAN("Quạt"),
    CURTAIN("Rèm");

    private final String vietnamese;

    Type(String vietnamese) {
        this.vietnamese = vietnamese;
    }

    public String getVietnamese() {
        return vietnamese;
    }
}
