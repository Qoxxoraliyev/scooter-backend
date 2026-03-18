package com.scooter_backend.mapper;

import com.scooter_backend.dto.scooter.*;
import com.scooter_backend.entity.Scooter;
import com.scooter_backend.entity.ScooterLocation;

public class ScooterMapper {

    public static ScooterResponseDTO toDTO(Scooter scooter) {
        if (scooter == null) return null;

        Double lat = null;
        Double lon = null;

        if (scooter.getLocation() != null) {
            lat = scooter.getLocation().getLatitude();
            lon = scooter.getLocation().getLongitude();
        }

        return new ScooterResponseDTO(
                scooter.getId(),
                scooter.getName(),
                scooter.getStatus(),
                scooter.getBatteryLevel(),
                scooter.getLocked(),
                scooter.getPricePerMinute(),
                lat,
                lon,
                scooter.getLastServiceDate(),
                scooter.getCreatedAt()
        );
    }

    public static Scooter toEntity(ScooterCreateDTO dto) {
        if (dto == null) return null;

        Scooter scooter = new Scooter();

        scooter.setName(dto.name());
        scooter.setStatus(dto.status());
        scooter.setBatteryLevel(dto.batteryLevel());
        scooter.setLocked(dto.isLocked());
        scooter.setPricePerMinute(dto.pricePerMinute());
        scooter.setLastServiceDate(dto.lastServiceDate());

        return scooter;
    }

    public static void updateStatus(Scooter scooter, ScooterStatusDTO dto) {
        if (scooter == null || dto == null) return;

        scooter.setStatus(dto.status());
    }

    public static ScooterLocation toLocationEntity(ScooterLocationDTO dto, Scooter scooter) {
        if (dto == null || scooter == null) return null;

        ScooterLocation location = scooter.getLocation();

        if (location == null) {
            location = new ScooterLocation();
            location.setScooter(scooter);
        }

        location.setLatitude(dto.latitude());
        location.setLongitude(dto.longitude());

        return location;
    }
}