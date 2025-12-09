package com.eventmanagement.repository;

import java.util.ArrayList;
import java.util.List;

import com.eventmanagement.model.Booking;

public class BookingRepositoryImpl {

    private final java.sql.Connection connection;

    public BookingRepositoryImpl(java.sql.Connection connection) {
        this.connection = connection;
    }

    /**
     * Create a booking row. Returns generated booking_id (from RETURNING).
     */
    public String createBooking(String user_id, String event_id, String booking_status) {
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        try {
            String sql = "INSERT INTO bookings (user_id, event_id, booking_status) VALUES (?, ?, ?) RETURNING booking_id";
            ps = connection.prepareStatement(sql);
            ps.setObject(1, java.util.UUID.fromString(user_id));
            ps.setObject(2, java.util.UUID.fromString(event_id));
            ps.setString(3, booking_status);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getObject("booking_id").toString();
            } else {
                throw new RuntimeException("Failed to obtain booking id after insert");
            }
        } catch (java.sql.SQLException ex) {
            throw new RuntimeException("Error creating booking", ex);
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
        }
    }

    /**
     * Insert booking_seats entries in batch.
     */
    public void insertBookingSeats(String booking_id, List<String> seatIds) {
        if (seatIds == null || seatIds.isEmpty()) return;
        java.sql.PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO booking_seats (booking_id, seat_id) VALUES (?, ?)";
            ps = connection.prepareStatement(sql);
            for (String seatId : seatIds) {
                ps.setObject(1, java.util.UUID.fromString(booking_id));
                ps.setObject(2, java.util.UUID.fromString(seatId));
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (java.sql.SQLException ex) {
            throw new RuntimeException("Error inserting booking seats", ex);
        } finally {
            closeQuietly(ps);
        }
    }

    /**
     * Fetch bookings by user
     */
    public List<Booking> findBookingsByUser(String user_id) {
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        List<Booking> bookings = new ArrayList<>();
        try {
            String sql = "SELECT booking_id, user_id, event_id, booking_status, booking_date FROM bookings WHERE user_id = ?";
            ps = connection.prepareStatement(sql);
            ps.setObject(1, java.util.UUID.fromString(user_id));
            rs = ps.executeQuery();
            while (rs.next()) {
                Booking b = new Booking();
                b.setBooking_id(rs.getObject("booking_id").toString());
                b.setUser_id(rs.getObject("user_id").toString());
                b.setEvent_id(rs.getObject("event_id").toString());
                b.setBooking_status(rs.getString("booking_status"));
                b.setBooking_date(rs.getString("booking_date"));
                bookings.add(b);
            }
            return bookings;
        } catch (java.sql.SQLException ex) {
            throw new RuntimeException("Error finding bookings by user", ex);
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
        }
    }

    private void closeQuietly(java.sql.ResultSet rs) {
        if (rs != null) {
            try { rs.close(); } catch (Exception ignored) {}
        }
    }

    private void closeQuietly(java.sql.Statement st) {
        if (st != null) {
            try { st.close(); } catch (Exception ignored) {}
        }
    }
}
