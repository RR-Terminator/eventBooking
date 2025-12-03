package com.eventmanagement.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.eventmanagement.model.Booking;
import com.eventmanagement.utils.dbconfig.DataBaseConnection;

public class BookingRepository {

    private static final Connection DB = DataBaseConnection.getConnection();

    public boolean save(Booking booking) throws SQLException {
        String sql = "INSERT INTO bookings (booking_id, user_id, event_id, booking_status, booking_date) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = DB.prepareStatement(sql)) {
            stmt.setObject(1, booking.getBooking_id());
            stmt.setObject(2, booking.getUser_id());
            stmt.setObject(3, booking.getEvent_id());
            stmt.setString(4, booking.getBooking_status().name());
            stmt.setTimestamp(5, new Timestamp(booking.getBooking_date().getTime()));
            return stmt.executeUpdate() > 0 ? true : false;
        }
    }

    public List<Booking> findByUserId(UUID userId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE user_id = ?";
        try (PreparedStatement stmt = DB.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = new Booking();
                    booking.setBooking_id((UUID) rs.getObject("booking_id"));
                    booking.setUser_id((UUID) rs.getObject("user_id"));
                    booking.setEvent_id((UUID) rs.getObject("event_id"));
                    booking.setBooking_status(Enum.valueOf(com.eventmanagement.model.BookingStatus.class,
                            rs.getString("booking_status")));
                    booking.setBooking_date(rs.getTimestamp("booking_date"));
                    bookings.add(booking);
                }
            }
        }
        return bookings;
    }
}
