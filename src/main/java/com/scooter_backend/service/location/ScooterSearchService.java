package com.scooter_backend.service.location;

import com.scooter_backend.entity.Scooter;
import com.scooter_backend.enums.ScooterStatus;
import com.scooter_backend.repository.ScooterRepository;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScooterSearchService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String GEO_KEY = "scooters";

    private final ScooterRepository scooterRepository;

    public ScooterSearchService(RedisTemplate<String, Object> redisTemplate,
                                ScooterRepository scooterRepository) {
        this.redisTemplate = redisTemplate;
        this.scooterRepository = scooterRepository;
    }

    public List<Long> findNearestScooters(double lat, double lon, double radiusKm) {

        Circle area = new Circle(
                new Point(lon, lat),
                new Distance(radiusKm, Metrics.KILOMETERS)
        );

        GeoResults<RedisGeoCommands.GeoLocation<Object>> results =
                redisTemplate.opsForGeo().radius(GEO_KEY, area);

        if (results == null) {
            return List.of();
        }

        return results.getContent()
                .stream()
                .map(r -> Long.parseLong(r.getContent().getName().toString()))
                .collect(Collectors.toList());
    }

    public Long findNearestOne(double lat, double lon) {
        List<Long> scooters = findNearestScooters(lat, lon, 5);
        return scooters.isEmpty() ? null : scooters.get(0);
    }

    public Scooter findNearestAvailable(double lat, double lon) {

        List<Long> ids = findNearestScooters(lat, lon, 5);

        return ids.stream()
                .map(id -> scooterRepository.findById(id).orElse(null))
                .filter(s -> s != null)
                .filter(s -> !Boolean.TRUE.equals(s.getLocked()))
                .filter(s -> !Boolean.TRUE.equals(s.getDeleted()))
                .filter(s -> s.getStatus() == ScooterStatus.ACTIVE)
                .findFirst()
                .orElse(null);
    }




}