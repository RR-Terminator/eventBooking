package com.eventmanagement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.eventmanagement.model.Booking;
import com.eventmanagement.model.Event;
import com.eventmanagement.model.Seat;
import com.eventmanagement.model.SeatType;
import com.eventmanagement.model.Users;
import com.eventmanagement.service.BookingService;
import com.eventmanagement.service.EventService;
import com.eventmanagement.service.UserService;
import com.eventmanagement.utils.dbconfig.DataBaseConnection;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // Obtain connection from your existing singleton connection manager
        java.sql.Connection connection = null;
        try {
            connection = DataBaseConnection.getConnection(); // REPLACE with your connection call

            UserService userService = new UserService(connection);
            EventService eventService = new EventService(connection);
            BookingService bookingService = new BookingService(connection);

            boolean running = true;
            Users loggedInUser = null;

            while (running) {
                if (loggedInUser == null) {
                    System.out.println("\n=== EVENT BOOKING SYSTEM (Not logged in) ===");
                    System.out.println("1. Register");
                    System.out.println("2. Login");
                    System.out.println("3. Exit");
                    System.out.print("Choose an option: ");
                    String input = sc.nextLine();
                    if (input.equals("1")) {
                        System.out.print("Enter username: ");
                        String username = sc.nextLine();
                        System.out.print("Enter password: ");
                        String password = sc.nextLine();

                        // By default new users are USER role. If you want to create ADMIN, do it
                        // manually via DB or require an ADMIN logged in.
                        String role = "USER";

                        try {
                            String newUserId = userService.createUser(username, password, role);
                            System.out.println("User created. ID: " + newUserId);
                        } catch (Exception ex) {
                            System.out.println("Failed to create user: " + ex.getMessage());
                        }
                    } else if (input.equals("2")) {
                        System.out.print("Enter username: ");
                        String username = sc.nextLine();
                        System.out.print("Enter password: ");
                        String password = sc.nextLine();
                        Users u = userService.login(username, password);
                        if (u == null) {
                            System.out.println("Invalid credentials.");
                        } else {
                            loggedInUser = u;
                            System.out.println("Logged in as: " + loggedInUser.getUser_name() + " (role: "
                                    + loggedInUser.getUser_role() + ")");
                        }
                    } else if (input.equals("3")) {
                        running = false;
                    } else {
                        System.out.println("Invalid option.");
                    }
                } else {
                    // Logged in menu — role-based
                    boolean isAdmin = "ADMIN".equalsIgnoreCase(loggedInUser.getUser_role());

                    System.out.println("\n=== EVENT BOOKING SYSTEM (Logged in: " + loggedInUser.getUser_name() + " | "
                            + loggedInUser.getUser_role() + ") ===");
                    System.out.println("1. Create Event" + (isAdmin ? "" : " (admin only)"));
                    System.out.println("2. List All Events");
                    System.out.println("3. Book Seats");
                    System.out.println("4. List my Bookings");
                    System.out.println("5. Cancel Event" + (isAdmin ? " (admin only)" : ""));
                    System.out.println("6. Logout");
                    System.out.print("Choose an option: ");
                    String choice = sc.nextLine();

                    switch (choice) {
                        case "1" -> {
                            if (!isAdmin) {
                                System.out.println("Only admins can create events.");
                                break;
                            }
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
                            event.setEvent_date(date.toString());
                            event.setEvent_venue(venue);
                            // You may need to provide seat creation logic here or create seats by another
                            // admin command
                            System.out.print("Number of Bronze Seats: ");
                            int bronze = Integer.parseInt(sc.nextLine());
                            System.out.print("Number of Silver Seats: ");
                            int silver = Integer.parseInt(sc.nextLine());
                            System.out.print("Number of Gold Seats: ");
                            int gold = Integer.parseInt(sc.nextLine());
                            System.out.print("Number of Platinum Seats: ");
                            int platinum = Integer.parseInt(sc.nextLine());
                            // Assuming EventService.createEvent returns created event_id as String
                            try {
                                String newEventId = eventService.createEvent(event);
                                System.out.println("Event created with ID: " + newEventId);
                                // After creating event, you should create seats under seats table for that
                                // event.
                                System.out.println(
                                        "NOTE: You must create seats for the event in the seats table (admin responsibility).");
                            } catch (Exception ex) {
                                System.out.println("Failed to create event: " + ex.getMessage());
                            }
                        }
                        case "2" -> {
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
                        case "3" -> {
                            System.out.print("Enter Event ID to book: ");
                            String eventIdStr = sc.nextLine();
                            List<String> selectedSeatIds = new ArrayList<>();
                            boolean adding = true;
                            while (adding) {
                                Map<SeatType, Integer> availableSeats = bookingService
                                        .numberOfSeatsAvailble(eventIdStr);
                                System.out.println("\nAvailable Seat Types:");
                                for (SeatType st : SeatType.values()) {
                                    int cnt = availableSeats.getOrDefault(st, 0);
                                    System.out.println("- " + st.name() + " (Price: " + st.getValue() + ", Available: "
                                            + cnt + ")");
                                }
                                System.out.print("Enter seat type to pick (DONE to finish): ");
                                String stInput = sc.nextLine().toUpperCase();
                                if (stInput.equals("DONE")) {
                                    adding = false;
                                    continue;
                                }
                                SeatType sel;
                                try {
                                    sel = SeatType.valueOf(stInput);
                                } catch (IllegalArgumentException ex) {
                                    System.out.println("Invalid type.");
                                    continue;
                                }
                                List<Seat> availList = bookingService.avaliableSeatlist(eventIdStr, sel);
                                if (availList.isEmpty()) {
                                    System.out.println("No seats of that type.");
                                    continue;
                                }
                                System.out.println("Available seats:");
                                for (Seat s : availList) {
                                    System.out.println("- " + s.getSeat_id() + " (price " + s.getSeat_price() + ")");
                                }
                                System.out.print("Enter number to pick: ");
                                int q = Integer.parseInt(sc.nextLine());
                                for (int i = 0; i < q; i++) {
                                    System.out.print("Enter seat id: ");
                                    String sid = sc.nextLine();
                                    selectedSeatIds.add(sid);
                                }
                            }

                            if (selectedSeatIds.isEmpty()) {
                                System.out.println("No seats selected. Cancelling booking.");
                                break;
                            }

                            try {
                                // createBooking method expects connection and user_id string
                                String bookingId = bookingService.createBooking(connection, loggedInUser.getUser_id(),
                                        eventIdStr, selectedSeatIds);
                                System.out.println("Booking successful. booking_id: " + bookingId);
                            } catch (Exception ex) {
                                System.out.println("Booking failed: " + ex.getMessage());
                            }
                        }
                        case "4" -> {
                            try {
                                List<Booking> bookings = bookingService.getBookingsByUser(loggedInUser.getUser_id());
                                if (bookings.isEmpty()) {
                                    System.out.println("You have no bookings.");
                                } else {
                                    for (Booking b : bookings) {
                                        System.out.println(b.getBooking_id() + " | " + b.getBooking_date() + " | "
                                                + b.getBooking_status());
                                    }
                                }
                            } catch (Exception ex) {
                                System.out.println("Failed to fetch bookings: " + ex.getMessage());
                            }
                        }
                        case "5" -> {
                            if (!isAdmin) {
                                System.out.println("Only admins can cancel events.");
                                break;
                            }
                            System.out.print("Enter Event ID to cancel: ");
                            String evId = sc.nextLine();
                            boolean canceled = eventService.cancelEvent(evId);
                            System.out.println("Event canceled: " + canceled);
                        }
                        case "6" -> {
                            loggedInUser = null;
                            System.out.println("Logged out.");
                        }
                        default -> System.out.println("Invalid option.");
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (Exception ignored) {
            }
            sc.close();
        }
    }
}
