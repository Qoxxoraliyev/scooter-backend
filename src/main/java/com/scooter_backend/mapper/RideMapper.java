package com.scooter_backend.mapper;

import com.scooter_backend.dto.ride.*;
import com.scooter_backend.entity.Ride;
import com.scooter_backend.entity.Scooter;
import com.scooter_backend.entity.User;
import com.scooter_backend.enums.RideStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RideMapper {


    public static RideResponseDTO toDTO(Ride ride) {
        if (ride == null) return null;

        Double distance = ride.getDistance();
        Double distanceRounded = null;

        if (distance != null) {
            distanceRounded = Math.round(distance * 10.0) / 10.0;
        }

        return new RideResponseDTO(
                ride.getId(),
                ride.getStartTime(),
                ride.getEndTime(),
                ride.getStartLat(),
                ride.getStartLon(),
                ride.getEndLat(),
                ride.getEndLon(),
                distanceRounded,
                ride.getCost(),
                ride.getStatus(),
                ride.getPaid(),
                ride.getUser().getId(),
                ride.getScooter().getId(),
                ride.getCreatedAt()
        );
    }

    public static Ride toEntity(RideStartDTO dto, User user, Scooter scooter) {
        if (dto == null) return null;

        Ride ride = new Ride();
        ride.setStartTime(LocalDateTime.now());
        ride.setStartLat(dto.startLat());
        ride.setStartLon(dto.startLon());

        ride.setStatus(RideStatus.STARTED);
        ride.setPaid(false);

        ride.setUser(user);
        ride.setScooter(scooter);

        return ride;
    }

    public static void finishRide(Ride ride, RideFinishDTO dto, BigDecimal cost) {
        if (ride == null || dto == null) return;

        ride.setEndTime(LocalDateTime.now());
        ride.setEndLat(dto.endLat());
        ride.setEndLon(dto.endLon());
        ride.setDistance(dto.distance());

        ride.setCost(cost);
        ride.setStatus(RideStatus.COMPLETED);
    }

}