package com.eventmanagement.repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.eventmanagement.model.Event;
import com.eventmanagement.model.SeatType;

public class EventRepository {

    private final java.sql.Connection connection;

    public EventRepository(java.sql.Connection connection) {
        this.connection = connection;
    }

    public Event findById(String event_id) {
        PreparedStatement ps = null;
        ResultSet rs = null;
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

                java.sql.Date sqlDate = rs.getDate("event_date");
                if (sqlDate != null) {
                    e.setEvent_date(sqlDate.toLocalDate());
                } else {
                    e.setEvent_date(null);
                }

                e.setEvent_venue(rs.getString("event_venue"));
                e.setEvent_isActive(rs.getBoolean("event_isActive"));
                e.setCreated_at(rs.getString("created_at"));
                e.setUpdated_at(rs.getString("updated_at"));
                return e;
            }
            return null;
        } catch (SQLException ex) {
            throw new RuntimeException(
                    "Error fetching event: SQLState=" + ex.getSQLState() + " Message=" + ex.getMessage(), ex);
        } finally {
            close(rs);
            close(ps);
        }
    }

    // TODO Fix this issue to remove the booking from user side as soon as event is
    // canceled by the admin

    public boolean deactivateEvent(String event_id) {
        PreparedStatement ps = null;
        try {
            String sql = "UPDATE events SET event_isActive = false, updated_at = NOW() WHERE event_id = ?";
            ps = connection.prepareStatement(sql);
            ps.setObject(1, java.util.UUID.fromString(event_id));
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException(
                    "Error deactivating event: SQLState=" + ex.getSQLState() + " Message=" + ex.getMessage(), ex);
        } finally {
            close(ps);
        }
    }

    /**
     * Fetch all ACTIVE events.
     */
    public List<Event> findAllActiveEvents() {
        PreparedStatement ps = null;
        ResultSet rs = null;
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

                java.sql.Date sqlDate = rs.getDate("event_date");
                if (sqlDate != null) {
                    e.setEvent_date(sqlDate.toLocalDate());
                } else {
                    e.setEvent_date(null);
                }

                e.setEvent_venue(rs.getString("event_venue"));
                e.setEvent_isActive(rs.getBoolean("event_isActive"));
                e.setCreated_at(rs.getString("created_at"));
                e.setUpdated_at(rs.getString("updated_at"));
                list.add(e);
            }
            return list;
        } catch (SQLException ex) {
            throw new RuntimeException(
                    "Error fetching all events: SQLState=" + ex.getSQLState() + " Message=" + ex.getMessage(), ex);
        } finally {
            close(rs);
            close(ps);
        }
    }

    /**
     * Inserts a new event and returns generated event_id.
     * Expects Event.event_date to be java.time.LocalDate (non-null).
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

            if (event.getEvent_date() == null) {
                throw new IllegalArgumentException("event_date cannot be null");
            }
            // convert LocalDate -> java.sql.Date for DATE column
            ps.setDate(3, Date.valueOf(event.getEvent_date()));

            ps.setString(4, event.getEvent_venue());

            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new RuntimeException("Event creation failed, no ID returned.");
            }

            String eventId = rs.getObject("event_id").toString();

            // ---------------------------
            // Create seats for this event
            // ---------------------------
            PreparedStatement seatPs = null;
            try {
                String seatSql = "INSERT INTO seats (seat_id, seat_type, seat_price, is_available, event_id) "
                        + "VALUES (?, ?, ?, true, ?)";
                seatPs = connection.prepareStatement(seatSql);

                generateSeats(event.getNumber_of_bronze_seats(), SeatType.BRONZE, seatPs, eventId);
                generateSeats(event.getNumber_of_silver_seats(), SeatType.SILVER, seatPs, eventId);
                generateSeats(event.getNumber_of_gold_seats(), SeatType.GOLD, seatPs, eventId);
                generateSeats(event.getNumber_of_platinum_seats(), SeatType.PLATINUM, seatPs, eventId);

                seatPs.executeBatch();

            } catch (Exception ex) {
                // best-effort cleanup: delete the event if seats couldn't be created
                try {
                    PreparedStatement cleanup = connection.prepareStatement("DELETE FROM events WHERE event_id = ?");
                    cleanup.setObject(1, java.util.UUID.fromString(eventId));
                    cleanup.executeUpdate();
                    try {
                        if (cleanup != null)
                            cleanup.close();
                    } catch (Exception ignored) {
                    }
                } catch (Exception cleanupEx) {
                    // ignore cleanup failure but keep original exception
                }
                throw new RuntimeException("Failed to create seats for event: " + ex.getMessage(), ex);
            } finally {
                try {
                    if (seatPs != null)
                        seatPs.close();
                } catch (Exception ignored) {
                }
            }

            return eventId;

        } catch (SQLException ex) {
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

    public void generateSeats(int seatCount, SeatType seatType, PreparedStatement ps, String eventId) {
        try {
            for (int i = 0; i < seatCount; i++) {
                ps.setObject(1, java.util.UUID.randomUUID());
                ps.setString(2, seatType.name());
                ps.setDouble(3, seatType.getValue());
                ps.setObject(4, java.util.UUID.fromString(eventId));
                ps.addBatch();
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to generate seats for type: "
                    + seatType.name() + " | " + ex.getMessage(), ex);
        }
    }

}



// TODO create a method to cancel booking by the user and not by admin update
// avalable seats