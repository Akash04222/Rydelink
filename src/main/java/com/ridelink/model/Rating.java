package com.ridelink.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rider_id", nullable = false)
    private User rider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    @Column(nullable = false)
    private Integer rating; // 1-5 stars

    private String comment;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Constructors
    public Rating() {
        this.createdAt = LocalDateTime.now();
    }

    public Rating(User rider, User driver, Integer rating, String comment) {
        this();
        this.rider = rider;
        this.driver = driver;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getRider() { return rider; }
    public void setRider(User rider) { this.rider = rider; }

    public User getDriver() { return driver; }
    public void setDriver(User driver) { this.driver = driver; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}