package com.eventmanagement.repository.RepositoryInterfaces;

import java.util.List;

import com.eventmanagement.model.Seat;
import com.eventmanagement.model.SeatType;

public interface SeatRepository extends CrudRepository<Seat, String> {

    List<Seat> findAvailableSeats(String eventId, SeatType type);

    List<Seat> lockSeatsForUpdate(List<String> seatIds); 

    void updateSeatAvailability(List<String> seatIds, boolean available);

    void createSeatsForEvent(String eventId, int bronze, int silver, int gold, int platinum);
}