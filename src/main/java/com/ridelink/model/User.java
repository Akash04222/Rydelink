package com.ridelink.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private String role;
    private String collegeId;
    private Double averageRating = 0.0;
    private Integer totalRatings = 0;

    //THIS FIELD is for admin approval system
    private String accountStatus = "PENDING"; // PENDING, APPROVED, REJECTED

    // Constructors
    public User() {}

    public User(String name, String email, String password, String role, String collegeId) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.collegeId = collegeId;
        this.accountStatus = "PENDING"; // Default status for new users
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getCollegeId() { return collegeId; }
    public void setCollegeId(String collegeId) { this.collegeId = collegeId; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public Integer getTotalRatings() { return totalRatings; }
    public void setTotalRatings(Integer totalRatings) { this.totalRatings = totalRatings; }

    // ADD GETTER AND SETTER FOR ACCOUNT STATUS
    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }
}