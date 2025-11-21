package com.ridelink.controller;

import com.ridelink.model.User;
import com.ridelink.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

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

    // Process registration (with database)
    @PostMapping("/register")
    public String registerUser(@RequestParam String name,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String role,
                               @RequestParam String collegeId,
                               Model model) {

        // Create new user
        User user = new User(name, email, password, role, collegeId);

        // Save to database
        userRepository.save(user);

        // Show success message
        model.addAttribute("message", "Registration successful! Please login.");
        return "login";
    }

    // Show login form
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    // Process login (with database verification)
    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            Model model) {

        // Find user by email
        User user = userRepository.findByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            // Login successful
            model.addAttribute("user", user);
            return "dashboard";
        } else {
            // Login failed
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }
    }

    // Show all users (for testing)
    @GetMapping("/users")
    @ResponseBody
    public String showUsers() {
        return userRepository.findAll().toString();
    }
}