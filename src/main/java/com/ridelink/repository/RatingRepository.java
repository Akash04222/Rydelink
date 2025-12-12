package com.ridelink.repository;

import com.ridelink.model.Rating;
import com.ridelink.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByDriver(User driver);
    boolean existsByRiderAndDriver(User rider, User driver);

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.driver = :driver")
    Double findAverageRatingByDriver(User driver);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.driver = :driver")
    Integer countRatingsByDriver(User driver);
}