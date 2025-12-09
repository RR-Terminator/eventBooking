package com.event_booking.model;


public enum SeatType {

    BRONZE("BRONZE", 500),
    SILVER("SILVER", 800),
    GOLD("GOLD", 1200),
    PLATINUM("PLATINUM", 2000);

    private final String label;
    private final double price;

    SeatType(String label, double price) {
        this.label = label;
        this.price = price;
    }

    public double getValue() {
        return price;
    }

    @Override
    public String toString() {
        return label;
    }
}
