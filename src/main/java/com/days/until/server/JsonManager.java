package com.days.until.server;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonManager {
    
    private static JsonManager INSTANCE = null;

    public static JsonManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JsonManager();
        }

        return INSTANCE;
    }

    private final ObjectMapper mapper = new ObjectMapper();

    public JsonManager() {
        mapper.registerModule(new JavaTimeModule());
    }

    public ObjectNode getFailureReturn() {
        return mapper.valueToTree(Collections.singletonMap("status", "FAILURE"));
    }

    public ObjectNode getSuccessReturn() {
        return mapper.valueToTree(Collections.singletonMap("status", "SUCCESS"));
    }

    public ArrayNode getSuccessReturn(JsonNode node) {
        ArrayNode data = (ArrayNode) node;
        data.addObject().put("status", "SUCCESS");
        return data;
    }

    public ArrayNode dayListToJson(List<Day> list) {
        return mapper.valueToTree(list);
    }

}
