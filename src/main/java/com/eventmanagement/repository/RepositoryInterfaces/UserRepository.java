package com.eventmanagement.repository.RepositoryInterfaces;

import com.eventmanagement.model.Users;

public interface UserRepository extends CrudRepository<Users, String> {
    Users findByUsername(String username);
}
