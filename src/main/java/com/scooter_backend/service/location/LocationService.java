package com.scooter_backend.service.location;

import com.scooter_backend.dto.scooter.ScooterLocationMessage;
import com.scooter_backend.service.redis.RedisPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import org.springframework.data.geo.Point;
import java.time.Duration;
import java.util.Map;

@Service
public class LocationService {

    private final RedisPublisher redisPublisher;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LOCATION_KEY = "scooter:location:";
    private static final long THROTTLE_MS = 1500;

    public LocationService(RedisPublisher redisPublisher,
                           RedisTemplate<String, Object> redisTemplate) {
        this.redisPublisher = redisPublisher;
        this.redisTemplate = redisTemplate;
    }

    public void updateLocation(Long scooterId, double lat, double lon) {

        if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
            throw new IllegalArgumentException("Invalid coordinates");
        }

        long now = System.currentTimeMillis();
        String key = LOCATION_KEY + scooterId;
        String tsKey = key + ":ts";

        Object tsValue = redisTemplate.opsForValue().get(tsKey);
        Long lastUpdate = tsValue instanceof Long ? (Long) tsValue : null;

        if (lastUpdate != null && (now - lastUpdate) < THROTTLE_MS) {
            return;
        }

        Map<String, Object> location = Map.of(
                "lat", lat,
                "lon", lon,
                "ts", now
        );

        redisTemplate.opsForValue().set(key, location, Duration.ofMinutes(5));
        redisTemplate.opsForValue().set(tsKey, now, Duration.ofMinutes(5));

        redisTemplate.opsForGeo().add(
                "scooters",
                new Point(lon, lat),
                scooterId.toString()
        );

        redisPublisher.publishScooterLocation(
                new ScooterLocationMessage(scooterId, lat, lon, now)
        );
    }


}
