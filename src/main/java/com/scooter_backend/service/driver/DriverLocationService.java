package com.scooter_backend.service.driver;

import com.scooter_backend.dto.user.AdminDriverLocationResponseDTO;
import com.scooter_backend.dto.driver.DriverLocationDTO;
import com.scooter_backend.dto.driver.DriverLocationUpdateDTO;
import com.scooter_backend.entity.Driver;
import com.scooter_backend.entity.DriverLocation;
import com.scooter_backend.repository.DriverLocationRepository;
import com.scooter_backend.repository.DriverRepository;
import com.scooter_backend.websocket.DriverSocketService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class DriverLocationService {

    private final DriverRepository driverRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final DriverSocketService driverSocketService;
    private final DriverPresenceService driverPresenceService;
    private final DriverLocationRepository driverLocationRepository;

    private static final String DRIVER_LOCATION_KEY = "driver:location:";
    private static final long THROTTLE_MS = 1500;

    public DriverLocationService(DriverRepository driverRepository,
                                 RedisTemplate<String, Object> redisTemplate,
                                 DriverSocketService driverSocketService,
                                 DriverPresenceService driverPresenceService,
                                 DriverLocationRepository driverLocationRepository) {
        this.driverRepository = driverRepository;
        this.redisTemplate = redisTemplate;
        this.driverSocketService = driverSocketService;
        this.driverPresenceService = driverPresenceService;
        this.driverLocationRepository = driverLocationRepository;
    }

    public void updateDriverLocation(Long driverId, DriverLocationUpdateDTO dto) {

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        long now = System.currentTimeMillis();
        String key = DRIVER_LOCATION_KEY + driver.getId();
        String tsKey = key + ":ts";

        Object tsValue = redisTemplate.opsForValue().get(tsKey);
        Long lastUpdate = extractLong(tsValue);

        if (lastUpdate != null && (now - lastUpdate) < THROTTLE_MS) {
            return;
        }

        Map<String, Object> location = new HashMap<>();
        location.put("lat", dto.lat());
        location.put("lon", dto.lon());
        location.put("ts", now);

        redisTemplate.opsForValue().set(key, location, Duration.ofMinutes(5));
        redisTemplate.opsForValue().set(tsKey, now, Duration.ofMinutes(5));

        driverPresenceService.setOnline(driverId);

        DriverLocation driverLocation = new DriverLocation();
        driverLocation.setDriver(driver);
        driverLocation.setLatitude(dto.lat());
        driverLocation.setLongitude(dto.lon());

        driverLocationRepository.save(driverLocation);

        driverSocketService.sendLocationUpdate(
                new DriverLocationDTO(driver.getId(), dto.lat(), dto.lon(), now)
        );
    }

    @Transactional(readOnly = true)
    public AdminDriverLocationResponseDTO getLastLocationByDriverId(Long driverId) {

        driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        String key = DRIVER_LOCATION_KEY + driverId;
        Object value = redisTemplate.opsForValue().get(key);

        if (value instanceof Map<?, ?> map) {
            Double lat = map.get("lat") != null ? Double.valueOf(map.get("lat").toString()) : null;
            Double lon = map.get("lon") != null ? Double.valueOf(map.get("lon").toString()) : null;
            Long ts = map.get("ts") != null ? Long.valueOf(map.get("ts").toString()) : null;

            return new AdminDriverLocationResponseDTO(driverId, lat, lon, ts);
        }

        DriverLocation lastLocation = driverLocationRepository
                .findTopByDriverIdOrderByCreatedAtDesc(driverId)
                .orElseThrow(() -> new RuntimeException("Driver location not found"));

        Long ts = lastLocation.getCreatedAt()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        return new AdminDriverLocationResponseDTO(
                driverId,
                lastLocation.getLatitude(),
                lastLocation.getLongitude(),
                ts
        );
    }

    private Long extractLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long l) {
            return l;
        }
        if (value instanceof Integer i) {
            return i.longValue();
        }
        if (value instanceof String s) {
            return Long.valueOf(s);
        }
        return null;
    }
}