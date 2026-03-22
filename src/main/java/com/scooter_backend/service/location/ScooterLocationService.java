package com.scooter_backend.service.location;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class ScooterLocationService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LOCATION_KEY_PREFIX = "scooter:location:";

    public ScooterLocationService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Map<Long, Map<String, Object>> getAllLocations() {

        Map<Long, Map<String, Object>> result = new HashMap<>();

        Set<String> keys = redisTemplate.keys(LOCATION_KEY_PREFIX + "*");

        if (keys == null || keys.isEmpty()) {
            return result;
        }

        for (String key : keys) {

            // ts keylarni tashlab yuboramiz
            if (key.endsWith(":ts")) {
                continue;
            }

            Object value = redisTemplate.opsForValue().get(key);

            if (!(value instanceof Map<?, ?> locationMap)) {
                continue;
            }

            try {
                String idStr = key.replace(LOCATION_KEY_PREFIX, "");
                Long scooterId = Long.parseLong(idStr);

                Map<String, Object> location = new HashMap<>();
                location.put("lat", locationMap.get("lat"));
                location.put("lon", locationMap.get("lon"));
                location.put("ts", locationMap.get("ts"));

                result.put(scooterId, location);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}