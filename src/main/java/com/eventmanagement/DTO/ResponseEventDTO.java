package com.eventmanagement.DTO;


import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseEventDTO {

    private UUID event_id;
    private String event_name;
    private String event_description;
    private LocalDate event_date;
    private String event_venue;
    private boolean event_isActive;
    private List<SeatDTO> seatDetails;

    // Smaller constructor used for listing events
    public ResponseEventDTO(UUID id, String name, LocalDate date) {
        this.event_id = id;
        this.event_name = name;
        this.event_date = date;
    }
}

