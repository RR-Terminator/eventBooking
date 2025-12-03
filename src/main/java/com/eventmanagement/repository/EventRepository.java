package com.eventmanagement.repository;


import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.eventmanagement.model.Event;
import com.eventmanagement.utils.SQLQueries;
import com.eventmanagement.utils.dbconfig.DataBaseConnection;

public class EventRepository {

    private static final Connection DB = DataBaseConnection.getConnection();

    public UUID save(Event event) throws SQLException {
        String sql = SQLQueries.CREATE_EVENT;

        try (PreparedStatement stmt = DB.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, event.getEvent_name());
            stmt.setString(2, event.getEvent_description());
            stmt.setDate(3, Date.valueOf(event.getEvent_date()));
            stmt.setString(4, event.getEvent_venue());
            stmt.setBoolean(5, event.isEvent_isActive());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) throw new SQLException("Creating event failed");

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    UUID id = generatedKeys.getObject(1, UUID.class);
                    event.setEvent_id(id);
                    return id;
                } else {
                    throw new SQLException("No event ID obtained.");
                }
            }
        }
    }

    public List<Event> findAll() throws SQLException {
        List<Event> events = new ArrayList<>();
        try (Statement stmt = DB.createStatement();
             ResultSet rs = stmt.executeQuery(SQLQueries.FETCH_ALL_EVENTS)) {
            while (rs.next()) {
                Event event = new Event(
                        rs.getString("event_name"),
                        rs.getString("event_description"),
                        rs.getDate("event_date").toLocalDate(),
                        rs.getString("event_venue"),
                        0, 0, 0, 0
                );
                event.setEvent_id((UUID) rs.getObject("event_id"));
                event.setEvent_isActive(rs.getBoolean("event_isActive"));
                events.add(event);
            }
        }
        return events;
    }

    public Event findById(UUID id) throws SQLException {
        try (PreparedStatement stmt = DB.prepareStatement(SQLQueries.FETCH_EVENT_BY_ID)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Event event = new Event(
                            rs.getString("event_name"),
                            rs.getString("event_description"),
                            rs.getDate("event_date").toLocalDate(),
                            rs.getString("event_venue"),
                            0, 0, 0, 0
                    );
                    event.setEvent_id((UUID) rs.getObject("event_id"));
                    event.setEvent_isActive(rs.getBoolean("event_isActive"));
                    return event;
                }
            }
        }
        return null;
    }

    public boolean cancelEvent(UUID id) throws SQLException {
        try (PreparedStatement stmt = DB.prepareStatement(SQLQueries.CANCEL_EVENT)) {
            stmt.setObject(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}
