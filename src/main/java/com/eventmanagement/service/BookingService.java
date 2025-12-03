package com.eventmanagement.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.eventmanagement.model.Booking;
import com.eventmanagement.model.BookingStatus;
import com.eventmanagement.model.Seat;
import com.eventmanagement.model.SeatType;
import com.eventmanagement.repository.BookingRepository;
import com.eventmanagement.repository.SeatRepository;

public class BookingService {

    private final BookingRepository bookingRepo = new BookingRepository();
    private final SeatRepository seatRepo = new SeatRepository();

    /**
     * Create a booking for a user for an event.
     * Only books seats that are available.
     * @return
     */
    public boolean createBooking(Booking booking) throws SQLException {
        // Check seat availability
        List<Seat> seats = booking.getBookedSeats();
        for (Seat seat : seats) {
            System.out.println("Checking");
            if (!seat.getIs_available()) {
                booking.setBooking_status(BookingStatus.FAILED);
                bookingRepo.save(booking);
                throw new IllegalStateException("One or more seats are not available.");
            }
        }

        // Mark seats as unavailable
        for (Seat seat : seats) {
            System.out.println("Marking seats unvailable");
            seat.setIs_available(false);
            seat.setUser_id(booking.getUser_id());
            seatRepo.update(seat); // Update seat availability
        }

        // Save booking
        booking.setBooking_status(BookingStatus.CONFIRMED);
        boolean result =bookingRepo.save(booking);
        return result;
    }

    public List<Booking> getBookingsByUser(UUID userId) throws SQLException {
        return bookingRepo.findByUserId(userId);
    }

    public  Map<SeatType, Integer> numberOfSeatsAvailble(UUID event_id) throws SQLException{
        return seatRepo.getAvailableSeats(event_id);
    }

    public List<Seat> avaliableSeatlist(UUID event_id , SeatType seatType){
        return seatRepo.getAvailableSeatsOfType(event_id , seatType);
    }
}
