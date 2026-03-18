package com.scooter_backend.controller;

import com.scooter_backend.dto.ride.*;
import com.scooter_backend.service.RideService;
import com.scooter_backend.service.ride.RideCalculationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;

    private final RideCalculationService calculationService;


    public RideController(RideService rideService, RideCalculationService calculationService) {
        this.rideService = rideService;
        this.calculationService = calculationService;
    }

    @PostMapping("/start")
    public ResponseEntity<RideResponseDTO> startRide(
            @RequestParam Long userId,
            @RequestBody RideStartDTO dto
    ) {
        RideResponseDTO response = rideService.startRide(userId, dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/finish")
    public ResponseEntity<RideResponseDTO> finishRide(
            @RequestBody RideFinishDTO dto
    ) {
        RideResponseDTO response = rideService.finishRide(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel/{rideId}")
    public ResponseEntity<Void> cancelRide(
            @PathVariable Long rideId
    ) {
        rideService.cancelRide(rideId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{rideId}")
    public ResponseEntity<RideResponseDTO> getById(
            @PathVariable Long rideId
    ) {
        RideResponseDTO response = rideService.getById(rideId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/calculate")
    public ResponseEntity<BigDecimal> calculateCost(
            @RequestParam Long scooterId,
            @RequestParam Double distance
    ) {
        BigDecimal cost = rideService.calculateCost(scooterId, distance);
        return ResponseEntity.ok(cost);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RideResponseDTO>> getAllRides() {
        return ResponseEntity.ok(rideService.getAllRides());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RideResponseDTO>> getUserRides(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(rideService.getUserRides(userId));
    }

    @PostMapping("/calculate")
    public ResponseEntity<RideCalculationDTO> calculate(
            @RequestBody RideCalculationRequestDTO dto
    ) {
        return ResponseEntity.ok(
                calculationService.calculateRide(
                        dto.startLat(),
                        dto.startLon(),
                        dto.endLat(),
                        dto.endLon()
                )
        );
    }


    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<RideResponseDTO>> getDriverRides(
            @PathVariable Long driverId
    ) {
        return ResponseEntity.ok(rideService.getDriverRides(driverId));
    }


}