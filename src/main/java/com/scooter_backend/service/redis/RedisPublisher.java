package com.scooter_backend.service.redis;

import com.scooter_backend.constant.RedisChannels;
import com.scooter_backend.dto.scooter.ScooterLocationMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisPublisher(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void publishScooterLocation(ScooterLocationMessage message) {
        redisTemplate.convertAndSend(
                RedisChannels.SCOOTER_LOCATION,
                message
        );
    }

}
