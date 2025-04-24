package com.smarthome.mobile.model;

import java.util.List;

public class Person {
    private int id;
    private String name;
    private Home home;
    private List<History> histories;

    public Person(int id, String name, Home home, List<History> histories) {
        this.id = id;
        this.name = name;
        this.home = home;
        this.histories = histories;
    }

    public Person() {}

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

    public Home getHome() {
        return home;
    }

    public void setHome(Home home) {
        this.home = home;
    }

    public List<History> getHistories() {
        return histories;
    }

    public void setHistories(List<History> histories) {
        this.histories = histories;
    }
}
