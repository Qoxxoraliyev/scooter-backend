package com.scooter_backend.dto.order;

public record OrderAcceptedEventDTO(

        Long orderId,
        Long driverId

) {}
