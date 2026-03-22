package com.scooter_backend.service.ride;

import com.scooter_backend.dto.ride.RideCalculationDTO;
import org.springframework.stereotype.Service;

@Service
public class RideCalculationService {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double AVG_SCOOTER_SPEED_KMH = 20.0;

    public RideCalculationDTO calculateRide(
            double startLat,
            double startLon,
            double endLat,
            double endLon
    ) {
        double distanceKm = haversine(startLat, startLon, endLat, endLon);

        double distanceRounded = Math.round(distanceKm * 10.0) / 10.0;

        double durationMin = (distanceKm / AVG_SCOOTER_SPEED_KMH) * 60.0;

        long durationRounded = Math.round(durationMin);

        return new RideCalculationDTO(distanceRounded, durationRounded);
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }


}