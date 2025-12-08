package com.eventmanagement.service;

import java.util.List;

import com.eventmanagement.Exceptions.InvalidEventIdException;
import com.eventmanagement.model.Event;
import com.eventmanagement.repository.EventRepository;

public class EventService {

    private final EventRepository eventRepository;

    public EventService(java.sql.Connection connection) {
        this.eventRepository = new EventRepository(connection);
    }

    public Event getEventById(String event_id) {
        Event e = eventRepository.findById(event_id);
        if (e == null)
            throw new InvalidEventIdException("Event not found: " + event_id);
        return e;
    }

    public boolean cancelEvent(String event_id) {
        return eventRepository.deactivateEvent(event_id);
    }

    /**
     * Returns all ACTIVE events.
     */
    public List<Event> getAllEvents() {
        return eventRepository.findAllActiveEvents();
    }

    /**
     * Creates an event and returns the generated event_id.
     */
    public String createEvent(Event event) {
        // Validation (use LocalDate checks, not String checks)
        if (event.getEvent_name() == null || event.getEvent_name().trim().isEmpty()) {
            throw new IllegalArgumentException("Event name cannot be empty");
        }

        if (event.getEvent_date() == null) {
            throw new IllegalArgumentException("Event date cannot be null");
        }

        return eventRepository.createEvent(event);
    }
}
