package com.ridelink.service;

import com.ridelink.model.Rating;
import com.ridelink.model.User;
import com.ridelink.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserService userService;

    public Rating submitRating(Long riderId, Long driverId, Integer rating, String comment) {
        User rider = userService.findById(riderId);
        User driver = userService.findById(driverId);

        if (ratingRepository.existsByRiderAndDriver(rider, driver)) {
            throw new RuntimeException("You have already rated this driver");
        }

        Rating newRating = new Rating(rider, driver, rating, comment);
        Rating savedRating = ratingRepository.save(newRating);

        updateDriverRating(driver);

        return savedRating;
    }

    private void updateDriverRating(User driver) {
        Double averageRating = ratingRepository.findAverageRatingByDriver(driver);
        Integer totalRatings = ratingRepository.countRatingsByDriver(driver);

        driver.setAverageRating(averageRating != null ? averageRating : 0.0);
        driver.setTotalRatings(totalRatings != null ? totalRatings : 0);
        userService.saveUser(driver);
    }

    public List<Rating> getDriverRatings(Long driverId) {
        User driver = userService.findById(driverId);
        return ratingRepository.findByDriver(driver);
    }
}