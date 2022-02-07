package com.example.web.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class BooleanSerializer implements JsonSerializer<Boolean> {

    @Override
    public JsonElement serialize(Boolean src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        return src == null ? null : new JsonPrimitive(src);
    }
}
