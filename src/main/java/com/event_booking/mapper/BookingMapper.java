package com.event_booking.mapper;

import java.util.ArrayList;
import java.util.List;

import com.event_booking.dto.BookingDTO;
import com.event_booking.dto.SeatDTO;
import com.event_booking.model.Booking;
import com.event_booking.model.Seat;

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
                dtoList);
    }
}
