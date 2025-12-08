package com.eventmanagement.repository;

import java.util.ArrayList;
import java.util.List;

import com.eventmanagement.model.Seat;

public class SeatRepository {

    private final java.sql.Connection connection;

    public SeatRepository(java.sql.Connection connection) {
        this.connection = connection;
    }

    public void createSeatsForEvent(String event_id,
            int bronzeCount, int silverCount,
            int goldCount, int platinumCount) {

        java.sql.PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO seats (seat_id, seat_type, seat_price, is_available, event_id) " +
                    "VALUES (?, ?, ?, true, ?)";

            ps = connection.prepareStatement(sql);

            // bronze seats
            for (int i = 0; i < bronzeCount; i++) {
                java.util.UUID seatUuid = java.util.UUID.randomUUID();
                ps.setObject(1, seatUuid);
                ps.setString(2, com.eventmanagement.model.SeatType.BRONZE.name());
                ps.setDouble(3, com.eventmanagement.model.SeatType.BRONZE.getValue());
                ps.setObject(4, java.util.UUID.fromString(event_id));
                ps.addBatch();
            }

            // silver seats
            for (int i = 0; i < silverCount; i++) {
                java.util.UUID seatUuid = java.util.UUID.randomUUID();
                ps.setObject(1, seatUuid);
                ps.setString(2, com.eventmanagement.model.SeatType.SILVER.name());
                ps.setDouble(3, com.eventmanagement.model.SeatType.SILVER.getValue());
                ps.setObject(4, java.util.UUID.fromString(event_id));
                ps.addBatch();
            }

            // gold seats
            for (int i = 0; i < goldCount; i++) {
                java.util.UUID seatUuid = java.util.UUID.randomUUID();
                ps.setObject(1, seatUuid);
                ps.setString(2, com.eventmanagement.model.SeatType.GOLD.name());
                ps.setDouble(3, com.eventmanagement.model.SeatType.GOLD.getValue());
                ps.setObject(4, java.util.UUID.fromString(event_id));
                ps.addBatch();
            }

            // platinum seats
            for (int i = 0; i < platinumCount; i++) {
                java.util.UUID seatUuid = java.util.UUID.randomUUID();
                ps.setObject(1, seatUuid);
                ps.setString(2, com.eventmanagement.model.SeatType.PLATINUM.name());
                ps.setDouble(3, com.eventmanagement.model.SeatType.PLATINUM.getValue());
                ps.setObject(4, java.util.UUID.fromString(event_id));
                ps.addBatch();
            }

            ps.executeBatch();

        } catch (java.sql.SQLException ex) {
            throw new RuntimeException("Error creating seats for event: " + ex.getMessage(), ex);

        } finally {
            try {
                if (ps != null)
                    ps.close();
            } catch (Exception ignored) {
            }
        }
    }

    public List<Seat> findAvailableSeatsByEventAndType(String event_id, String seat_type) {
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        List<Seat> seats = new ArrayList<>();
        try {
            String sql = "SELECT seat_id, seat_type, seat_price, is_available, event_id FROM seats "
                    + "WHERE event_id = ? AND seat_type = ? AND is_available = true";
            ps = connection.prepareStatement(sql);
            ps.setObject(1, java.util.UUID.fromString(event_id));
            ps.setString(2, seat_type);
            rs = ps.executeQuery();
            while (rs.next()) {
                Seat s = new Seat();
                s.setSeat_id(rs.getObject("seat_id").toString());
                s.setSeat_type(rs.getString("seat_type"));
                s.setSeat_price(rs.getDouble("seat_price"));
                s.setIs_available(rs.getBoolean("is_available"));
                s.setEvent_id(rs.getObject("event_id").toString());
                seats.add(s);
            }
            return seats;
        } catch (java.sql.SQLException ex) {
            throw new RuntimeException("Error finding available seats", ex);
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
        }
    }

    public List<Seat> getSeatsByIdsForUpdate(List<String> seatIds) {
        if (seatIds == null || seatIds.isEmpty())
            return new ArrayList<>();
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        List<Seat> seats = new ArrayList<>();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT seat_id, seat_type, seat_price, is_available, event_id FROM seats WHERE seat_id IN (");
            for (int i = 0; i < seatIds.size(); i++) {
                sb.append("?");
                if (i < seatIds.size() - 1)
                    sb.append(",");
            }
            sb.append(") FOR UPDATE");
            ps = connection.prepareStatement(sb.toString());
            for (int i = 0; i < seatIds.size(); i++) {
                ps.setObject(i + 1, java.util.UUID.fromString(seatIds.get(i)));
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                Seat s = new Seat();
                s.setSeat_id(rs.getObject("seat_id").toString());
                s.setSeat_type(rs.getString("seat_type"));
                s.setSeat_price(rs.getDouble("seat_price"));
                s.setIs_available(rs.getBoolean("is_available"));
                s.setEvent_id(rs.getObject("event_id").toString());
                seats.add(s);
            }
            return seats;
        } catch (java.sql.SQLException ex) {
            throw new RuntimeException("Error locking seats for update", ex);
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
        }
    }

    /**
     * Update is_available flag for a list of seats (single batch update).
     */
    public void updateSeatAvailability(List<String> seatIds, boolean isAvailable) {
        if (seatIds == null || seatIds.isEmpty())
            return;
        java.sql.PreparedStatement ps = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("UPDATE seats SET is_available = ? WHERE seat_id IN (");
            for (int i = 0; i < seatIds.size(); i++) {
                sb.append("?");
                if (i < seatIds.size() - 1)
                    sb.append(",");
            }
            sb.append(")");
            ps = connection.prepareStatement(sb.toString());
            ps.setBoolean(1, isAvailable);
            for (int i = 0; i < seatIds.size(); i++) {
                ps.setObject(2 + i, java.util.UUID.fromString(seatIds.get(i)));
            }
            ps.executeUpdate();
        } catch (java.sql.SQLException ex) {
            throw new RuntimeException("Error updating seat availability", ex);
        } finally {
            closeQuietly(ps);
        }
    }

    private void closeQuietly(java.sql.ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception ignored) {
            }
        }
    }

    private void closeQuietly(java.sql.Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (Exception ignored) {
            }
        }
    }
}

