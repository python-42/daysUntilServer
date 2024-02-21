package com.days.until.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class DataManager {
    
    private static DataManager INSTANCE = null;

    private final Logger logger = LoggerFactory.getLogger(DataManager.class);
    private final JsonManager json = JsonManager.getInstance();

    private Connection conn;
    private final String DATABASE_PATH = "jdbc:sqlite:test.db"; //TODO replace

    private final String SELECT_QUERY = "SELECT * FROM days WHERE username = ?;";

    private final String INSERT_QUERY  =
    """
    INSERT INTO days 
    (username, dayName, day) 
    VALUES (?, ?, ?);
    """;

    private final String DELETE_QUERY = "DELETE FROM days WHERE username = ? AND dayName = ?;";

    private final String CREATE_DB_QUERY = 
    """
        CREATE TABLE IF NOT EXISTS days (
            username TEXT NOT NULL,
            dayName TEXT NOT NULL,
            day DATETIME NOT NULL
        );
    """;

    private DataManager() {
        try {
            conn = DriverManager.getConnection(DATABASE_PATH);
            conn.createStatement().execute(CREATE_DB_QUERY);
        } catch (SQLException e) {
            logger.error("SQL Exception occurred while trying to connect to database.", e);
        }
        logger.info("Database connection successful. Database has been created if it did not exist before.");
    }

    public static DataManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataManager();
        }
        return INSTANCE;
    }


    public ArrayNode getDaysJson(String name) throws JsonProcessingException, IllegalArgumentException, SQLException {
        name = StringEscapeUtils.escapeHtml4(name);

        PreparedStatement stmt =  conn.prepareStatement(SELECT_QUERY);
        stmt.setString(1, name);

        ResultSet set = stmt.executeQuery();
        List<Day> arr = new ArrayList<Day>();
        
        while(set.next()) {
            arr.add(new Day(set.getString("dayName"), set.getLong("day")));
        }
        
        ArrayNode rtn = json.dayListToJson(arr);
        
        logger.debug("Days JSON generated: " + rtn.toString());

        return rtn;
    }

    public void insertDay(String name, String dayName, long time) throws SQLException {
        name = StringEscapeUtils.escapeHtml4(name);
        dayName = StringEscapeUtils.escapeHtml4(dayName);
        PreparedStatement stmt = conn.prepareStatement(INSERT_QUERY);
        stmt.setString(1, name);
        stmt.setString(2, dayName);
        stmt.setTimestamp(3, Timestamp.from(Instant.ofEpochMilli(time)));
        stmt.execute();
    }

    public void deleteDay(String name, String dayName) throws SQLException {
        name = StringEscapeUtils.escapeHtml4(name);
        dayName = StringEscapeUtils.escapeHtml4(dayName);
        PreparedStatement stmt = conn.prepareStatement(DELETE_QUERY);
        stmt.setString(1, name);
        stmt.setString(2, dayName);

        stmt.execute();
    }

}
