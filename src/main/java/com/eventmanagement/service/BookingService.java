package com.eventmanagement.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eventmanagement.Exceptions.BookingFailedException;
import com.eventmanagement.Exceptions.InvalidEventIdException;
import com.eventmanagement.Exceptions.InvalidSeatIDException;
import com.eventmanagement.Exceptions.InvalidUserIdException;
import com.eventmanagement.Exceptions.SeatAlreadyBookedException;
import com.eventmanagement.model.Booking;
import com.eventmanagement.model.Seat;
import com.eventmanagement.model.SeatType;
import com.eventmanagement.repository.BookingRepository;
import com.eventmanagement.repository.EventRepository;
import com.eventmanagement.repository.SeatRepository;
import com.eventmanagement.repository.UserRepository;

public class BookingService {

    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public BookingService(java.sql.Connection connection) {
        this.bookingRepository = new BookingRepository(connection);
        this.seatRepository = new SeatRepository(connection);
        this.userRepository = new UserRepository(connection);
        this.eventRepository = new EventRepository(connection);
    }

    /**
     * Main booking flow.
     *
     * @param connection The JDBC connection to use (should be same across
     *                   repositories)
     * @param user_id    User making the booking
     * @param event_id   Event id
     * @param seatIds    List of desired seat ids
     * @return booking_id if successful
     * @throws InvalidUserIdException     if user not found
     * @throws InvalidEventIdException    if event not found
     * @throws InvalidSeatIdException     if any seat does not exist or does not
     *                                    belong to event
     * @throws SeatAlreadyBookedException if any seat already booked
     * @throws BookingFailedException     on other failures
     */
    public String createBooking(java.sql.Connection connection, String user_id, String event_id, List<String> seatIds) {

        // Validate input
        if (seatIds == null || seatIds.isEmpty()) {
            throw new InvalidSeatIDException("No seats provided for booking");
        }

        try {
            // 1. validate user
            UsersValidation(user_id);

            // 2. validate event
            EventValidation(event_id);

            // 3. Begin transaction on provided connection
            connection.setAutoCommit(false);

            // 4. Lock requested seat rows FOR UPDATE and fetch details
            List<Seat> lockedSeats = seatRepository.getSeatsByIdsForUpdate(seatIds);

            // Check that we got all seats back
            if (lockedSeats.size() != seatIds.size()) {
                connection.rollback();
                throw new InvalidSeatIDException("One or more seat ids are invalid or do not exist");
            }

            // Map seat_id -> Seat
            Map<String, Seat> seatMap = new HashMap<>();
            for (Seat s : lockedSeats)
                seatMap.put(s.getSeat_id(), s);

            // Ensure all seats belong to the event
            for (String seatId : seatIds) {
                Seat s = seatMap.get(seatId);
                if (s == null) {
                    connection.rollback();
                    throw new InvalidSeatIDException("Seat not found: " + seatId);
                }
                if (!event_id.equals(s.getEvent_id())) {
                    connection.rollback();
                    throw new InvalidSeatIDException("Seat " + seatId + " does not belong to event " + event_id);
                }
                if (!s.isIs_available()) {
                    connection.rollback();
                    throw new SeatAlreadyBookedException("Seat already booked: " + seatId);
                }
            }

            // 5. Create booking row
            String booking_status = "CONFIRMED";
            String booking_id = bookingRepository.createBooking(user_id, event_id, booking_status);

            // 6. Insert booking_seats records
            bookingRepository.insertBookingSeats(booking_id, seatIds);

            // 7. Update seats as not available
            seatRepository.updateSeatAvailability(seatIds, false);

            // 8. Commit
            connection.commit();

            return booking_id;

        } catch (SeatAlreadyBookedException | InvalidSeatIDException | InvalidUserIdException
                | InvalidEventIdException ex) {
            try {
                connection.rollback();
            } catch (Exception ignored) {
            }
            throw ex;
        } catch (Exception ex) {
            try {
                connection.rollback();
            } catch (Exception ignored) {
            }
            throw new BookingFailedException("Booking failed: " + ex.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (Exception ignored) {
            }
        }
    }

    private void UsersValidation(String user_id) {
        com.eventmanagement.model.Users u = userRepository.findById(user_id);
        if (u == null)
            throw new InvalidUserIdException("User not found: " + user_id);
    }

    private void EventValidation(String event_id) {
        com.eventmanagement.model.Event e = eventRepository.findById(event_id);
        if (e == null)
            throw new InvalidEventIdException("Event not found: " + event_id);
        if (!e.isEvent_isActive())
            throw new InvalidEventIdException("Event is not active: " + event_id);
    }

    /**
     * Returns a map of SeatType -> count of available seats for the given event.
     */
    public Map<SeatType, Integer> numberOfSeatsAvailble(String eventIdStr) {
        Map<SeatType, Integer> result = new HashMap<>();
        for (SeatType st : SeatType.values()) {
            // repository uses seat_type as string (e.g., "BRONZE", "SILVER", ...)
            List<Seat> seats = seatRepository.findAvailableSeatsByEventAndType(eventIdStr, st.name());
            result.put(st, seats == null ? 0 : seats.size());
        }
        return result;
    }

    /**
     * Returns the list of available seats for the given event and seat type.
     */
    public List<Seat> avaliableSeatlist(String eventIdStr, SeatType sel) {
        return seatRepository.findAvailableSeatsByEventAndType(eventIdStr, sel.name());
    }

    /**
     * Returns bookings for a user. This returns what BookingRepository provides.
     * If you want to populate Booking.bookedSeats for each booking, ask and I will
     * extend this method to query booking_seats and load Seat objects.
     */
    public List<Booking> getBookingsByUser(String user_id) {
        return bookingRepository.findBookingsByUser(user_id);
    }
}
