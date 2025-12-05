package com.eventmanagement.mapper;

import java.util.ArrayList;
import java.util.List;

import com.eventmanagement.DTO.BookingDTO;
import com.eventmanagement.DTO.SeatDTO;
import com.eventmanagement.model.Booking;
import com.eventmanagement.model.Seat;

public class BookingMapper {

    public static BookingDTO toDTO(Booking b) {

        List<SeatDTO> dtoList = new ArrayList<>();
        if (b.getBookedSeats() != null) {
            for (Seat seat : b.getBookedSeats()) {
                dtoList.add(SeatMapper.toDTO(seat));
            }
        }

        return new BookingDTO(
                b.getBooking_id(),
                b.getUser_id(),
                b.getEvent_id(),
                b.getBooking_status(),
                b.getBooking_date(),
                dtoList
        );
    }
}
