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

    //  This maps to: /admin/dashboard
    @GetMapping("/dashboard")
    public String adminDashboard(Model model, HttpSession session) {
        try {
            System.out.println("=== ADMIN DASHBOARD ===");

            // Check if user is logged in via session
            User user = (User) session.getAttribute("user");
            System.out.println("Session user: " + (user != null ? user.getEmail() : "NULL"));

            if (user == null) {
                System.out.println("No user in session");
                return "redirect:/login?error=Please login first";
            }

            System.out.println("User role: " + user.getRole());

            // Check if user has ADMIN role
            if (!"ADMIN".equals(user.getRole())) {
                System.out.println("User is NOT admin, role is: " + user.getRole());
                return "redirect:/login?error=Access denied. Admin only.";
            }

            System.out.println("User is admin, loading data...");

            // Get data
            List<User> allUsers = userRepository.findAll();
            List<User> pendingUsers = userRepository.findByAccountStatus("PENDING");
            List<Ride> allRides = rideRepository.findAll();

            System.out.println("Data loaded:");
            System.out.println("  - Total users: " + allUsers.size());
            System.out.println("  - Pending users: " + pendingUsers.size());
            System.out.println("  - Total rides: " + allRides.size());

            model.addAttribute("allUsers", allUsers);
            model.addAttribute("pendingUsers", pendingUsers);
            model.addAttribute("allRides", allRides);
            model.addAttribute("currentUser", user);

            System.out.println("Loading admin-dashboard template");
            return "admin-dashboard";

        } catch (Exception e) {
            System.err.println("=== ADMIN DASHBOARD ERROR ===");
            e.printStackTrace();
            model.addAttribute("error", "Unable to load dashboard: " + e.getMessage());
            return "admin-dashboard";
        }
    }

    // This maps to: /admin/rides
    @GetMapping("/rides")
    public String allRides(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login?error=Unauthorized";
        }

        try {
            List<Ride> rides = Optional.of(rideRepository.findAll()).orElse(new ArrayList<>());
            model.addAttribute("rides", rides);
            return "admin-rides";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("rides", new ArrayList<>());
            model.addAttribute("error", "Error loading rides data");
            return "admin-rides";
        }
    }

    @PostMapping("/approve-user/{id}")
    public String approveUser(@PathVariable Long id, HttpSession session) {
        User admin = (User) session.getAttribute("user");
        if (admin == null || !"ADMIN".equalsIgnoreCase(admin.getRole())) {
            return "redirect:/login?error=Unauthorized";
        }

        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setAccountStatus("APPROVED");
                userRepository.save(user);
                return "redirect:/admin/dashboard?success=user_approved";
            } else {
                return "redirect:/admin/dashboard?error=user_not_found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/dashboard?error=approval_failed";
        }
    }

    @PostMapping("/reject-user/{id}")
    public String rejectUser(@PathVariable Long id, HttpSession session) {
        User admin = (User) session.getAttribute("user");
        if (admin == null || !"ADMIN".equalsIgnoreCase(admin.getRole())) {
            return "redirect:/login?error=Unauthorized";
        }

        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setAccountStatus("REJECTED");
                userRepository.save(user);
                return "redirect:/admin/dashboard?success=user_rejected";
            } else {
                return "redirect:/admin/dashboard?error=user_not_found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/dashboard?error=rejection_failed";
        }
    }

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