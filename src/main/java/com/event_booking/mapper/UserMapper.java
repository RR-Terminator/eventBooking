package com.event_booking.mapper;

import com.event_booking.dto.UserDTO;
import com.event_booking.model.Users;

public class UserMapper {

    public static UserDTO toDTO(Users u) {
        return new UserDTO(
                u.getUser_id(),
                u.getUser_name());
    }
}
