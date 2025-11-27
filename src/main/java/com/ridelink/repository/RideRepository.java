package com.ridelink.repository;

import com.ridelink.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {

    // Find rides by driver ID
    List<Ride> findByDriverId(Long driverId);

    // Find upcoming rides
    List<Ride> findByDepartureTimeAfter(LocalDateTime departureTime);

    // Search rides with location and date
    @Query("SELECT r FROM Ride r WHERE " +
            "LOWER(r.startLocation) LIKE LOWER(CONCAT('%', :from, '%')) AND " +
            "LOWER(r.endLocation) LIKE LOWER(CONCAT('%', :to, '%')) AND " +
            "r.departureTime >= :date AND " +
            "r.availableSeats > 0")
    List<Ride> searchRides(@Param("from") String from,
                           @Param("to") String to,
                           @Param("date") LocalDateTime date);

    // Find rides with available seats
    List<Ride> findByAvailableSeatsGreaterThan(int seats);
}