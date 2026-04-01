package com.scooter_backend.controller;

import com.scooter_backend.dto.driver.DriverLocationUpdateDTO;
import com.scooter_backend.service.driver.DriverLocationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/driver")
public class DriverLocationController {

    private final DriverLocationService driverLocationService;

    public DriverLocationController(DriverLocationService driverLocationService) {
        this.driverLocationService = driverLocationService;
    }

    @PostMapping("/location")
    public ResponseEntity<Void> updateLocation(
            @RequestParam Long driverId,
            @Valid @RequestBody DriverLocationUpdateDTO dto
    ) {
        driverLocationService.updateDriverLocation(driverId, dto);
        return ResponseEntity.accepted().build();
    }


}
