package com.eventmanagement.repository.RepositoryInterfaces;

import java.util.List;

import com.eventmanagement.model.Event;

public interface EventRepository extends CrudRepository<Event, String> {
    List<Event> findAllActive();
}
