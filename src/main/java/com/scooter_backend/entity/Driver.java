package com.scooter_backend.entity;

import com.scooter_backend.enums.DriverStatus;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "drivers")
public class Driver {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    private User user;

    @OneToOne
    private Scooter scooter;

    @Enumerated(EnumType.STRING)
    private DriverStatus status;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DriverLocation> locations = new ArrayList<>();

    public Driver(){}

    public Driver(Long id, User user, Scooter scooter, DriverStatus status) {
        this.id = id;
        this.user = user;
        this.scooter = scooter;
        this.status = status;
    }

    public Long getId() {
        return id;
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

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }

    public List<DriverLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<DriverLocation> locations) {
        this.locations = locations;
    }


}
