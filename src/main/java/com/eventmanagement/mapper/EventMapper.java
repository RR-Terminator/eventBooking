package com.eventmanagement.mapper;

import com.eventmanagement.DTO.EventDTO;
import com.eventmanagement.model.Event;

public class EventMapper {

    public static EventDTO toDTO(Event e) {
        return new EventDTO(
                e.getEvent_id(),
                e.getEvent_name(),
                e.getEvent_description(),
                e.getEvent_date(),
                e.getEvent_venue(),
                e.isEvent_isActive()
        );
    }
}
