package com.scooter_backend.controller;
import com.scooter_backend.dto.ride.RideResponseDTO;
import com.scooter_backend.dto.scooter.ScooterResponseDTO;
import com.scooter_backend.dto.scooter.ScooterStatusDTO;
import com.scooter_backend.dto.user.UserResponseDTO;
import com.scooter_backend.dto.user.UserUpdateDTO;
import com.scooter_backend.entity.User;
import com.scooter_backend.enums.DriverStatus;
import com.scooter_backend.mapper.UserMapper;
import com.scooter_backend.repository.UserRepository;
import com.scooter_backend.service.RideService;
import com.scooter_backend.service.ScooterService;
import com.scooter_backend.service.UserService;
import com.scooter_backend.service.driver.DriverPresenceService;
import com.scooter_backend.service.driver.DriverService;
import com.scooter_backend.service.location.ScooterLocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
    private final UserService userService;

    public AdminController(ScooterService scooterService, RideService rideService, UserRepository userRepository, ScooterLocationService locationService, DriverService driverService, DriverPresenceService presenceService, PasswordEncoder passwordEncoder, UserService userService) {
        this.scooterService = scooterService;
        this.rideService = rideService;
        this.userRepository = userRepository;
        this.locationService = locationService;
        this.driverService = driverService;
        this.presenceService = presenceService;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
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

    @PutMapping("/scooters/{id}/price")
    public ResponseEntity<ScooterResponseDTO> updateScooterPrice(
            @PathVariable Long id,
            @RequestParam BigDecimal pricePerKm
    ) {
        return ResponseEntity.ok(scooterService.updatePricePerKm(id, pricePerKm));
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
    public ResponseEntity<List<UserResponseDTO>> getUsers(
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

        List<UserResponseDTO> response = users.stream()
                .map(UserMapper::toDTO)
                .toList();

        return ResponseEntity.ok(response);
    }



    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userRepository.save(user));
    }

    @PutMapping("/users/{id}/update")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateDTO dto
    ) {
        return ResponseEntity.ok(userService.update(id,dto));
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

    @GetMapping("/scooters/active/count")
    public ResponseEntity<Long> activeScootersCount() {
        return ResponseEntity.ok(scooterService.countActive());
    }

    @GetMapping("/scooters/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(scooterService.countAll());
    }

    @GetMapping("/rides/today/count")
    public ResponseEntity<Long> todayRides() {
        return ResponseEntity.ok(rideService.countTodayRides());
    }

    @GetMapping("/scooters/{id}/today-rides")
    public ResponseEntity<Long> scooterTodayRides(@PathVariable Long id) {
        return ResponseEntity.ok(rideService.countTodayRidesByScooter(id));
    }

}