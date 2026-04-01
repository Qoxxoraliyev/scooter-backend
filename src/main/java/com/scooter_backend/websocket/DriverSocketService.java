package com.scooter_backend.websocket;

import com.scooter_backend.dto.driver.DriverLocationDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


@Service
public class DriverSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    private static final String DRIVER_LOCATION_TOPIC="/topic/drivers/location";

    public DriverSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendLocationUpdate(DriverLocationDTO dto) {
        messagingTemplate.convertAndSend(DRIVER_LOCATION_TOPIC, dto);
    }
}
