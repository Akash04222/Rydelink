package com.ridelink.repository;

import com.ridelink.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Find bookings by rider
    List<Booking> findByRiderId(Long riderId);

    // Find bookings for a specific ride
    List<Booking> findByRideId(Long rideId);

    // Find pending bookings for a ride
    List<Booking> findByRideIdAndStatus(Long rideId, String status);

    // Find bookings by rider with status
    List<Booking> findByRiderIdAndStatus(Long riderId, String status);

    // Count confirmed bookings for a ride
    @Query("SELECT COALESCE(SUM(b.seatsBooked), 0) FROM Booking b WHERE b.ride.id = :rideId AND b.status = 'CONFIRMED'")
    int countConfirmedSeatsByRideId(@Param("rideId") Long rideId);
}