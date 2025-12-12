package com.ridelink.service;

import com.ridelink.model.Booking;
import com.ridelink.model.Ride;
import com.ridelink.repository.BookingRepository;
import com.ridelink.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public List<Ride> getUpcomingRides(Long driverId) {
        return rideRepository.findByDriverId(driverId);
    }

    public Ride updateRide(Long rideId, Ride updatedRide) {
        Ride existingRide = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        existingRide.setStartLocation(updatedRide.getStartLocation());
        existingRide.setEndLocation(updatedRide.getEndLocation());
        existingRide.setDepartureTime(updatedRide.getDepartureTime());
        existingRide.setAvailableSeats(updatedRide.getAvailableSeats());
        existingRide.setCostPerSeat(updatedRide.getCostPerSeat());
        existingRide.setVehicleType(updatedRide.getVehicleType());
        existingRide.setVehicleNumber(updatedRide.getVehicleNumber());
        existingRide.setNotes(updatedRide.getNotes());

        return rideRepository.save(existingRide);
    }

    public void deleteRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        rideRepository.delete(ride);
    }

    public List<Booking> getRideBookings(Long rideId) {
        return bookingRepository.findByRideId(rideId);
    }

    public Ride getRideById(Long rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found with id: " + rideId));
    }
}