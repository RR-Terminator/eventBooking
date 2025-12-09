package com.event_booking.repository.implementation;

import java.sql.Connection;

import com.event_booking.mapper.EventMapper;
import com.event_booking.model.Event;
import com.event_booking.repository.JdbcRepository;

public class EventRepositoryJdbcImpl
        extends JdbcRepository<Event, String> {

    public EventRepositoryJdbcImpl(Connection connection) {
        super(connection, new EventMapper());
    }

    @Override
    protected String getTableName() {
        return "events";
    }

    @Override
    protected String getPrimaryKeyColumn() {
        return "event_id";
    }

    @Override
    public String save(Event event) {
        if (event.getEvent_id() == null || event.getEvent_id().isEmpty()) {
            event.setEvent_id(java.util.UUID.randomUUID().toString());
        }
        return super.save(event);
    }
}
