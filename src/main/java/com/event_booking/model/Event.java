package com.event_booking.model;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import com.event_booking.persistence.PersistableEntity;

public class Event implements PersistableEntity<String> {

    private String event_id;
    private String event_name;
    private String event_description;
    private LocalDate event_date;
    private String event_venue;
    private boolean event_isActive;
    private String created_at;
    private String updated_at;
    private int number_of_bronze_seats;
    private int number_of_silver_seats;
    private int number_of_gold_seats;
    private int number_of_platinum_seats;

    public Event() { }

    public Event(String event_id, String event_name, String event_description,
                 LocalDate event_date, String event_venue, boolean event_isActive,
                 String created_at, String updated_at,
                 int number_of_bronze_seats, int number_of_silver_seats,
                 int number_of_gold_seats, int number_of_platinum_seats) {

        this.event_id = event_id;
        this.event_name = event_name;
        this.event_description = event_description;
        this.event_date = event_date;
        this.event_venue = event_venue;
        this.event_isActive = event_isActive;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.number_of_bronze_seats = number_of_bronze_seats;
        this.number_of_silver_seats = number_of_silver_seats;
        this.number_of_gold_seats = number_of_gold_seats;
        this.number_of_platinum_seats = number_of_platinum_seats;
    }

    // ------------------------ PersistableEntity Implementation ------------------------

    @Override
    public String getTableName() {
        return "events"; // <-- YOUR ACTUAL TABLE NAME
    }

    @Override
    public Map<String, Object> getColumnValues() {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("event_id", event_id);
        map.put("event_name", event_name);
        map.put("event_description", event_description);
        map.put("event_date", event_date); // LocalDate → JDBC handles via converter
        map.put("event_venue", event_venue);
        map.put("event_isActive", event_isActive);
        map.put("created_at", created_at);
        map.put("updated_at", updated_at);
        map.put("number_of_bronze_seats", number_of_bronze_seats);
        map.put("number_of_silver_seats", number_of_silver_seats);
        map.put("number_of_gold_seats", number_of_gold_seats);
        map.put("number_of_platinum_seats", number_of_platinum_seats);

        return map;
    }

    @Override
    public String getPrimaryKeyColumn() {
        return "event_id";
    }

    @Override
    public String getPrimaryKeyValue() {
        return event_id;
    }

    @Override
    public void setPrimaryKeyValue(String id) {
        this.event_id = id;
    }

    // ---------------------------- Getters and Setters ----------------------------

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

    public int getNumber_of_bronze_seats() {
        return number_of_bronze_seats;
    }

    public void setNumber_of_bronze_seats(int number_of_bronze_seats) {
        this.number_of_bronze_seats = number_of_bronze_seats;
    }

    public int getNumber_of_silver_seats() {
        return number_of_silver_seats;
    }

    public void setNumber_of_silver_seats(int number_of_silver_seats) {
        this.number_of_silver_seats = number_of_silver_seats;
    }

    public int getNumber_of_gold_seats() {
        return number_of_gold_seats;
    }

    public void setNumber_of_gold_seats(int number_of_gold_seats) {
        this.number_of_gold_seats = number_of_gold_seats;
    }

    public int getNumber_of_platinum_seats() {
        return number_of_platinum_seats;
    }

    public void setNumber_of_platinum_seats(int number_of_platinum_seats) {
        this.number_of_platinum_seats = number_of_platinum_seats;
    }
}
