package com.ridelink.controller;

import com.ridelink.model.Ride;
import com.ridelink.model.User;
import com.ridelink.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Collections;

@Controller
public class RideManagementController {

    @Autowired
    private RideRepository rideRepository;

    // SIMPLE EDIT RIDE - JUST TEST IF IT LOADS
    @GetMapping("/ride/edit/{rideId}")
    public String showEditRideForm(@PathVariable Long rideId, Model model, HttpSession session) {
        System.out.println(" EDIT RIDE ENDPOINT CALLED - ID: " + rideId);

        try {
            // Check if user is logged in
            User user = (User) session.getAttribute("user");
            if (user == null) {
                System.out.println(" User not logged in");
                return "redirect:/login";
            }
            System.out.println(" User: " + user.getName());

            // Try to find the ride
            Ride ride = rideRepository.findById(rideId).orElse(null);
            if (ride == null) {
                System.out.println(" Ride not found with ID: " + rideId);
                return "redirect:/dashboard?error=ride_not_found";
            }

            System.out.println(" Ride found: " + ride.getStartLocation() + " to " + ride.getEndLocation());

            // Add ride to model
            model.addAttribute("ride", ride);
            System.out.println(" Returning edit-ride template");
            return "edit-ride";

        } catch (Exception e) {
            System.out.println(" EXCEPTION in edit ride: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/dashboard?error=server_error";
        }
    }

    // SIMPLE VIEW BOOKINGS - JUST TEST IF IT LOADS
    @GetMapping("/ride/{rideId}/bookings")
    public String viewRideBookings(@PathVariable Long rideId, Model model, HttpSession session) {
        System.out.println(" VIEW BOOKINGS ENDPOINT CALLED - ID: " + rideId);

        try {
            // Check if user is logged in
            User user = (User) session.getAttribute("user");
            if (user == null) {
                System.out.println(" User not logged in");
                return "redirect:/login";
            }
            System.out.println(" User: " + user.getName());

            // Try to find the ride
            Ride ride = rideRepository.findById(rideId).orElse(null);
            if (ride == null) {
                System.out.println(" Ride not found with ID: " + rideId);
                return "redirect:/dashboard?error=ride_not_found";
            }

            System.out.println(" Ride found: " + ride.getStartLocation() + " to " + ride.getEndLocation());

            // For now, just pass empty bookings
            model.addAttribute("ride", ride);
            model.addAttribute("bookings", Collections.emptyList());
            System.out.println(" Returning ride-bookings template with empty bookings");
            return "ride-bookings";

        } catch (Exception e) {
            System.out.println(" EXCEPTION in view bookings: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/dashboard?error=server_error";
        }
    }

    // SIMPLE RIDE UPDATE
    @PostMapping("/ride/update/{rideId}")
    public String updateRide(@PathVariable Long rideId,
                             @RequestParam String startLocation,
                             @RequestParam String endLocation,
                             @RequestParam String departureTime,
                             @RequestParam int availableSeats,
                             @RequestParam double costPerSeat,
                             HttpSession session) {

        System.out.println("UPDATE RIDE CALLED - ID: " + rideId);

        try {
            User user = (User) session.getAttribute("user");
            if (user == null) return "redirect:/login";

            Ride ride = rideRepository.findById(rideId).orElse(null);
            if (ride != null) {
                // Update basic fields only
                ride.setStartLocation(startLocation);
                ride.setEndLocation(endLocation);
                ride.setAvailableSeats(availableSeats);
                ride.setCostPerSeat(costPerSeat);

                rideRepository.save(ride);
                System.out.println("Ride updated successfully");
                return "redirect:/dashboard?updateSuccess=true";
            }

            System.out.println(" Ride not found for update");
            return "redirect:/dashboard?error=ride_not_found";

        } catch (Exception e) {
            System.out.println(" Error updating ride: " + e.getMessage());
            return "redirect:/dashboard?updateError=" + e.getMessage();
        }
    }
}