package com.eventmanagement.service;



import java.sql.SQLException;
import java.util.UUID;

import com.eventmanagement.model.Users;
import com.eventmanagement.repository.UserRepository;

public class UserService {

    private final UserRepository userRepo = new UserRepository();

    public boolean createUser(Users user) throws SQLException {
        return userRepo.save(user);
    }

    public boolean deleteUser(UUID userId) throws SQLException {
        return userRepo.delete(userId);
    }

    public Users getUserById(UUID userId) throws SQLException {
        return userRepo.findById(userId);
    }



}
