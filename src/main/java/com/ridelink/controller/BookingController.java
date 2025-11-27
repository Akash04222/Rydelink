package com.ridelink.controller;

import com.ridelink.model.Booking;
import com.ridelink.model.Ride;
import com.ridelink.model.User;
import com.ridelink.repository.BookingRepository;
import com.ridelink.repository.RideRepository;
import com.ridelink.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    // Show booking form
    @GetMapping("/create/{rideId}")
    public String showBookingForm(@PathVariable Long rideId,
                                  HttpSession session,
                                  Model model) {
        User rider = (User) session.getAttribute("user");
        if (rider == null) return "redirect:/login";

        Optional<Ride> rideOpt = rideRepository.findById(rideId);
        if (rideOpt.isEmpty()) {
            model.addAttribute("error", "Ride not found!");
            return "redirect:/dashboard";
        }

        Ride ride = rideOpt.get();

        // Check if rider is trying to book their own ride
        if (ride.getDriver().getId().equals(rider.getId())) {
            model.addAttribute("error", "You cannot book your own ride!");
            return "redirect:/dashboard";
        }

        model.addAttribute("ride", ride);
        model.addAttribute("booking", new Booking());
        return "book-ride";
    }

    // Process booking
    @PostMapping("/create")
    public String createBooking(@RequestParam Long rideId,
                                @RequestParam int seatsBooked,
                                @RequestParam(required = false) String notes,
                                HttpSession session,
                                Model model) {
        User rider = (User) session.getAttribute("user");
        if (rider == null) return "redirect:/login";

        try {
            Optional<Ride> rideOpt = rideRepository.findById(rideId);
            if (rideOpt.isEmpty()) {
                model.addAttribute("error", "Ride not found!");
                return "redirect:/dashboard";
            }

            Ride ride = rideOpt.get();

            // Validate booking
            if (seatsBooked <= 0) {
                model.addAttribute("error", "Please select at least 1 seat");
                model.addAttribute("ride", ride);
                return "book-ride";
            }

            if (!ride.hasAvailableSeats(seatsBooked)) {
                model.addAttribute("error", "Not enough seats available. Only " + ride.getAvailableSeats() + " seats left.");
                model.addAttribute("ride", ride);
                return "book-ride";
            }

            // Check if rider is the driver
            if (ride.getDriver().getId().equals(rider.getId())) {
                model.addAttribute("error", "You cannot book your own ride!");
                return "redirect:/dashboard";
            }

            // Create booking
            Booking booking = new Booking(ride, rider, seatsBooked);
            booking.setNotes(notes);

            // For now, auto-confirm the booking
            // In real scenario, driver would confirm
            booking.setStatus("CONFIRMED");

            // Update available seats
            ride.setAvailableSeats(ride.getAvailableSeats() - seatsBooked);

            bookingRepository.save(booking);
            rideRepository.save(ride);

            return "redirect:/bookings/success?bookingId=" + booking.getId();

        } catch (Exception e) {
            model.addAttribute("error", "Booking failed: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }

    // Booking success page
    @GetMapping("/success")
    public String bookingSuccess(@RequestParam Long bookingId,
                                 HttpSession session,
                                 Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            model.addAttribute("error", "Booking not found!");
            return "redirect:/dashboard";
        }

        Booking booking = bookingOpt.get();

        // Verify user owns this booking
        if (!booking.getRider().getId().equals(user.getId())) {
            model.addAttribute("error", "Access denied!");
            return "redirect:/dashboard";
        }

        model.addAttribute("booking", booking);
        return "booking-success";
    }

    // View user's bookings
    @GetMapping("/my-bookings")
    public String myBookings(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        List<Booking> bookings = bookingRepository.findByRiderId(user.getId());
        model.addAttribute("bookings", bookings);
        return "my-bookings";
    }

    // Cancel booking
    @PostMapping("/cancel/{bookingId}")
    public String cancelBooking(@PathVariable Long bookingId,
                                HttpSession session,
                                Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            model.addAttribute("error", "Booking not found!");
            return "redirect:/dashboard";
        }

        Booking booking = bookingOpt.get();

        // Verify user owns this booking
        if (!booking.getRider().getId().equals(user.getId())) {
            model.addAttribute("error", "Access denied!");
            return "redirect:/dashboard";
        }

        // Restore seats to the ride
        Ride ride = booking.getRide();
        ride.setAvailableSeats(ride.getAvailableSeats() + booking.getSeatsBooked());

        booking.setStatus("CANCELLED");

        bookingRepository.save(booking);
        rideRepository.save(ride);

        model.addAttribute("success", "Booking cancelled successfully!");
        return "redirect:/bookings/my-bookings";
    }

    // Driver: View bookings for their rides
    @GetMapping("/driver-bookings")
    public String driverBookings(HttpSession session, Model model) {
        User driver = (User) session.getAttribute("user");
        if (driver == null) return "redirect:/login";

        if (!"driver".equals(driver.getRole())) {
            model.addAttribute("error", "Access denied! Driver feature only.");
            return "redirect:/dashboard";
        }

        // Get all rides by this driver
        List<Ride> driverRides = rideRepository.findByDriverId(driver.getId());
        model.addAttribute("driverRides", driverRides);

        return "driver-bookings";
    }
}