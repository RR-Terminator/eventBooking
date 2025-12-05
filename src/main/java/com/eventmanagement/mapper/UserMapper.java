package com.eventmanagement.mapper;

import com.eventmanagement.DTO.UserDTO;
import com.eventmanagement.model.Users;

public class UserMapper {

    public static UserDTO toDTO(Users u) {
        return new UserDTO(
                u.getUser_id(),
                u.getUser_name()
        );
    }
}

