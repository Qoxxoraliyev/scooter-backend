package com.scooter_backend.mapper;

import com.scooter_backend.dto.location.LocationDTO;
import com.scooter_backend.entity.ScooterLocation;

public class LocationMapper {

    public static LocationDTO toDTO(ScooterLocation location) {
        if (location == null) return null;

        return new LocationDTO(
                location.getLatitude(),
                location.getLongitude()
        );
    }
}