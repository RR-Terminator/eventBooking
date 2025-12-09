package com.event_booking.repository;

import java.util.List;

import com.event_booking.model.Seat;
import com.event_booking.model.SeatType;

public interface SeatRepository extends BaseRepository<Seat, String> {

    List<Seat> findAvailableSeats(String eventId, SeatType type);

    List<Seat> lockSeatsForUpdate(List<String> seatIds);

    void updateSeatAvailability(List<String> seatIds, boolean available);

    void createSeatsForEvent(String eventId, int bronze, int silver, int gold, int platinum);

}
