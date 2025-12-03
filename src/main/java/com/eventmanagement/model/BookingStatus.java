package com.eventmanagement.model;



public enum BookingStatus {
    CREATED,    // Booking started but not confirmed
    CONFIRMED,  // Booking successful
    FAILED,     // Booking failed (e.g., payment)
    CANCELLED;  // User or system cancelled
}
