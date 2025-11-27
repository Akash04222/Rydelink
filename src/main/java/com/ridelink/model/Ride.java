package com.ridelink.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rides")
public class Ride {

    public int getBookedSeats() {
        return availableSeats; // This will be calculated from bookings
    }

    public boolean hasAvailableSeats(int requestedSeats) {
        return availableSeats >= requestedSeats;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    @Column(nullable = false)
    private String startLocation;

    @Column(nullable = false)
    private String endLocation;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private int availableSeats;

    private double costPerSeat;
    private String vehicleType;
    private String vehicleNumber;
    private String notes;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Default constructor - MAKE SURE THIS EXISTS
    public Ride() {
        this.createdAt = LocalDateTime.now();
    }

    // Parameterized constructor - MAKE SURE THIS EXISTS
    public Ride(User driver, String startLocation, String endLocation,
                LocalDateTime departureTime, int availableSeats, double costPerSeat) {
        this();
        this.driver = driver;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.departureTime = departureTime;
        this.availableSeats = availableSeats;
        this.costPerSeat = costPerSeat;
    }

    // Getters and Setters for ALL fields
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getDriver() { return driver; }
    public void setDriver(User driver) { this.driver = driver; }

    public String getStartLocation() { return startLocation; }
    public void setStartLocation(String startLocation) { this.startLocation = startLocation; }

    public String getEndLocation() { return endLocation; }
    public void setEndLocation(String endLocation) { this.endLocation = endLocation; }

    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public double getCostPerSeat() { return costPerSeat; }
    public void setCostPerSeat(double costPerSeat) { this.costPerSeat = costPerSeat; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}