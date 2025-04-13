package com.smarthome.mobile.model;

import java.util.List;

public class Home {
    private int id;
    private String email;
    private String address;
    private float temperature;
    private float humidity;
    private List<Location> locations;
    private List<Person> people;

    public Home(int id, String email, String address, float temperature,
                float humidity, List<Location> locations, List<Person> people) {
        this.id = id;
        this.email = email;
        this.address = address;
        this.temperature = temperature;
        this.humidity = humidity;
        this.locations = locations;
        this.people = people;
    }

    public Home() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public List<Person> getPeople() {
        return people;
    }

    public void setPeople(List<Person> people) {
        this.people = people;
    }
}
