package com.eventmanagement.utils;

public class SQLQueries {

    // ===== USERS =====
    public static final String CREATE_USER = """
                INSERT INTO users ( user_name, user_password)
                VALUES ( ?, ?);
            """;

    public static final String DELETE_USER = """
                DELETE FROM users
                WHERE user_id = ?;
            """;

    public static final String UPDATE_USER = """
                UPDATE users
                SET user_name = ?, user_password = ?
                WHERE user_id = ?;
            """;

    public static final String FETCH_ALL_USERS = """
                SELECT * FROM users;
            """;

    public static final String FETCH_USER_BY_ID = """
                SELECT * FROM users WHERE user_id = ?;
            """;

    // ===== EVENTS =====
    public static final String CREATE_EVENT = """
                INSERT INTO events ( event_name, event_description, event_date, event_venue, event_isActive)
                VALUES ( ?, ?, ?, ?, ?);
            """;

    public static final String FETCH_ALL_EVENTS = """
                SELECT * FROM events WHERE event_isActive = true;
            """;

    public static final String FETCH_EVENT_BY_ID = """
                SELECT * FROM events WHERE event_id = ?;
            """;

    public static final String CANCEL_EVENT = """
                UPDATE events
                SET event_isActive = false
                WHERE event_id = ?;
            """;

    // ===== SEATS =====
    public static final String INSERT_SEAT = """
                INSERT INTO seats (seat_id, seat_type, seat_price, is_available, event_id)
                VALUES (?, ?, ?, ?, ?);
            """;

    public static final String FETCH_SEATS_BY_EVENT_ID = """
                SELECT * FROM seats WHERE event_id = ?;
            """;

    public static final String UPDATE_SEAT = """
                UPDATE seats
                SET seat_type = ?, seat_price = ?, is_available = ?, user_id = ?
                WHERE seat_id = ?;
            """;

    // ===== BOOKINGS =====
    public static final String CREATE_BOOKING = """
                INSERT INTO bookings (booking_id, user_id, event_id, booking_status, booking_date)
                VALUES (?, ?, ?, ?, ?);
            """;

    public static final String FETCH_BOOKINGS_BY_USER = """
                SELECT * FROM bookings WHERE user_id = ?;
            """;

    public static final String FETCH_BOOKINGS_BY_EVENT = """
                SELECT * FROM bookings WHERE event_id = ?;
            """;

    public static final String UPDATE_BOOKING_STATUS = """
                UPDATE bookings
                SET booking_status = ?
                WHERE booking_id = ?;
            """;
}
