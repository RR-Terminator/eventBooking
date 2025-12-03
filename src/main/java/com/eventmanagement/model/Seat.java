package com.eventmanagement.model;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class Seat {

    private UUID seat_id = UUID.randomUUID();

    @NonNull
    private SeatType seat_type;

    @NonNull
    private Double seat_price;

    private Boolean is_available = true;

    @NonNull
    private UUID event_id;

    private UUID user_id;

    public Seat(SeatType type, Double price, UUID eventId) {
        this.seat_type = type;
        this.seat_price = price;
        this.event_id = eventId;
        this.seat_id = UUID.randomUUID();
    }
}
