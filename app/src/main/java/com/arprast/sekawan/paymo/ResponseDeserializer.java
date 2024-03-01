package com.arprast.sekawan.paymo;


import com.arprast.sekawan.paymo.response.TokenResponse;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class ResponseDeserializer implements JsonDeserializer<Response> {

    public ResponseDeserializer() {
    }

    public Response deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonElement type = json.getAsJsonObject().get("type");
        Response message = null;
        if (type != null) {
            Class<? extends Response> clazz = this.getMessageClass(type.getAsString());
            if (clazz != null) {
                message = (Response) context.deserialize(json, clazz);
            }
        }

        return message;
    }

    private Class<? extends Response> getMessageClass(String type) {
        if (Response.TYPE_GENERATE_TOKEN.equals(type)) {
            return TokenResponse.class;
        } else {
            return null;
        }
    }
}
