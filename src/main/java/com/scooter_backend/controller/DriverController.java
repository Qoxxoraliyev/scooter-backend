package com.scooter_backend.controller;

import com.scooter_backend.enums.DriverStatus;
import com.scooter_backend.service.driver.DriverPresenceService;
import com.scooter_backend.service.driver.DriverService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/driver")
public class DriverController {

    private final DriverService driverService;
    private final DriverPresenceService presenceService;

    public DriverController(DriverService driverService,
                            DriverPresenceService presenceService) {
        this.driverService = driverService;
        this.presenceService = presenceService;
    }

    @PostMapping("/{driverId}/online")
    public ResponseEntity<Void> setOnline(
            @PathVariable Long driverId
    ) {
        presenceService.setOnline(driverId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{driverId}/offline")
    public ResponseEntity<Void> setOffline(
            @PathVariable Long driverId
    ) {
        presenceService.setOffline(driverId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/online")
    public ResponseEntity<Set<Long>> getOnlineDrivers() {
        return ResponseEntity.ok(presenceService.getOnlineDrivers());
    }

    @PatchMapping("/{driverId}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long driverId,
            @RequestParam DriverStatus status
    ) {
        driverService.updateDriverStatus(driverId, status);
        return ResponseEntity.noContent().build();
    }
}