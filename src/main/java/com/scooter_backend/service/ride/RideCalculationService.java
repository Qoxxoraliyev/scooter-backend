package com.scooter_backend.service.ride;

import com.scooter_backend.dto.ride.RideCalculationDTO;
import com.scooter_backend.dto.yandex.YandexDistanceResponse;
import com.scooter_backend.service.yandex.YandexRoutingService;
import org.springframework.stereotype.Service;

@Service
public class RideCalculationService {

    private final YandexRoutingService routingService;

    public RideCalculationService(YandexRoutingService routingService) {
        this.routingService = routingService;
    }

    public RideCalculationDTO calculateRide(
            double startLat,
            double startLon,
            double endLat,
            double endLon
    ) {

        YandexDistanceResponse response =
                routingService.getDistanceAndTime(
                        startLat, startLon,
                        endLat, endLon
                );

        double distanceKm = response.distanceMeters() / 1000.0;
        double durationMin = response.durationSeconds() / 60.0;

        return new RideCalculationDTO(distanceKm, durationMin);
    }
}