package com.ridelink.controller;

import com.ridelink.model.Rating;
import com.ridelink.model.User;
import com.ridelink.service.RatingService;
import com.ridelink.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private UserService userService;

    // Show rating form
    @GetMapping("/rate/driver/{driverId}")
    public String showRatingForm(@PathVariable Long driverId, Model model, HttpSession session) {
        User rider = (User) session.getAttribute("user");
        if (rider == null) return "redirect:/login";

        User driver = userService.findById(driverId);

        model.addAttribute("driver", driver);
        return "rating-form";
    }

    // Submit rating
    @PostMapping("/rate/driver/{driverId}")
    public String submitRating(@PathVariable Long driverId,
                               @RequestParam Integer rating,
                               @RequestParam String comment,
                               HttpSession session) {
        User rider = (User) session.getAttribute("user");
        if (rider == null) return "redirect:/login";

        try {
            ratingService.submitRating(rider.getId(), driverId, rating, comment);
            return "redirect:/dashboard?ratingSuccess=true";
        } catch (Exception e) {
            return "redirect:/dashboard?ratingError=" + e.getMessage();
        }
    }

    // View driver ratings
    @GetMapping("/driver/{driverId}/ratings")
    public String viewDriverRatings(@PathVariable Long driverId, Model model) {
        User driver = userService.findById(driverId);
        List<Rating> ratings = ratingService.getDriverRatings(driverId);

        model.addAttribute("driver", driver);
        model.addAttribute("ratings", ratings);
        return "driver-ratings";
    }
}