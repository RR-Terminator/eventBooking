package com.eventmanagement.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.eventmanagement.model.Seat;
import com.eventmanagement.model.SeatType;
import com.eventmanagement.utils.SQLQueries;
import com.eventmanagement.utils.dbconfig.DataBaseConnection;

public class SeatRepository {

    private static final Connection DB = DataBaseConnection.getConnection();

    public void save(Seat seat) throws SQLException {
        try (PreparedStatement stmt = DB.prepareStatement(SQLQueries.INSERT_SEAT)) {
            stmt.setObject(1, seat.getSeat_id());
            stmt.setString(2, seat.getSeat_type().name());
            stmt.setDouble(3, seat.getSeat_price());
            stmt.setBoolean(4, seat.getIs_available());
            stmt.setObject(5, seat.getEvent_id());
            stmt.executeUpdate();
        }
    }

    public void update(Seat seat) throws SQLException {
        try (PreparedStatement stmt = DB.prepareStatement(SQLQueries.UPDATE_SEAT)) {
            stmt.setObject(5, seat.getSeat_id());
            stmt.setString(1, seat.getSeat_type().name());
            stmt.setDouble(2, seat.getSeat_price());
            stmt.setBoolean(3, seat.getIs_available());
            stmt.setObject(4, seat.getUser_id());
            stmt.executeUpdate();
        }
    }

    public List<Seat> findByEventId(UUID eventId) throws SQLException {
        List<Seat> seats = new ArrayList<>();
        try (PreparedStatement stmt = DB.prepareStatement(SQLQueries.FETCH_SEATS_BY_EVENT_ID)) {
            stmt.setObject(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Seat seat = new Seat();
                    seat.setSeat_id((UUID) rs.getObject("seat_id"));
                    seat.setSeat_type(SeatType.valueOf(rs.getString("seat_type")));
                    seat.setSeat_price(rs.getDouble("seat_price"));
                    seat.setIs_available(rs.getBoolean("is_available"));
                    seat.setEvent_id((UUID) rs.getObject("event_id"));
                    seat.setUser_id((UUID) rs.getObject("user_id"));
                    seats.add(seat);
                }
            }
        }
        return seats;
    }

    public Map<SeatType, Integer> getAvailableSeats(UUID eventId) throws SQLException {
        String query = "SELECT seat_type, COUNT(*) AS seat_count " +
                "FROM seats " +
                "WHERE event_id = ? AND is_available = true " +
                "GROUP BY seat_type";

        Map<SeatType, Integer> availableSeats = new HashMap<>();

        try (PreparedStatement stmt = DB.prepareStatement(query)) {
            stmt.setObject(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                SeatType type = SeatType.valueOf(rs.getString("seat_type"));
                int count = rs.getInt("seat_count");
                availableSeats.put(type, count);
            }
        }

        return availableSeats;
    }

    public List<Seat> getAvailableSeatsOfType(UUID event_id, SeatType seatType) {
        
        String sql = """
                SELECT * FROM seats WHERE event_id = ? AND seat_type = ? AND is_available = true;
                """;


        List<Seat> availableSeatList = new ArrayList<>();

        try (PreparedStatement stmt = DB.prepareStatement(sql)) {
            // Set the parameters for the SQL query
            stmt.setObject(1, event_id);
            stmt.setString(2, seatType.toString());

          
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                   
                    Seat seat = new Seat();

                
                    seat.setSeat_id((UUID) rs.getObject("seat_id"));
                    seat.setEvent_id((UUID) rs.getObject("event_id"));
                    seat.setSeat_type(SeatType.valueOf(rs.getString("seat_type")));
                    seat.setSeat_price(rs.getDouble("seat_price"));
                    seat.setIs_available(rs.getBoolean("is_available"));

                
                    availableSeatList.add(seat);
                }
            }
        } catch (SQLException e) {
            // Print the exception message (you can log it or rethrow it as needed)
            System.err.println("Error fetching available seats: " + e.getMessage());
        }

        return availableSeatList;
    }

}
