package org.example.url_shortner.models;

import io.vertx.core.json.JsonObject;

public class ErrorMessage {
    int statusCode;
    String errorMessage;
    public ErrorMessage(int statusCode, String errorMessage) {
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }

    public JsonObject toJsonObject() {
        return new JsonObject()
                .put("statusCode", statusCode)
                .put("errorMessage", errorMessage);
    }
}
