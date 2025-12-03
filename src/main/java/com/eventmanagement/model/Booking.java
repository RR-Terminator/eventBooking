package com.eventmanagement.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Booking {

    private UUID booking_id = UUID.randomUUID(); // Unique booking ID
    private UUID user_id; // FK to user
    private UUID event_id; // FK to event
    private BookingStatus booking_status = BookingStatus.CONFIRMED; // Default status
    private Date booking_date = new Date(); // Default: now
    private List<Seat> bookedSeats = new ArrayList<>();

    public Booking(UUID user_id, UUID event_id, List<Seat> bookedSeats) {
        this.user_id = user_id;
        this.event_id = event_id;
        this.bookedSeats = bookedSeats;
    }
}
