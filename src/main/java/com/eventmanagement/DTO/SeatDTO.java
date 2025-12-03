package com.eventmanagement.DTO;

import java.util.UUID;

import com.eventmanagement.model.SeatType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeatDTO {
    private UUID seat_id;
    private SeatType seat_type;
    private double seat_price;
    private boolean is_available;
}
