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
        java.sql.Connection connection = null;

        try {
            connection = DataBaseConnection.getConnection();

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
                    String input = sc.nextLine().trim();

                    switch (input) {
                        case "1" -> {
                            System.out.print("Enter username: ");
                            String username = sc.nextLine().trim();
                            System.out.print("Enter password: ");
                            String password = sc.nextLine();
                            String role = "USER"; // default

                            try {
                                String newUserId = userService.createUser(username, password, role);
                                System.out.println("User created. ID: " + newUserId);
                            } catch (Exception ex) {
                                System.out.println("Failed to create user: " + ex.getMessage());
                            }
                        }
                        case "2" -> {
                            System.out.print("Enter username: ");
                            String username = sc.nextLine().trim();
                            System.out.print("Enter password: ");
                            String password = sc.nextLine();
                            try {
                                Users u = userService.login(username, password);
                                if (u == null) {
                                    System.out.println("Invalid credentials.");
                                } else {
                                    loggedInUser = u;
                                    System.out.println("Logged in as: " + loggedInUser.getUser_name() + " (role: "
                                            + loggedInUser.getUser_role() + ")");
                                }
                            } catch (Exception ex) {
                                System.out.println("Login failed: " + ex.getMessage());
                            }
                        }
                        case "3" -> running = false;
                        default -> System.out.println("Invalid option.");
                    }
                } else {
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
                    String choice = sc.nextLine().trim();

                    switch (choice) {

                        case "1" -> {
                            if (!isAdmin) {
                                System.out.println("Only admins can create events.");
                                break;
                            }
                            try {
                                System.out.print("Enter event name: ");
                                String name = sc.nextLine().trim();

                                System.out.print("Enter event description: ");
                                String desc = sc.nextLine().trim();

                                System.out.print("Enter event date (YYYY-MM-DD): ");
                                String dateStr = sc.nextLine().trim();
                                LocalDate date = LocalDate.parse(dateStr);

                                System.out.print("Enter event venue: ");
                                String venue = sc.nextLine().trim();

                                System.out.print("Number of Bronze Seats: ");
                                int bronze = Integer.parseInt(sc.nextLine().trim());

                                System.out.print("Number of Silver Seats: ");
                                int silver = Integer.parseInt(sc.nextLine().trim());

                                System.out.print("Number of Gold Seats: ");
                                int gold = Integer.parseInt(sc.nextLine().trim());

                                System.out.print("Number of Platinum Seats: ");
                                int platinum = Integer.parseInt(sc.nextLine().trim());

                                Event event = new Event();
                                event.setEvent_name(name);
                                event.setEvent_description(desc);
                                event.setEvent_date(date);
                                event.setEvent_venue(venue);
                                event.setNumber_of_bronze_seats(bronze);
                                event.setNumber_of_silver_seats(silver);
                                event.setNumber_of_gold_seats(gold);
                                event.setNumber_of_platinum_seats(platinum);

                                String newEventId = eventService.createEvent(event);
                                System.out.println("Event created with ID: " + newEventId + ". Seats generated automatically.");
                            } catch (java.time.format.DateTimeParseException dtpe) {
                                System.out.println("Invalid date format. Use YYYY-MM-DD.");
                            } catch (NumberFormatException nfe) {
                                System.out.println("Seat counts must be integers.");
                            } catch (Exception ex) {
                                System.out.println("Failed to create event: " + ex.getMessage());
                            }
                        }

                        case "2" -> {
                            try {
                                List<Event> events = eventService.getAllEvents();
                                if (events.isEmpty()) {
                                    System.out.println("No active events found.");
                                } else {
                                    System.out.println("\nActive events:");
                                    for (Event e : events) {
                                        System.out.println(e.getEvent_id() + " | " + e.getEvent_name() + " | "
                                                + (e.getEvent_date() != null ? e.getEvent_date().toString() : "N/A")
                                                + " | Venue: " + e.getEvent_venue());
                                    }
                                }
                            } catch (Exception ex) {
                                System.out.println("Failed to list events: " + ex.getMessage());
                            }
                        }

                        case "3" -> {
                            System.out.print("Enter Event ID to book: ");
                            String eventIdStr = sc.nextLine().trim();

                            try {
                                List<String> selectedSeatIds = new ArrayList<>();
                                boolean selecting = true;

                                while (selecting) {
                                    Map<SeatType, Integer> availableSeats = bookingService.numberOfSeatsAvailble(eventIdStr);
                                    System.out.println("\nAvailable Seat Types:");
                                    for (SeatType st : SeatType.values()) {
                                        int cnt = availableSeats.getOrDefault(st, 0);
                                        System.out.println("- " + st.name() + " (Price: " + st.getValue() + ", Available: " + cnt + ")");
                                    }

                                    System.out.print("Enter seat type to view seats or 'done' to finish selection: ");
                                    String stInput = sc.nextLine().trim();
                                    if ("done".equalsIgnoreCase(stInput)) {
                                        selecting = false;
                                        continue;
                                    }

                                    SeatType sel;
                                    try {
                                        sel = SeatType.valueOf(stInput.toUpperCase());
                                    } catch (IllegalArgumentException ex) {
                                        System.out.println("Invalid seat type. Try BRONZE, SILVER, GOLD, or PLATINUM.");
                                        continue;
                                    }

                                    List<Seat> availList = bookingService.avaliableSeatlist(eventIdStr, sel);
                                    if (availList.isEmpty()) {
                                        System.out.println("No available seats of this type.");
                                        continue;
                                    }

                                    System.out.println("Available seats of type " + sel + ":");
                                    for (Seat s : availList) {
                                        System.out.println("- " + s.getSeat_id() + " (Price: " + s.getSeat_price() + ")");
                                    }

                                    System.out.print("How many seats of this type do you want to pick? ");
                                    int q;
                                    try {
                                        q = Integer.parseInt(sc.nextLine().trim());
                                    } catch (NumberFormatException nfe) {
                                        System.out.println("Invalid number.");
                                        continue;
                                    }

                                    if (q <= 0) {
                                        System.out.println("Quantity must be >= 1.");
                                        continue;
                                    }

                                    if (q > availList.size()) {
                                        System.out.println("Only " + availList.size() + " seats available of this type.");
                                        continue;
                                    }

                                    for (int i = 0; i < q; i++) {
                                        System.out.print("Enter seat id from the list above: ");
                                        String sid = sc.nextLine().trim();
                                        boolean found = false;
                                        for (Seat s : availList) {
                                            if (sid.equals(s.getSeat_id())) {
                                                found = true;
                                                break;
                                            }
                                        }
                                        if (!found) {
                                            System.out.println("Seat id not in the available list. Try again.");
                                            i--;
                                            continue;
                                        }
                                        selectedSeatIds.add(sid);
                                    }

                                    System.out.print("Add more seats? (yes/no): ");
                                    String more = sc.nextLine().trim();
                                    if (!"yes".equalsIgnoreCase(more)) selecting = false;
                                } // end selection loop

                                if (selectedSeatIds.isEmpty()) {
                                    System.out.println("No seats selected. Cancelling booking.");
                                    break;
                                }

                                // perform booking (transaction handled in BookingService)
                                String bookingId = bookingService.createBooking(connection, loggedInUser.getUser_id(), eventIdStr, selectedSeatIds);
                                System.out.println("Booking successful. booking_id: " + bookingId);

                            } catch (Exception ex) {
                                System.out.println("Booking failed: " + ex.getMessage());
                            }
                        }

                        case "4" -> {
                            try {
                                List<Booking> bookings = bookingService.getBookingsByUser(loggedInUser.getUser_id());
                                if (bookings == null || bookings.isEmpty()) {
                                    System.out.println("You have no bookings.");
                                } else {
                                    System.out.println("\nYour bookings:");
                                    for (Booking b : bookings) {
                                        System.out.println(b.getBooking_id() + " | " + b.getBooking_date() + " | " + b.getBooking_status());
                                        if (b.getBookedSeats() != null && !b.getBookedSeats().isEmpty()) {
                                            System.out.println("  Seats:");
                                            for (Seat s : b.getBookedSeats()) {
                                                System.out.println("   - " + s.getSeat_id() + " (" + s.getSeat_type() + ")");
                                            }
                                        }
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
                            String evId = sc.nextLine().trim();
                            try {
                                boolean canceled = eventService.cancelEvent(evId);
                                System.out.println("Event canceled: " + canceled);
                            } catch (Exception ex) {
                                System.out.println("Failed to cancel event: " + ex.getMessage());
                            }
                        }

                        case "6" -> {
                            loggedInUser = null;
                            System.out.println("Logged out.");
                        }

                        default -> System.out.println("Invalid option.");
                    } // end switch logged in
                } // end else loggedInUser != null
            } // end while running

        } catch (Exception ex) {
            System.err.println("Fatal error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try { if (connection != null) connection.close(); } catch (Exception ignored) {}
            sc.close();
        }
    }
}


//TODO Multi Threading Implementation
//TODO Comparator Implementation
//TODO Make SOLID Proof

//TODO Change the Impl to more genrics as possible
//TODO Add Custom Compartor logic 
//TODO use of Streams 



