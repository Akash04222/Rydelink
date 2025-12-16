package com.ridelink.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rides")
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

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
    private String status;

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
    // UPDATE THESE METHODS
    public int getBookedSeats() {
        if (bookings == null) return 0;
        return bookings.stream()
                .filter(booking -> booking.getStatus() == Booking.BookingStatus.CONFIRMED)
                .mapToInt(Booking::getSeatsBooked)
                .sum();
    }

    public int getRemainingSeats() {
        return availableSeats - getBookedSeats();
    }

    public boolean hasAvailableSeats(int requestedSeats) {
        return getRemainingSeats() >= requestedSeats;
    }

    // Getters and Setters for ALL fields
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getDriver() { return driver; }
    public void setDriver(User driver) { this.driver = driver; }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}