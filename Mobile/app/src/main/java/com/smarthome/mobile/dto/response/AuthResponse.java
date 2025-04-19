package com.smarthome.mobile.dto.response;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("person_id")
    private int personId;

    @SerializedName("person_name")
    private String personName;

    public AuthResponse(int personId, String personName) {
        this.personId = personId;
        this.personName = personName;
    }

    public AuthResponse() {}

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }
}
