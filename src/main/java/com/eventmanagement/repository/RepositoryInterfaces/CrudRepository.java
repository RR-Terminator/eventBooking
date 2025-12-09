package com.eventmanagement.repository.RepositoryInterfaces;

import java.util.List;

public interface CrudRepository<T, ID> {
    ID save(T entity);

    T findById(ID id);

    List<T> findAll();

    void delete(ID id);
}