package com.ridelink.controller;

import com.ridelink.model.User;
import com.ridelink.model.Ride;
import com.ridelink.repository.UserRepository;
import com.ridelink.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RideRepository rideRepository;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model, HttpSession session) {
        try {
            System.out.println("=== ADMIN DASHBOARD ===");

            // Check if user is logged in via session
            Object sessionObj = session.getAttribute("user");
            if (!(sessionObj instanceof User)) {
                return "redirect:/login?error=Please login first";
            }

            User user = (User) sessionObj;

            // Check if user has ADMIN role
            if (user.getRole() == null || !"ADMIN".equals(user.getRole())) {
                return "redirect:/login?error=Access denied. Admin only.";
            }

            // Get data
            List<User> allUsers = userRepository.findAll();
            List<User> pendingUsers = userRepository.findByAccountStatus("PENDING");
            List<Ride> allRides = rideRepository.findAll();

            // Calculate driver count
            long driverCount = 0;
            if (allUsers != null) {
                driverCount = allUsers.stream()
                        .filter(u -> u != null && "DRIVER".equalsIgnoreCase(u.getRole()))
                        .count();
            }

            // Get recent rides (last 5)
            List<Ride> recentRides = new ArrayList<>();
            if (allRides != null && !allRides.isEmpty()) {
                int limit = Math.min(allRides.size(), 5);
                recentRides = allRides.subList(0, limit);
            }

            model.addAttribute("allUsers", allUsers != null ? allUsers : new ArrayList<>());
            model.addAttribute("pendingUsers", pendingUsers != null ? pendingUsers : new ArrayList<>());
            model.addAttribute("allRides", allRides != null ? allRides : new ArrayList<>());
            model.addAttribute("recentRides", recentRides);
            model.addAttribute("driverCount", driverCount);
            model.addAttribute("currentUser", user);

            return "admin-dashboard-fixed";

        } catch (Exception e) {
            System.err.println("=== ERROR ===");
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    // Improved Approve User method
    @PostMapping("/approve-user/{id}")
    public String approveUser(@PathVariable Long id,
                              @RequestParam(required = false) String redirect,
                              HttpSession session) {
        try {
            User admin = (User) session.getAttribute("user");
            if (admin == null || !"ADMIN".equalsIgnoreCase(admin.getRole())) {
                return "redirect:/login?error=Unauthorized";
            }

            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setAccountStatus("APPROVED");
                userRepository.save(user);
                System.out.println("Approved user: " + user.getEmail());

                // Redirect based on parameter or default to dashboard
                if ("rides".equals(redirect)) {
                    return "redirect:/admin/rides?success=user_approved";
                } else {
                    return "redirect:/admin/dashboard?success=user_approved";
                }
            } else {
                return "redirect:/admin/dashboard?error=user_not_found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/dashboard?error=approval_failed";
        }
    }

    // Improved Reject User method
    @PostMapping("/reject-user/{id}")
    public String rejectUser(@PathVariable Long id,
                             @RequestParam(required = false) String redirect,
                             HttpSession session) {
        try {
            User admin = (User) session.getAttribute("user");
            if (admin == null || !"ADMIN".equalsIgnoreCase(admin.getRole())) {
                return "redirect:/login?error=Unauthorized";
            }

            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setAccountStatus("REJECTED");
                userRepository.save(user);
                System.out.println("Rejected user: " + user.getEmail());

                if ("rides".equals(redirect)) {
                    return "redirect:/admin/rides?success=user_rejected";
                } else {
                    return "redirect:/admin/dashboard?success=user_rejected";
                }
            } else {
                return "redirect:/admin/dashboard?error=user_not_found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/dashboard?error=rejection_failed";
        }
    }

    // FIXED: Rides Page WITHOUT filtering methods that don't exist
    @GetMapping("/rides")
    public String allRides(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login?error=Unauthorized";
        }

        try {
            // Get all rides (simple - no filtering for now)
            List<Ride> rides = rideRepository.findAll();

            // Get pending users for sidebar
            List<User> pendingUsers = userRepository.findByAccountStatus("PENDING");

            model.addAttribute("rides", rides != null ? rides : new ArrayList<>());
            model.addAttribute("pendingUsers", pendingUsers);
            model.addAttribute("currentUser", user);

            return "admin-rides";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("rides", new ArrayList<>());
            model.addAttribute("error", "Error loading rides data: " + e.getMessage());
            return "admin-rides";
        }
    }

    // View Single Ride Details
    @GetMapping("/ride/{id}")
    public String viewRide(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login?error=Unauthorized";
        }

        try {
            Optional<Ride> rideOptional = rideRepository.findById(id);
            if (rideOptional.isPresent()) {
                Ride ride = rideOptional.get();

                model.addAttribute("ride", ride);
                model.addAttribute("currentUser", user);

                return "admin-ride-details";
            } else {
                return "redirect:/admin/rides?error=ride_not_found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/rides?error=load_failed";
        }
    }

    // Delete Ride
    @PostMapping("/delete-ride/{id}")
    public String deleteRide(@PathVariable Long id, HttpSession session) {
        User admin = (User) session.getAttribute("user");
        if (admin == null || !"ADMIN".equalsIgnoreCase(admin.getRole())) {
            return "redirect:/login?error=Unauthorized";
        }

        try {
            if (rideRepository.existsById(id)) {
                rideRepository.deleteById(id);
                return "redirect:/admin/rides?success=ride_deleted";
            } else {
                return "redirect:/admin/rides?error=ride_not_found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/rides?error=delete_failed";
        }
    }
}