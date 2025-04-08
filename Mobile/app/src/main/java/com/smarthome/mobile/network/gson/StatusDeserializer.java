package com.smarthome.mobile.network.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.smarthome.mobile.enums.Status;

import java.lang.reflect.Type;

public class StatusDeserializer implements JsonDeserializer<Status> {
    @Override
    public Status deserialize(JsonElement json, Type typeOfT,
                              JsonDeserializationContext context) throws JsonParseException {
        try {
            return Status.valueOf(json.getAsString().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Unknown type: " + json.getAsString());
        }
    }
}
