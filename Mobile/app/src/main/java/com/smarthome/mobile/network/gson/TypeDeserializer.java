package com.smarthome.mobile.network.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.smarthome.mobile.enums.Type;

public class TypeDeserializer implements JsonDeserializer<Type> {
    @Override
    public Type deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
                            JsonDeserializationContext context) throws JsonParseException {
        try {
            return Type.valueOf(json.getAsString().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Unknown type: " + json.getAsString());
        }
    }
}
