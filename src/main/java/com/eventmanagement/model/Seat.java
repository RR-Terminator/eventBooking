package com.eventmanagement.model;

public class Seat {

    private String seat_id;
    private String seat_type;
    private double seat_price;
    private boolean is_available;
    private String event_id;

    public Seat() {
    }

    public Seat(String seat_id, String seat_type, double seat_price, boolean is_available, String event_id) {
        this.seat_id = seat_id;
        this.seat_type = seat_type;
        this.seat_price = seat_price;
        this.is_available = is_available;
        this.event_id = event_id;
    }

    public String getSeat_id() {
        return seat_id;
    }

    public void setSeat_id(String seat_id) {
        this.seat_id = seat_id;
    }

    public String getSeat_type() {
        return seat_type;
    }

    public void setSeat_type(String seat_type) {
        this.seat_type = seat_type;
    }

    public double getSeat_price() {
        return seat_price;
    }

    public void setSeat_price(double seat_price) {
        this.seat_price = seat_price;
    }

    public boolean isIs_available() {
        return is_available;
    }

    public void setIs_available(boolean is_available) {
        this.is_available = is_available;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }
}
