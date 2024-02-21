package com.days.until.server;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@CrossOrigin(origins = "*")
public class RestApiController {
    
    private final String ENDPOINT_START = "/api/";
    private final Logger logger = LoggerFactory.getLogger(RestApiController.class);
    private final DataManager data = DataManager.getInstance();
    private final JsonManager json = JsonManager.getInstance();

    @GetMapping(ENDPOINT_START + "get")
    public JsonNode getAllDays(@RequestParam("name") String name) {
        logger.debug("getAllDays called with parameters {name:" + name + "}");
        name = name.toLowerCase();
        try {
            return data.getDaysJson(name);
        } catch (IllegalArgumentException | JsonProcessingException | SQLException e) {
            logger.error("An error occurred while fetching day data.", e);
            return json.getFailureReturn();
        }
    }

    @PostMapping(ENDPOINT_START + "add")
    public ObjectNode addDay(@RequestBody ObjectNode node) {
        logger.debug("addDay called with request body: " +  node.toString());

        String username = node.get("username").asText().toLowerCase(); 
        String dayName = node.get("dayName").asText();
        long date = node.get("date").asLong();

        try {
            data.insertDay(username, dayName, date);
        } catch (SQLException e) {
            logger.error("An error occurred while inserting new days in to database", e);
            return json.getFailureReturn();
        }

        return json.getSuccessReturn();
    }

    @PostMapping(ENDPOINT_START + "delete")
    public ObjectNode deleteDay(@RequestBody ObjectNode node) {
        logger.debug("deleteDay called with request body: " + node.toString());

        String username = node.get("username").asText().toLowerCase();
        String dayName = node.get("dayName").asText();

        try {
            data.deleteDay(username, dayName);
        } catch (SQLException e) {
            logger.error("An error occurred while deleting a day from the database", e);
            return json.getFailureReturn();
        }

        return json.getSuccessReturn();
    }
}
