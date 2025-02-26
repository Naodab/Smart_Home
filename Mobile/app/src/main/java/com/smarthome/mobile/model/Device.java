package com.smarthome.mobile.model;

import com.smarthome.mobile.enums.Category;

public class Device {
    private String id;
    private String name;
    private boolean state;
    private Category category;

    public Device() {}

    public Device(String id, String name, boolean state, Category category) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
