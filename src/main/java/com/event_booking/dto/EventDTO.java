package com.event_booking.dto;

import java.time.LocalDate;

public class EventDTO {

    private String event_id;
    private String event_name;
    private String event_description;
    private LocalDate event_date;
    private String event_venue;
    private boolean event_isActive;

    public EventDTO() {
    }

    public EventDTO(String event_id, String event_name, String event_description,
            LocalDate event_date, String event_venue, boolean event_isActive) {

        this.event_id = event_id;
        this.event_name = event_name;
        this.event_description = event_description;
        this.event_date = event_date;
        this.event_venue = event_venue;
        this.event_isActive = event_isActive;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_description() {
        return event_description;
    }

    public void setEvent_description(String event_description) {
        this.event_description = event_description;
    }

    public LocalDate getEvent_date() {
        return event_date;
    }

    public void setEvent_date(LocalDate event_date) {
        this.event_date = event_date;
    }

    public String getEvent_venue() {
        return event_venue;
    }

    public void setEvent_venue(String event_venue) {
        this.event_venue = event_venue;
    }

    public boolean isEvent_isActive() {
        return event_isActive;
    }

    public void setEvent_isActive(boolean event_isActive) {
        this.event_isActive = event_isActive;
    }
}
