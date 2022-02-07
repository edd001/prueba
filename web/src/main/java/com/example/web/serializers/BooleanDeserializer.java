package com.example.web.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class BooleanDeserializer implements JsonDeserializer<Boolean> {

    @Override
    public Boolean deserialize(JsonElement json, Type typeOfT,
                               JsonDeserializationContext context)
            throws JsonParseException {
        try {
            int code = json.getAsInt();
            return code == 1;
        } catch (Exception e) {
            try {
                return json.getAsBoolean();
            } catch (Exception ignored) {
                return false;
            }
        }
    }
}
