package com.eventmanagement.model;

import java.util.List;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Users {

    private UUID user_id = UUID.randomUUID();
    private String user_name;
    private String user_password; // Note: store hashed in production!

    private List<Booking> bookings;

    public Users(String username, String password) {
        this.user_name = username;
        this.user_password = password;
    }
}
