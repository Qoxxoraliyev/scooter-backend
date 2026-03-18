package com.scooter_backend.service.Impl;
import com.scooter_backend.dto.ride.*;
import com.scooter_backend.entity.Ride;
import com.scooter_backend.entity.Scooter;
import com.scooter_backend.entity.User;
import com.scooter_backend.enums.RideStatus;
import com.scooter_backend.mapper.RideMapper;
import com.scooter_backend.repository.RideRepository;
import com.scooter_backend.repository.ScooterRepository;
import com.scooter_backend.repository.UserRepository;
import com.scooter_backend.service.RideService;
import com.scooter_backend.service.ride.RideCalculationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final ScooterRepository scooterRepository;
    private final UserRepository userRepository;
    private final RideCalculationService calculationService;

    public RideServiceImpl(RideRepository rideRepository, ScooterRepository scooterRepository, UserRepository userRepository, RideCalculationService calculationService) {
        this.rideRepository = rideRepository;
        this.scooterRepository = scooterRepository;
        this.userRepository = userRepository;
        this.calculationService = calculationService;
    }

    @Override
    public RideResponseDTO startRide(Long userId, RideStartDTO dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Scooter scooter = scooterRepository.findById(dto.scooterId())
                .orElseThrow(() -> new RuntimeException("Scooter not found"));

        if (!Boolean.TRUE.equals(scooter.getLocked())) {
            throw new RuntimeException("Scooter already unlocked");
        }

        if (scooter.getBatteryLevel() < 20) {
            throw new RuntimeException("Battery too low");
        }

        scooter.setLocked(false);

        Ride ride = new Ride();
        ride.setUser(user);
        ride.setScooter(scooter);

        ride.setStartTime(LocalDateTime.now());
        ride.setStartLat(dto.startLat());
        ride.setStartLon(dto.startLon());

        ride.setStatus(RideStatus.STARTED);
        ride.setPaid(false);

        rideRepository.save(ride);

        return RideMapper.toDTO(ride);
    }

    @Override
    public RideResponseDTO finishRide(RideFinishDTO dto) {

        Ride ride = rideRepository.findById(dto.rideId())
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (ride.getStatus() != RideStatus.STARTED) {
            throw new RuntimeException("Ride is not active");
        }

        Scooter scooter = ride.getScooter();

        ride.setEndTime(LocalDateTime.now());
        ride.setEndLat(dto.endLat());
        ride.setEndLon(dto.endLon());

        RideCalculationDTO calc = calculationService.calculateRide(
                ride.getStartLat(),
                ride.getStartLon(),
                dto.endLat(),
                dto.endLon()
        );

        ride.setDistance(calc.distanceKm());

        BigDecimal cost = scooter.getPricePerMinute()
                .multiply(BigDecimal.valueOf(calc.durationMin()))
                .setScale(2, BigDecimal.ROUND_HALF_UP);

        ride.setCost(cost);

        ride.setStatus(RideStatus.FINISHED);
        ride.setPaid(false);

        scooter.setLocked(true);

        return RideMapper.toDTO(ride);
    }

    @Override
    @Transactional(readOnly = true)
    public RideResponseDTO getById(Long rideId) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        return RideMapper.toDTO(ride);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateCost(Long scooterId, Double distance) {

        Scooter scooter = scooterRepository.findById(scooterId)
                .orElseThrow(() -> new RuntimeException("Scooter not found"));

        if (distance == null || distance <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal distanceBD = BigDecimal.valueOf(distance);

        return scooter.getPricePerMinute()
                .multiply(distanceBD)
                .setScale(2, BigDecimal.ROUND_HALF_UP);
    }


    @Override
    @Transactional(readOnly = true)
    public List<RideResponseDTO> getAllRides() {
        return rideRepository.findAll()
                .stream()
                .map(RideMapper::toDTO)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<RideResponseDTO> getUserRides(Long userId) {
        return rideRepository.findByUserId(userId)
                .stream()
                .map(RideMapper::toDTO)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<RideResponseDTO> getDriverRides(Long driverId) {
        return rideRepository.findByDriverId(driverId)
                .stream()
                .map(RideMapper::toDTO)
                .toList();
    }


    @Override
    public void cancelRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        if (ride.getStatus() != RideStatus.STARTED) {
            throw new RuntimeException("Cannot cancel");
        }
        ride.setStatus(RideStatus.CANCELLED);
        Scooter scooter = ride.getScooter();
        scooter.setLocked(true);
    }




}