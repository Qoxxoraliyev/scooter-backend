package com.scooter_backend.websocket;

import com.scooter_backend.dto.scooter.ScooterLocationDTO;
import com.scooter_backend.dto.scooter.ScooterResponseDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ScooterSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public ScooterSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    private static final String LOCATION_TOPIC = "/topic/scooters";
    private static final String STATUS_TOPIC = "/topic/scooters/status";

    public void sendLocationUpdate(ScooterLocationDTO dto) {

        messagingTemplate.convertAndSend(
                LOCATION_TOPIC,
                dto
        );
    }

    public void sendStatusUpdate(ScooterResponseDTO dto) {

        messagingTemplate.convertAndSend(
                STATUS_TOPIC,
                dto
        );
    }

    public void sendScooterUpdate(ScooterResponseDTO dto) {

        messagingTemplate.convertAndSend(
                LOCATION_TOPIC,
                dto
        );
    }
}