package com.vodqa.messaging;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MessageBuilder {

    int id;
    public String method;
    public Map<String, Object> params;

    public MessageBuilder(int id, String domain, String methodName) {
        this.id = id;
        this.method = domain + "." + methodName;
    }

    public MessageBuilder(String methodName) {

        this.method = methodName;
    }

    public void addParameter(String key, Object value) {
        if (Objects.isNull(params))
            params = new HashMap<>();
        params.put(key, value);
    }

    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }


}
