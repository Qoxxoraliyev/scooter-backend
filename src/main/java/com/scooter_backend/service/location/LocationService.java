package com.scooter_backend.service.location;

import com.scooter_backend.dto.scooter.ScooterLocationMessage;
import com.scooter_backend.entity.Scooter;
import com.scooter_backend.entity.ScooterLocation;
import com.scooter_backend.repository.ScooterLocationRepository;
import com.scooter_backend.repository.ScooterRepository;
import com.scooter_backend.service.redis.RedisPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import org.springframework.data.geo.Point;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class LocationService {

    private final RedisPublisher redisPublisher;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ScooterRepository scooterRepository;
    private final ScooterLocationRepository scooterLocationRepository;

    private static final String LOCATION_KEY = "scooter:location:";
    private static final long THROTTLE_MS = 1500;

    public LocationService(RedisPublisher redisPublisher,
                           RedisTemplate<String, Object> redisTemplate,
                           ScooterRepository scooterRepository,
                           ScooterLocationRepository scooterLocationRepository) {
        this.redisPublisher = redisPublisher;
        this.redisTemplate = redisTemplate;
        this.scooterRepository = scooterRepository;
        this.scooterLocationRepository = scooterLocationRepository;
    }

    public void updateLocation(Long scooterId, double lat, double lon) {

        if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
            throw new IllegalArgumentException("Invalid coordinates");
        }

        long now = System.currentTimeMillis();
        String key = LOCATION_KEY + scooterId;
        String tsKey = key + ":time";

        Object tsValue = redisTemplate.opsForValue().get(tsKey);
        Long lastUpdate = tsValue instanceof Long ? (Long) tsValue : null;

        if (lastUpdate != null && (now - lastUpdate) < THROTTLE_MS) {
            return;
        }

        Map<String, Object> location = Map.of(
                "lat", lat,
                "lon", lon,
                "time", now
        );

        redisTemplate.opsForValue().set(key, location, Duration.ofMinutes(5));
        redisTemplate.opsForValue().set(tsKey, now, Duration.ofMinutes(5));

        redisTemplate.opsForGeo().add(
                "scooters",
                new Point(lon, lat),
                scooterId.toString()
        );

        Scooter scooter = scooterRepository.findById(scooterId)
                .orElseThrow(() -> new RuntimeException("Scooter not found"));

        ScooterLocation scooterLocation = scooterLocationRepository.findByScooter_Id(scooterId)
                .orElseGet(ScooterLocation::new);

        scooterLocation.setScooter(scooter);
        scooterLocation.setLatitude(lat);
        scooterLocation.setLongitude(lon);


        scooterLocationRepository.save(scooterLocation);

        redisPublisher.publishScooterLocation(
                new ScooterLocationMessage(scooterId, lat, lon, now)
        );
    }
}