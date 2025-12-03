package com.eventmanagement.model;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.NonNull;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class Event {

    private UUID event_id;

    @NonNull
    private String event_name;

    private String event_description;

    @NonNull
    private LocalDate event_date;

    @NonNull
    private String event_venue;

    private boolean event_isActive = true;

    private int number_of_bronze_seats;
    private int number_of_silver_seats;
    private int number_of_gold_seats;
    private int number_of_platinum_seats;

    public Event(String event_name,
            String event_description,
            LocalDate event_date,
            String event_venue,
            int bronze,
            int silver,
            int gold,
            int platinum) {

        this.event_name = event_name;
        this.event_description = event_description;
        this.event_date = event_date;
        this.event_venue = event_venue;
        this.number_of_bronze_seats = bronze;
        this.number_of_silver_seats = silver;
        this.number_of_gold_seats = gold;
        this.number_of_platinum_seats = platinum;
    }
}
