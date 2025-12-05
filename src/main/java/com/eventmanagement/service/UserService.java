package com.eventmanagement.service;

import java.security.MessageDigest;

import com.eventmanagement.Exceptions.InvalidUserIdException;
import com.eventmanagement.model.UserRole;
import com.eventmanagement.model.Users;
import com.eventmanagement.repository.UserRepository;

public class UserService {

    private final UserRepository userRepository;

    public UserService(java.sql.Connection connection) {
        this.userRepository = new UserRepository(connection);
    }

    public Users getUserById(String user_id) {
        Users u = userRepository.findById(user_id);
        if (u == null)
            throw new InvalidUserIdException("User not found: " + user_id);
        return u;
    }

    public Users getUserByUsername(String user_name) {
        Users u = userRepository.findByUsername(user_name);
        if (u == null)
            throw new InvalidUserIdException("User not found: " + user_name);
        return u;
    }

    /**
     * Create user. Password will be hashed using SHA-256 here.
     * Returns created user_id.
     */
    public String createUser(String user_name, String plainPassword, String role) {
        String hashed = hashPassword(plainPassword);
        return userRepository.createUser(user_name, hashed, role);
    }

    /**
     * Authenticate user and return Users object if successful, otherwise null.
     */
    public Users login(String user_name, String plainPassword) {
        String hashed = hashPassword(plainPassword);
        Users u = userRepository.authenticateUser(user_name, hashed);
        return u;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest)
                sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Could not hash password", ex);
        }
    }

    public boolean isAdmin(Users u) {
        if (u == null)
            return false;
        return UserRole.ADMIN.name().equalsIgnoreCase(u.getUser_role());
    }
}
