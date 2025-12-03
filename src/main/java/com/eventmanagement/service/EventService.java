package com.eventmanagement.service;


import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import com.eventmanagement.model.Event;
import com.eventmanagement.model.Seat;
import com.eventmanagement.model.SeatType;
import com.eventmanagement.repository.EventRepository;
import com.eventmanagement.repository.SeatRepository;

public class EventService {

    private final EventRepository eventRepo = new EventRepository();
    private final SeatRepository seatRepo = new SeatRepository();

    /**
     * Creates an event and automatically inserts seats of all types.
     */
    public UUID createEvent(Event event) throws SQLException {
        UUID eventId = eventRepo.save(event);

        // Insert seats
        insertSeats(eventId, event.getNumber_of_bronze_seats(), SeatType.BRONZE);
        insertSeats(eventId, event.getNumber_of_silver_seats(), SeatType.SILVER);
        insertSeats(eventId, event.getNumber_of_gold_seats(), SeatType.GOLD);
        insertSeats(eventId, event.getNumber_of_platinum_seats(), SeatType.PLATINUM);

        return eventId;
    }

    private void insertSeats(UUID eventId, int count, SeatType type) throws SQLException {
        for (int i = 0; i < count; i++) {
            Seat seat = new Seat(type, type.getValue(), eventId);
            seatRepo.save(seat);
        }
    }

    public List<Event> getAllEvents() throws SQLException {
        return eventRepo.findAll();
    }

    public Event getEventById(UUID eventId) throws SQLException {
        return eventRepo.findById(eventId);
    }

    public boolean cancelEvent(UUID eventId) throws SQLException {
        return eventRepo.cancelEvent(eventId);
    }
}

