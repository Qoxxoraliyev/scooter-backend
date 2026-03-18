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

@Service
public class LocationService {

    private final RedisPublisher redisPublisher;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ScooterRepository scooterRepository;
    private final ScooterLocationRepository locationRepository;

    public LocationService(RedisPublisher redisPublisher, RedisTemplate<String, Object> redisTemplate, ScooterRepository scooterRepository, ScooterLocationRepository locationRepository) {
        this.redisPublisher = redisPublisher;
        this.redisTemplate = redisTemplate;
        this.scooterRepository = scooterRepository;
        this.locationRepository = locationRepository;
    }

    private static final String LOCATION_CACHE_KEY = "scooter:location:";
    private static final long THROTTLE_MS = 1500; // 1.5 sec

    public void updateLocation(Long scooterId, double lat, double lon) {

        if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
            throw new IllegalArgumentException("Invalid coordinates");
        }

        long now = System.currentTimeMillis();
        String key = LOCATION_CACHE_KEY + scooterId;

        Long lastUpdate = (Long) redisTemplate.opsForValue().get(key + ":ts");

        if (lastUpdate != null && (now - lastUpdate) < THROTTLE_MS) {
            return;
        }

        redisTemplate.opsForValue().set(key + ":lat", lat);
        redisTemplate.opsForValue().set(key + ":lon", lon);
        redisTemplate.opsForValue().set(key + ":ts", now);

        // TTL (optional, clean cache)
        redisTemplate.expire(key + ":lat", Duration.ofMinutes(5));
        redisTemplate.expire(key + ":lon", Duration.ofMinutes(5));
        redisTemplate.expire(key + ":ts", Duration.ofMinutes(5));

        Scooter scooter = scooterRepository.findById(scooterId)
                .orElseThrow(() -> new RuntimeException("Scooter not found"));

        ScooterLocation location = scooter.getLocation();

        if (location == null) {
            location = new ScooterLocation();
            location.setScooter(scooter);
        }

        location.setLatitude(lat);
        location.setLongitude(lon);

        locationRepository.save(location);

        redisTemplate.opsForGeo().add(
                "scooters",
                new Point(lon, lat),
                scooterId.toString()
        );

        ScooterLocationMessage message = new ScooterLocationMessage(
                scooterId,
                lat,
                lon,
                now
        );

        redisPublisher.publishScooterLocation(message);
    }
}
