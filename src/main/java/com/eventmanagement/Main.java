package com.eventmanagement;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import com.eventmanagement.model.Booking;
import com.eventmanagement.model.Event;
import com.eventmanagement.model.Seat;
import com.eventmanagement.model.SeatType;
import com.eventmanagement.model.Users;
import com.eventmanagement.service.BookingService;
import com.eventmanagement.service.EventService;
import com.eventmanagement.service.UserService;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        UserService userService = new UserService();
        EventService eventService = new EventService();
        BookingService bookingService = new BookingService();

        try {
            boolean running = true;

            while (running) {

                System.out.println("\n=== EVENT BOOKING SYSTEM ===");
                System.out.println("1. Create User");
                System.out.println("2. Create Event");
                System.out.println("3. List All Events");
                System.out.println("4. Book Seats");
                System.out.println("5. List all Booked Event by UserId Event");
                System.out.println("6. Cancel Event");
                System.out.println("7. Exit");
                System.out.print("Choose an option: ");

                int option = Integer.parseInt(sc.nextLine());

                switch (option) {
                    case 1 -> {
                        System.out.print("Enter username: ");
                        String username = sc.nextLine();
                        System.out.print("Enter password: ");
                        String password = sc.nextLine();

                        Users user = new Users();
                        user.setUser_name(username);
                        user.setUser_password(password);

                        boolean created = userService.createUser(user);
                        if (created) {
                            System.out.println("User created with ID: " + user.getUser_id());
                        } else {
                            System.out.println("Failed to create user.");
                        }
                    }

                    case 2 -> {
                        System.out.print("Enter event name: ");
                        String name = sc.nextLine();
                        System.out.print("Enter event description: ");
                        String desc = sc.nextLine();
                        System.out.print("Enter event date (YYYY-MM-DD): ");
                        LocalDate date = LocalDate.parse(sc.nextLine());
                        System.out.print("Enter event venue: ");
                        String venue = sc.nextLine();
                        Event event = new Event();
                        event.setEvent_name(name);
                        event.setEvent_description(desc);
                        event.setEvent_date(date);
                        event.setEvent_venue(venue);
                        System.out.print("Number of Bronze Seats: ");
                        event.setNumber_of_bronze_seats(Integer.parseInt(sc.nextLine()));
                        System.out.print("Number of Silver Seats: ");
                        event.setNumber_of_silver_seats(Integer.parseInt(sc.nextLine()));
                        System.out.print("Number of Gold Seats: ");
                        event.setNumber_of_gold_seats(Integer.parseInt(sc.nextLine()));
                        System.out.print("Number of Platinum Seats: ");
                        event.setNumber_of_platinum_seats(Integer.parseInt(sc.nextLine()));

                        UUID eventId = eventService.createEvent(event);
                        System.out.println("Event created with ID: " + eventId);
                    }

                    case 3 -> {
                        List<Event> events = eventService.getAllEvents();
                        if (events.isEmpty()) {
                            System.out.println("No active events found.");
                        } else {
                            for (Event e : events) {
                                System.out.println(e.getEvent_id() + " | " + e.getEvent_name() +
                                        " | " + e.getEvent_date() +
                                        " | Venue: " + e.getEvent_venue());
                            }
                        }
                    }

                    case 4 -> {
                        System.out.print("Enter your User ID: ");
                        UUID userId = UUID.fromString(sc.nextLine());

                        System.out.print("Enter Event ID to book: ");
                        UUID eventId = UUID.fromString(sc.nextLine());

                        Event event = eventService.getEventById(eventId);
                        if (event == null) {
                            System.out.println("Event not found.");
                            break;
                        }

                        Booking booking = new Booking();
                        booking.setUser_id(userId);
                        booking.setEvent_id(eventId);

                        boolean addingSeats = true;

                        while (addingSeats) {
                            // Fetch available seats for this event
                            Map<SeatType, Integer> availableSeats;
                            try {
                                availableSeats = bookingService.numberOfSeatsAvailble(eventId);
                            } catch (SQLException e) {
                                System.out.println("Error fetching available seats: " + e.getMessage());
                                break;
                            }

                            // Show available seat types with counts
                            System.out.println("\nAvailable Seat Types:");
                            for (SeatType type : SeatType.values()) {
                                int count = availableSeats.getOrDefault(type, 0);
                                System.out.println("- " + type.name() + " (Price: " + type.getValue() + ", Available: "
                                        + count + ")");
                            }

                            System.out.print("Enter seat type to book (or 'done' to finish): ");
                            String seatTypeInput = sc.nextLine().toUpperCase();

                            if (seatTypeInput.equals("DONE")) {
                                addingSeats = false;
                                continue;
                            }

                            SeatType selectedType;
                            try {
                                selectedType = SeatType.valueOf(seatTypeInput);
                                // Display available seats for the selected type
                                List<Seat> availableSeatList = bookingService.avaliableSeatlist(eventId, selectedType);
                                if (availableSeatList.isEmpty()) {
                                    System.out.println("No available seats of this type.");
                                    continue;
                                }
                                // Show available seats for the selected type
                                System.out.println("Available seats of type " + selectedType + ":");
                                for (Seat seat : availableSeatList) {
                                    System.out.println("- Seat ID: " + seat.getSeat_id() + " (Price: "
                                            + seat.getSeat_price() + ")");
                                }
                            } catch (IllegalArgumentException e) {
                                System.out.println("Invalid seat type. Try again.");
                                continue;
                            }

                            int maxAvailable = availableSeats.getOrDefault(selectedType, 0);
                            if (maxAvailable == 0) {
                                System.out.println("No seats available for " + selectedType);
                                continue;
                            }

                            System.out.print("Enter number of seats to book for " + selectedType + " (Max "
                                    + maxAvailable + "): ");
                            int quantity;
                            try {
                                quantity = Integer.parseInt(sc.nextLine());
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid number. Try again.");
                                continue;
                            }

                            if (quantity <= 0 || quantity > maxAvailable) {
                                System.out.println("Invalid quantity. Must be between 1 and " + maxAvailable);
                                continue;
                            }

                            List<String> selectedSeats = new ArrayList<>();

                            // Allow the user to select specific seat IDs
                            for (int i = 0; i < quantity; i++) {
                                System.out.print("Enter the Seat ID to book (from the available list above): ");
                                String seatIdInput = sc.nextLine().trim();
                                selectedSeats.add(seatIdInput);
                            }

                            // Add the selected seats to the booking
                            for (String seatId : selectedSeats) {
                                Seat seat = new Seat();
                                seat.setSeat_id(UUID.fromString(seatId));
                                seat.setEvent_id(eventId);
                                seat.setSeat_type(selectedType);
                                seat.setSeat_price(selectedType.getValue());
                                booking.getBookedSeats().add(seat);

                                // Mark the seat as unavailable in the database
                                // try {
                                // bookingService.updateSeatAvailability(seat); // Make the seat unavailable
                                // } catch (SQLException e) {
                                // System.out.println("Error updating seat availability: " + e.getMessage());
                                // continue;
                                // }
                            }

                            System.out.println(quantity + " " + selectedType + " seat(s) added to booking.");
                        }

                        // Create booking in the database
                        try {
                            boolean booked = bookingService.createBooking(booking);
                            if (booked) {
                                System.out.println("Booking successful for user: " + userId);
                            } else {
                                System.out.println("Booking failed. Some seats may be unavailable.");
                            }
                        } catch (Exception e) {
                            System.out.println("Booking failed: " + e.getMessage());
                        }
                    }
                    case 5 -> {
                        System.out.println("Enter UserID to view all the bookings");
                        UUID userId = UUID.fromString(sc.nextLine());
                        List<Booking> bookedEventList = bookingService.getBookingsByUser(userId);

                        bookedEventList.forEach(booking -> {
                            System.out.println(booking.getBooking_id() + " | " + booking.getBooking_date() + " | "
                                    + booking.getBooking_status() + " | " + booking.getBookedSeats());
                        });

                    }
                    case 6 -> {
                        System.out.print("Enter Event ID to cancel: ");
                        UUID eventId = UUID.fromString(sc.nextLine());
                        boolean canceled = eventService.cancelEvent(eventId);
                        System.out.println("Event canceled: " + canceled);
                    }

                    case 7 -> {
                        running = false;
                        System.out.println("Exiting...");
                    }

                    default -> System.out.println("Invalid option. Try again.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sc.close();
        }
    }
}
