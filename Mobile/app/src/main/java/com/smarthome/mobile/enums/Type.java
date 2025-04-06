package com.smarthome.mobile.enums;

public enum Type {
    LIGHT("đèn"),
    DOOR("cửa"),
    FAN("quạt"),
    CURTAIN("rèm");

    private final String vietnamese;

    Type(String vietnamese) {
        this.vietnamese = vietnamese;
    }

    public String getVietnamese() {
        return vietnamese;
    }
}
