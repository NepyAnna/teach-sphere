package com.sheoanna.teach_sphere.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    private final StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveToken(String token) {
        long ttl = 7;
        TimeUnit timeUnit = TimeUnit.DAYS;

        setTokenWithTTL("valid:" + token, "valid", ttl, timeUnit);
    }

    public void setTokenWithTTL(String key, String value, long ttl, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, ttl, timeUnit);
    }

    public boolean isBlacklisted(String token) {
        String status = redisTemplate.opsForValue().get(token);
        return "blacklisted".equals(status);
    }

    public boolean hasToken(String token) {
        return redisTemplate.hasKey(token);
    }
}
