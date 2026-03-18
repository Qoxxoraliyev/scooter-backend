package com.scooter_backend.service.driver;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Service
public class DriverPresenceService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String ONLINE_DRIVERS_KEY = "drivers:online";
    private static final long TTL_MINUTES = 5;

    public DriverPresenceService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setOnline(Long driverId) {

        redisTemplate.opsForSet().add(ONLINE_DRIVERS_KEY, driverId);

        redisTemplate.expire(ONLINE_DRIVERS_KEY, Duration.ofMinutes(TTL_MINUTES));
    }

    public void setOffline(Long driverId) {

        redisTemplate.opsForSet().remove(ONLINE_DRIVERS_KEY, driverId);
    }

    public Set<Long> getOnlineDrivers() {

        Set<Object> members = redisTemplate.opsForSet().members(ONLINE_DRIVERS_KEY);

        if (members == null || members.isEmpty()) {
            return Set.of();
        }

        Set<Long> result = new HashSet<>();

        for (Object obj : members) {
            if (obj instanceof Long id) {
                result.add(id);
            } else if (obj instanceof Integer id) {
                result.add(id.longValue());
            } else if (obj instanceof String id) {
                result.add(Long.parseLong(id));
            }
        }

        return result;
    }
}