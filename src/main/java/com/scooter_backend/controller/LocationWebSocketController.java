package com.scooter_backend.controller;

import com.scooter_backend.dto.scooter.ScooterLocationMessage;
import com.scooter_backend.service.location.LocationService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class LocationWebSocketController {

    private final LocationService locationService;

    public LocationWebSocketController(LocationService locationService) {
        this.locationService = locationService;
    }

    @MessageMapping("/location")
    public void handleLocation(ScooterLocationMessage message) {

        locationService.updateLocation(
                message.scooterId(),
                message.lat(),
                message.lon()
        );
    }
}
