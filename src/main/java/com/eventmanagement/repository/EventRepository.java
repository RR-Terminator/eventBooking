package com.eventmanagement.repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.eventmanagement.model.Event;

public class EventRepository {

    private final java.sql.Connection connection;

    public EventRepository(java.sql.Connection connection) {
        this.connection = connection;
    }

    public Event findById(String event_id) {
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        try {
            String sql = "SELECT event_id, event_name, event_description, event_date, event_venue, "
                    + "event_isActive, created_at, updated_at "
                    + "FROM events WHERE event_id = ?";
            ps = connection.prepareStatement(sql);
            ps.setObject(1, java.util.UUID.fromString(event_id));
            rs = ps.executeQuery();
            if (rs.next()) {
                Event e = new Event();
                e.setEvent_id(rs.getObject("event_id").toString());
                e.setEvent_name(rs.getString("event_name"));
                e.setEvent_description(rs.getString("event_description"));
                e.setEvent_date(rs.getString("event_date"));
                e.setEvent_venue(rs.getString("event_venue"));
                e.setEvent_isActive(rs.getBoolean("event_isActive"));
                e.setCreated_at(rs.getString("created_at"));
                e.setUpdated_at(rs.getString("updated_at"));
                return e;
            }
            return null;
        } catch (Exception ex) {
            throw new RuntimeException("Error fetching event", ex);
        } finally {
            close(rs);
            close(ps);
        }
    }

    public boolean deactivateEvent(String event_id) {
        java.sql.PreparedStatement ps = null;
        try {
            String sql = "UPDATE events SET event_isActive = false, updated_at = NOW() WHERE event_id = ?";
            ps = connection.prepareStatement(sql);
            ps.setObject(1, java.util.UUID.fromString(event_id));
            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            throw new RuntimeException("Error deactivating event", ex);
        } finally {
            close(ps);
        }
    }

    /**
     * Fetch all ACTIVE events.
     */
    public List<Event> findAllActiveEvents() {
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        List<Event> list = new ArrayList<>();
        try {
            String sql = "SELECT event_id, event_name, event_description, event_date, event_venue, "
                    + "event_isActive, created_at, updated_at "
                    + "FROM events WHERE event_isActive = true ORDER BY created_at DESC";
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Event e = new Event();
                e.setEvent_id(rs.getObject("event_id").toString());
                e.setEvent_name(rs.getString("event_name"));
                e.setEvent_description(rs.getString("event_description"));
                e.setEvent_date(rs.getString("event_date"));
                e.setEvent_venue(rs.getString("event_venue"));
                e.setEvent_isActive(rs.getBoolean("event_isActive"));
                e.setCreated_at(rs.getString("created_at"));
                e.setUpdated_at(rs.getString("updated_at"));
                list.add(e);
            }
            return list;
        } catch (Exception ex) {
            throw new RuntimeException("Error fetching all events", ex);
        } finally {
            close(rs);
            close(ps);
        }
    }

    /**
     * Inserts a new event and returns generated event_id.
     */
    public String createEvent(Event event) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "INSERT INTO events (event_name, event_description, event_date, event_venue, event_isActive) "
                    + "VALUES (?, ?, ?, ?, true) RETURNING event_id";
            ps = connection.prepareStatement(sql);

            ps.setString(1, Objects.toString(event.getEvent_name(), null));
            ps.setString(2, event.getEvent_description());

            // handle event_date robustly
            Object dateObj = event.getEvent_date(); // could be String or LocalDate depending on your model
            if (dateObj == null) {
                throw new IllegalArgumentException("event_date cannot be null");
            }

            if (dateObj instanceof java.time.LocalDate) {
                LocalDate ld = (LocalDate) dateObj;
                ps.setDate(3, Date.valueOf(ld));
            } else {
                // assume string in ISO format yyyy-MM-dd
                String dateStr = dateObj.toString();
                // validate format
                LocalDate ld = LocalDate.parse(dateStr); // will throw if not yyyy-MM-dd
                ps.setDate(3, Date.valueOf(ld));
            }

            ps.setString(4, event.getEvent_venue());

            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getObject("event_id").toString();
            }
            throw new RuntimeException("Event creation failed, no ID returned.");

        } catch (SQLException ex) {
            // include SQLState and message to make debugging easy
            throw new RuntimeException(
                    "Error creating event: SQLState=" + ex.getSQLState() + " Message=" + ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new RuntimeException("Error creating event: " + ex.getMessage(), ex);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ignored) {
            }
            try {
                if (ps != null)
                    ps.close();
            } catch (Exception ignored) {
            }
        }
    }

    private void close(java.sql.ResultSet r) {
        try {
            if (r != null)
                r.close();
        } catch (Exception ignored) {
        }
    }

    private void close(java.sql.Statement st) {
        try {
            if (st != null)
                st.close();
        } catch (Exception ignored) {
        }
    }
}
