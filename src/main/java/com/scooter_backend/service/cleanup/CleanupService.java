package com.scooter_backend.service.cleanup;

import com.scooter_backend.repository.DriverLocationRepository;
import com.scooter_backend.repository.OrderRequestRepository;
import com.scooter_backend.repository.RideRepository;
import com.scooter_backend.repository.ScooterLocationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CleanupService {

    private final RideRepository rideRepository;
    private final ScooterLocationRepository locationRepository;
    private final DriverLocationRepository driverLocationRepository;
    private final OrderRequestRepository orderRequestRepository;

    public CleanupService(RideRepository rideRepository, ScooterLocationRepository locationRepository, DriverLocationRepository driverLocationRepository, OrderRequestRepository orderRequestRepository) {
        this.rideRepository = rideRepository;
        this.locationRepository = locationRepository;
        this.driverLocationRepository = driverLocationRepository;
        this.orderRequestRepository = orderRequestRepository;
    }

    // 🔥 har kuni 03:00 da ishlaydi
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanOldData() {

        LocalDateTime threshold = LocalDateTime.now().minusDays(2);

        rideRepository.deleteOldRides(threshold);
        locationRepository.deleteOldLocations(threshold);
        driverLocationRepository.deleteOldDriverLocations(threshold);
        orderRequestRepository.deleteOldOrders(threshold);

        System.out.println("Old data cleaned: " + threshold);
    }

    
}