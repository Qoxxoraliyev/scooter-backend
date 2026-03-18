package com.scooter_backend.controller;

import com.scooter_backend.service.location.LocationService;
import com.scooter_backend.service.location.ScooterLocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    private final LocationService locationService;
    private final ScooterLocationService scooterLocationService;

    public LocationController(LocationService locationService,
                              ScooterLocationService scooterLocationService) {
        this.locationService = locationService;
        this.scooterLocationService = scooterLocationService;
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateLocation(
            @RequestParam Long scooterId,
            @RequestParam double lat,
            @RequestParam double lon
    ) {
        locationService.updateLocation(scooterId, lat, lon);
        return ResponseEntity.accepted().build(); // 202
    }

    @GetMapping("/all")
    public ResponseEntity<Map<Long, Map<String, Object>>> getAllLocations() {
        return ResponseEntity.ok(scooterLocationService.getAllLocations());
    }
}