package com.eventmanagement.DTO;

import java.util.List;

public class BookingDTO {

    private String booking_id;
    private String user_id;
    private String event_id;
    private String booking_status;
    private String booking_date;

    private List<SeatDTO> bookedSeats;

    public BookingDTO() {
    }

    public BookingDTO(String booking_id, String user_id, String event_id,
            String booking_status, String booking_date, List<SeatDTO> bookedSeats) {

        this.booking_id = booking_id;
        this.user_id = user_id;
        this.event_id = event_id;
        this.booking_status = booking_status;
        this.booking_date = booking_date;
        this.bookedSeats = bookedSeats;
    }

    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getBooking_status() {
        return booking_status;
    }

    public void setBooking_status(String booking_status) {
        this.booking_status = booking_status;
    }

    public String getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(String booking_date) {
        this.booking_date = booking_date;
    }

    public List<SeatDTO> getBookedSeats() {
        return bookedSeats;
    }

    public void setBookedSeats(List<SeatDTO> bookedSeats) {
        this.bookedSeats = bookedSeats;
    }
}
