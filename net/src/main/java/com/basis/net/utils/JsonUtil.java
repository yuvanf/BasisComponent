package com.basis.net.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class JsonUtil {
    private JsonObject jsonObject = new JsonObject();
    private String[] keys;

    public JsonUtil addKeys(String... args) {
        this.keys = args;
        return this;
    }

    public JsonUtil addValues(Object[] objects) {
        int count = keys.length > objects.length ? objects.length : keys.length;
        for (int i = 0; i < count; i++) {
            jsonObject.addProperty(keys[i], objects.toString());
        }
        return this;
    }

    @Override
    public String toString() {
        return jsonObject.toString();
    }

    public JsonElement toJson() {
        return jsonObject;
    }

    public JsonUtil addPty(String key, Object object) {
        if (object instanceof JsonElement) {
            jsonObject.add(key, (JsonElement) object);
        } else {
            jsonObject.add(key, object == null ? null : GsonConvertUtils.getGson().toJsonTree(object));
        }
        return this;
    }
}
