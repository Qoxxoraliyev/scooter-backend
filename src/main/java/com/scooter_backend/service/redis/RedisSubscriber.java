package com.scooter_backend.service.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scooter_backend.dto.scooter.ScooterLocationMessage;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public RedisSubscriber(ObjectMapper objectMapper, SimpMessagingTemplate messagingTemplate) {
        this.objectMapper = objectMapper;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            ScooterLocationMessage location = objectMapper.readValue(
                    message.getBody(),
                    ScooterLocationMessage.class
            );

            messagingTemplate.convertAndSend(
                    "/topic/scooters/location",
                    location
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
