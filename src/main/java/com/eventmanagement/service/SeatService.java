package com.eventmanagement.service;

import java.util.List;

import com.eventmanagement.model.Seat;
import com.eventmanagement.repository.SeatRepository;

public class SeatService {

    private final SeatRepository seatRepository;

    public SeatService(java.sql.Connection connection) {
        this.seatRepository = new SeatRepository(connection);
    }

    public List<Seat> findAvailableSeatsByEventAndType(String event_id, String seat_type) {
        return seatRepository.findAvailableSeatsByEventAndType(event_id, seat_type);
    }

    public List<Seat> getSeatsByIdsForUpdate(List<String> seatIds) {
        return seatRepository.getSeatsByIdsForUpdate(seatIds);
    }

    public void updateSeatAvailability(List<String> seatIds, boolean available) {
        seatRepository.updateSeatAvailability(seatIds, available);
    }
}
