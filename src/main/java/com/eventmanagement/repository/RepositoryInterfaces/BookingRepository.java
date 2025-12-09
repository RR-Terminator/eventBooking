package com.eventmanagement.repository.RepositoryInterfaces;

import java.util.List;

import com.eventmanagement.model.Booking;

public interface BookingRepository extends CrudRepository<Booking, String> {
    List<Booking> findByUser(String userId);

    void addSeats(String bookingId, List<String> seatIds);
}
