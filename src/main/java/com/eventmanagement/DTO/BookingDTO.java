package com.eventmanagement.DTO;



import java.util.Date;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingDTO {

    private UUID booking_id;
    private UUID user_id;
    private UUID event_id;
    private String booking_status;
    private Date booking_date;

    private List<SeatDTO> bookedSeats;
}
