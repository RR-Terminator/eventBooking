package com.event_booking.mapper;

import com.event_booking.dto.SeatDTO;
import com.event_booking.model.Seat;

public class SeatMapper {

    public static SeatDTO toDTO(Seat s) {
        return new SeatDTO(
                s.getSeat_id(),
                s.getSeat_type(),
                s.getSeat_price(),
                s.isIs_available());
    }
}
