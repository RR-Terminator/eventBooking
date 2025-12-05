package com.eventmanagement.mapper;

import com.eventmanagement.DTO.SeatDTO;
import com.eventmanagement.model.Seat;

public class SeatMapper {

    public static SeatDTO toDTO(Seat s) {
        return new SeatDTO(
                s.getSeat_id(),
                s.getSeat_type(),
                s.getSeat_price(),
                s.isIs_available()
        );
    }
}
