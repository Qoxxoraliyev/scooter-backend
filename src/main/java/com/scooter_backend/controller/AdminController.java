package com.scooter_backend.controller;
import com.scooter_backend.dto.ride.RideResponseDTO;
import com.scooter_backend.dto.scooter.ScooterResponseDTO;
import com.scooter_backend.dto.scooter.ScooterStatusDTO;
import com.scooter_backend.entity.User;
import com.scooter_backend.enums.DriverStatus;
import com.scooter_backend.repository.UserRepository;
import com.scooter_backend.service.RideService;
import com.scooter_backend.service.ScooterService;
import com.scooter_backend.service.driver.DriverPresenceService;
import com.scooter_backend.service.driver.DriverService;
import com.scooter_backend.service.location.ScooterLocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ScooterService scooterService;
    private final RideService rideService;
    private final UserRepository userRepository;
    private final ScooterLocationService locationService;
    private final DriverService driverService;
    private final DriverPresenceService presenceService;
    private final PasswordEncoder passwordEncoder;

    public AdminController(ScooterService scooterService, RideService rideService, UserRepository userRepository, ScooterLocationService locationService, DriverService driverService, DriverPresenceService presenceService, PasswordEncoder passwordEncoder) {
        this.scooterService = scooterService;
        this.rideService = rideService;
        this.userRepository = userRepository;
        this.locationService = locationService;
        this.driverService = driverService;
        this.presenceService = presenceService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard() {

        Map<String, Object> data = new HashMap<>();
        data.put("totalRides", rideService.getAllRides().size());
        data.put("totalUsers", userRepository.count());
        data.put("onlineDrivers", presenceService.getOnlineDrivers().size());

        return ResponseEntity.ok(data);
    }

    @GetMapping("/statistics/weekly-rides")
    public ResponseEntity<List<RideResponseDTO>> weeklyRides() {
        return ResponseEntity.ok(rideService.getAllRides());
    }

    @GetMapping("/scooters")
    public ResponseEntity<List<ScooterResponseDTO>> getScooters() {
        return ResponseEntity.ok(scooterService.getAll());
    }

    @PatchMapping("/scooters/{id}/status")
    public ResponseEntity<Void> updateScooterStatus(
            @PathVariable Long id,
            @RequestBody ScooterStatusDTO dto
    ) {
        scooterService.updateStatus(id, dto);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/scooters/locations")
    public ResponseEntity<Map<Long, Map<String, Object>>> getLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @GetMapping("/rides")
    public ResponseEntity<List<RideResponseDTO>> getAllRides() {
        return ResponseEntity.ok(rideService.getAllRides());
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(
            @RequestParam(required = false) String role
    ) {
        List<User> users;

        if (role == null) {
            users = userRepository.findAll();
        } else {
            users = userRepository.findAll()
                    .stream()
                    .filter(u -> u.getRole().name().equalsIgnoreCase(role))
                    .toList();
        }

        return ResponseEntity.ok(users);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userRepository.save(user));
    }

    @PutMapping("/users/{id}/update")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody User updated
    ) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(updated.getFullName());
        user.setPhone(updated.getPhone());

        return ResponseEntity.ok(userRepository.save(user));
    }

    @PutMapping("/users/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long id,
            @RequestParam String password
    ) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/drivers/{id}/status")
    public ResponseEntity<Void> updateDriverStatus(
            @PathVariable Long id,
            @RequestParam DriverStatus status
    ) {
        driverService.updateDriverStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/drivers/online")
    public ResponseEntity<Set<Long>> getOnlineDrivers() {
        return ResponseEntity.ok(presenceService.getOnlineDrivers());
    }
}