package com.eventmanagement.model;

public class Event {

    private String event_id;
    private String event_name;
    private String event_description;
    private String event_date;
    private String event_venue;
    private boolean event_isActive;
    private String created_at;
    private String updated_at;

    public Event() {
    }

    public Event(String event_id, String event_name, String event_description,
            String event_date, String event_venue, boolean event_isActive,
            String created_at, String updated_at) {

        this.event_id = event_id;
        this.event_name = event_name;
        this.event_description = event_description;
        this.event_date = event_date;
        this.event_venue = event_venue;
        this.event_isActive = event_isActive;
        this.created_at = created_at;
        this.updated_at = updated_at;
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

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
