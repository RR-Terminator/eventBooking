package com.event_booking.repository;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T, ID> {

    ID save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    boolean delete(ID id);

    T update (T entity);
}

