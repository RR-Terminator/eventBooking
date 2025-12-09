package com.event_booking.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.event_booking.dto.EventDTO;
import com.event_booking.model.Event;
import com.event_booking.persistence.EntityMapper;

public class EventMapper implements EntityMapper<Event> {

    public static EventDTO toDTO(Event e) {
        return new EventDTO(
                e.getEvent_id(),
                e.getEvent_name(),
                e.getEvent_description(),
                e.getEvent_date(),
                e.getEvent_venue(),
                e.isEvent_isActive());
    }

    @Override
    public Event map(ResultSet rs) throws SQLException {
        Event e = new Event();

        e.setEvent_id(rs.getString("event_id"));
        e.setEvent_name(rs.getString("event_name"));
        e.setEvent_description(rs.getString("event_description"));

        e.setEvent_date(rs.getDate("event_date").toLocalDate());

        e.setEvent_venue(rs.getString("event_venue"));
        e.setEvent_isActive(rs.getBoolean("event_isActive"));

        e.setCreated_at(rs.getString("created_at"));
        e.setUpdated_at(rs.getString("updated_at"));

        e.setNumber_of_bronze_seats(rs.getInt("number_of_bronze_seats"));
        e.setNumber_of_silver_seats(rs.getInt("number_of_silver_seats"));
        e.setNumber_of_gold_seats(rs.getInt("number_of_gold_seats"));
        e.setNumber_of_platinum_seats(rs.getInt("number_of_platinum_seats"));

        return e;
    }
}
