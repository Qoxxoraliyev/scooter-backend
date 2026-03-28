package com.scooter_backend.entity;

import com.scooter_backend.enums.ScooterStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "scooters")
public class Scooter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScooterStatus status;

    @NotNull(message = "Battery level is required")
    @Min(value = 0, message = "Battery cannot be less than 0")
    @Max(value = 100, message = "Battery cannot be more than 100")
    @Column(nullable = false)
    private Integer batteryLevel;

    @NotNull(message = "Lock status is required")
    @Column(nullable = false)
    private Boolean isLocked;

    @NotNull(message = "Price per km is required")
    @Positive(message = "Price must be positive")
    @Column(nullable = false)
    private BigDecimal pricePerKm;

    @PastOrPresent(message = "Service date cannot be in the future")
    private LocalDateTime lastServiceDate;

    @Column(nullable = false)
    private Boolean deleted = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "scooter")
    private List<Ride> rides;

    @OneToOne(mappedBy = "scooter", cascade = CascadeType.ALL,orphanRemoval = true)
    private ScooterLocation location;

    @OneToOne(mappedBy = "scooter")
    private Driver driver;

    //  Auto set createdAt
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Scooter(){}

    public Scooter(Long id, String name, ScooterStatus status, Integer batteryLevel, Boolean isLocked, BigDecimal pricePerKm, LocalDateTime lastServiceDate, Boolean deleted, LocalDateTime createdAt, List<Ride> rides, ScooterLocation location, Driver driver) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.batteryLevel = batteryLevel;
        this.isLocked = isLocked;
        this.pricePerKm = pricePerKm;
        this.lastServiceDate = lastServiceDate;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.rides = rides;
        this.location = location;
        this.driver = driver;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ScooterStatus getStatus() {
        return status;
    }

    public void setStatus(ScooterStatus status) {
        this.status = status;
    }

    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public Boolean getLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public BigDecimal getPricePerKm() {
        return pricePerKm;
    }

    public void setPricePerKm(BigDecimal pricePerKm) {
        this.pricePerKm = pricePerKm;
    }

    public LocalDateTime getLastServiceDate() {
        return lastServiceDate;
    }

    public void setLastServiceDate(LocalDateTime lastServiceDate) {
        this.lastServiceDate = lastServiceDate;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Ride> getRides() {
        return rides;
    }

    public void setRides(List<Ride> rides) {
        this.rides = rides;
    }

    public ScooterLocation getLocation() {
        return location;
    }

    public void setLocation(ScooterLocation location) {
        this.location = location;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

}