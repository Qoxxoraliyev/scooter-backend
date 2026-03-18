package com.scooter_backend.websocket;

import com.scooter_backend.dto.ride.RideResponseDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class RideSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public RideSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    private static final String DESTINATION = "/topic/rides/";

    public void sendRideUpdate(RideResponseDTO dto) {

        messagingTemplate.convertAndSend(
                DESTINATION + dto.id(),
                dto
        );
    }

    public void sendRideStarted(RideResponseDTO dto) {

        messagingTemplate.convertAndSend(
                DESTINATION + dto.id(),
                dto
        );
    }

    public void sendRideFinished(RideResponseDTO dto) {

        messagingTemplate.convertAndSend(
                DESTINATION + dto.id(),
                dto
        );
    }
}