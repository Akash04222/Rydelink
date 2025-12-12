package com.ridelink.controller;

import com.ridelink.model.Ride;
import com.ridelink.model.User;
import com.ridelink.repository.RideRepository;
import com.ridelink.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RideRepository rideRepository;

    // Home page
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // Show registration form
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // Process registration
    @PostMapping("/register")
    public String registerUser(@RequestParam String name,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String role,
                               @RequestParam String collegeId,
                               Model model) {
        // Check if user already exists
        User existingUser = userRepository.findByEmail(email);
        if (existingUser != null) {
            model.addAttribute("error", "User with this email already exists!");
            return "register";
        }

        // Create new user with PENDING status
        User user = new User(name, email, password, role, collegeId);
        user.setAccountStatus("PENDING"); // All new accounts need approval
        userRepository.save(user);

        model.addAttribute("message", "Registration successful! Your account is pending admin approval.");
        return "login";
    }

    // Show login form
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    // Process login - FIXED: Added admin redirect
    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {
        try {
            System.out.println("=== LOGIN ATTEMPT ===");
            System.out.println("Email: " + email);

            // Find user by email
            User user = userRepository.findByEmail(email);
            System.out.println("User found: " + (user != null ? "YES" : "NO"));

            if (user == null) {
                System.out.println("ERROR: User not found with email: " + email);
                model.addAttribute("error", "Invalid email or password");
                return "login";
            }

            System.out.println("User details:");
            System.out.println("  - ID: " + user.getId());
            System.out.println("  - Name: " + user.getName());
            System.out.println("  - Role: " + user.getRole());
            System.out.println("  - Status: " + user.getAccountStatus());
            System.out.println("  - Password in DB: " + user.getPassword());
            System.out.println("  - Password entered: " + password);

            // Check password
            if (!user.getPassword().equals(password)) {
                System.out.println("ERROR: Password mismatch");
                model.addAttribute("error", "Invalid email or password");
                return "login";
            }

            // Check account status
            String status = user.getAccountStatus();
            System.out.println("Account status: " + status);

            if (status == null) {
                System.out.println("WARNING: Account status is null, setting to PENDING");
                status = "PENDING";
                user.setAccountStatus(status);
                userRepository.save(user);
            }

            if ("REJECTED".equals(status)) {
                System.out.println("ERROR: Account is REJECTED");
                model.addAttribute("error", "Your account has been rejected. Please contact admin.");
                return "login";
            }

            if ("PENDING".equals(status)) {
                System.out.println("ERROR: Account is PENDING");
                model.addAttribute("error", "Your account is pending approval. Please wait for admin approval.");
                return "login";
            }

            // Store user in session
            System.out.println("Setting user in session");
            session.setAttribute("user", user);

            // Redirect based on role
            System.out.println("User role: " + user.getRole());

            if ("ADMIN".equals(user.getRole())) {
                System.out.println("Redirecting to ADMIN dashboard");
                return "redirect:/admin/dashboard";
            }

            System.out.println("Redirecting to regular dashboard");
            return "redirect:/dashboard";

        } catch (Exception e) {
            System.err.println("=== LOGIN EXCEPTION ===");
            e.printStackTrace();
            model.addAttribute("error", "Login error: " + e.getMessage());
            return "login";
        }
    }
    // Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);

        if ("driver".equals(user.getRole())) {
            // Show driver's rides
            List<Ride> myRides = rideRepository.findByDriverId(user.getId());
            model.addAttribute("myRides", myRides);
        } else if ("rider".equals(user.getRole())) {
            // Show available rides for riders
            List<Ride> availableRides = rideRepository.findByDepartureTimeAfter(LocalDateTime.now());
            model.addAttribute("availableRides", availableRides);
        } else if ("ADMIN".equals(user.getRole())) {
            // Admin dashboard - redirect to admin panel
            return "redirect:/admin/dashboard";
        }

        return "dashboard";
    }

    // Show create ride form
    @GetMapping("/rides/create")
    public String showCreateRideForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        if (!"driver".equals(user.getRole())) {
            return "redirect:/dashboard";
        }

        model.addAttribute("ride", new Ride());
        return "create-ride";
    }

    // Process ride creation
    @PostMapping("/rides/create")
    public String createRide(@RequestParam String startLocation,
                             @RequestParam String endLocation,
                             @RequestParam String departureTime,
                             @RequestParam int availableSeats,
                             @RequestParam double costPerSeat,
                             @RequestParam(required = false) String vehicleType,
                             @RequestParam(required = false) String vehicleNumber,
                             @RequestParam(required = false) String notes,
                             HttpSession session,
                             Model model) {

        User driver = (User) session.getAttribute("user");
        if (driver == null) return "redirect:/login";

        try {
            System.out.println("Creating ride with data:");
            System.out.println("Start: " + startLocation);
            System.out.println("End: " + endLocation);
            System.out.println("Time: " + departureTime);
            System.out.println("Seats: " + availableSeats);
            System.out.println("Cost: " + costPerSeat);
            System.out.println("Driver: " + driver.getName());

            // Parse departure time
            LocalDateTime departureDateTime = LocalDateTime.parse(departureTime);
            System.out.println("Parsed time: " + departureDateTime);

            // Create new ride
            Ride ride = new Ride();
            ride.setDriver(driver);
            ride.setStartLocation(startLocation);
            ride.setEndLocation(endLocation);
            ride.setDepartureTime(departureDateTime);
            ride.setAvailableSeats(availableSeats);
            ride.setCostPerSeat(costPerSeat);
            ride.setVehicleType(vehicleType);
            ride.setVehicleNumber(vehicleNumber);
            ride.setNotes(notes);

            System.out.println("Saving ride to database...");
            rideRepository.save(ride);
            System.out.println("Ride saved successfully!");

            return "redirect:/dashboard?success=ride_created";

        } catch (Exception e) {
            System.out.println("ERROR creating ride: " + e.getMessage());
            e.printStackTrace();

            // Add error message and return to form
            model.addAttribute("error", "Failed to create ride: " + e.getMessage());
            model.addAttribute("startLocation", startLocation);
            model.addAttribute("endLocation", endLocation);
            model.addAttribute("departureTime", departureTime);
            model.addAttribute("availableSeats", availableSeats);
            model.addAttribute("costPerSeat", costPerSeat);
            model.addAttribute("vehicleType", vehicleType);
            model.addAttribute("vehicleNumber", vehicleNumber);
            model.addAttribute("notes", notes);

            return "create-ride";
        }
    }

    // Search rides
    @GetMapping("/rides/search")
    public String searchRides(@RequestParam String from,
                              @RequestParam String to,
                              @RequestParam String date,
                              Model model) {
        try {
            LocalDateTime searchDate = LocalDateTime.parse(date + "T00:00:00");
            List<Ride> rides = rideRepository.searchRides(from, to, searchDate);
            model.addAttribute("searchResults", rides);
            model.addAttribute("searchFrom", from);
            model.addAttribute("searchTo", to);
            model.addAttribute("searchDate", date);
        } catch (Exception e) {
            model.addAttribute("error", "Invalid search parameters");
        }
        return "search-results";
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        // Invalidate session
        request.getSession().invalidate();
        return "redirect:/login";
    }
}