package com.scooter_backend.entity;

import com.scooter_backend.enums.RideStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rides")
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @PastOrPresent(message = "Start time cannot be in the future")
    private LocalDateTime startTime;

    @PastOrPresent(message = "End time cannot be in the future")
    private LocalDateTime endTime;

    // 📍 START LOCATION
    @NotNull(message = "Start latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    @Column(nullable = false)
    private Double startLat;

    @NotNull(message = "Start longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    @Column(nullable = false)
    private Double startLon;

    // 📍 END LOCATION (ride tugaganda keladi → optional)
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private Double endLat;

    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private Double endLon;

    // 📏 DISTANCE
    @PositiveOrZero(message = "Distance cannot be negative")
    private Double distance; // km

    // 💰 COST
    @PositiveOrZero(message = "Cost cannot be negative")
    private BigDecimal cost;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status;

    @Column(nullable = false)
    private Boolean paid = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Auto set createdAt
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scooter_id", nullable = false)
    @NotNull
    private Scooter scooter;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private User driver;

    // Business validation (important)
    @PreUpdate
    public void validateRide() {
        if (endTime != null && startTime != null && endTime.isBefore(startTime)) {
            throw new RuntimeException("End time cannot be before start time");
        }
    }

    public Ride(){}

    public Ride(Long id, LocalDateTime startTime, LocalDateTime endTime, Double startLat, Double startLon, Double endLat, Double endLon, Double distance, BigDecimal cost, RideStatus status, Boolean paid, LocalDateTime createdAt, User user, Scooter scooter, User driver) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startLat = startLat;
        this.startLon = startLon;
        this.endLat = endLat;
        this.endLon = endLon;
        this.distance = distance;
        this.cost = cost;
        this.status = status;
        this.paid = paid;
        this.createdAt = createdAt;
        this.user = user;
        this.scooter = scooter;
        this.driver = driver;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Double getStartLat() {
        return startLat;
    }

    public void setStartLat(Double startLat) {
        this.startLat = startLat;
    }

    public Double getStartLon() {
        return startLon;
    }

    public void setStartLon(Double startLon) {
        this.startLon = startLon;
    }

    public Double getEndLat() {
        return endLat;
    }

    public void setEndLat(Double endLat) {
        this.endLat = endLat;
    }

    public Double getEndLon() {
        return endLon;
    }

    public void setEndLon(Double endLon) {
        this.endLon = endLon;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Scooter getScooter() {
        return scooter;
    }

    public void setScooter(Scooter scooter) {
        this.scooter = scooter;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

}