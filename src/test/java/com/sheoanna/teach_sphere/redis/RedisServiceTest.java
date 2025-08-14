package com.sheoanna.teach_sphere.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {
    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private RedisService redisService;

    @BeforeEach
    void setUp() {
        redisService = new RedisService(redisTemplate);
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void saveTokenTest_shouldCallSetWithTTL() {
        String token = "some-token";
        redisService.saveToken(token);
        verify(valueOperations)
                .set(eq("valid:" + token), eq("valid"), eq(7L), eq(TimeUnit.DAYS));
    }

    @Test
    void setTokenWithTTLTest_shouldCallRedisTemplate() {
        redisService.setTokenWithTTL("key", "value", 10, TimeUnit.MINUTES);
        verify(valueOperations).set("key", "value", 10, TimeUnit.MINUTES);
    }

    @Test
    void isBlacklistedTest_shouldReturnTrueIfBlacklisted() {
        String token = "blacklisted-token";
        when(valueOperations.get(token)).thenReturn("blacklisted");
        boolean result = redisService.isBlacklisted(token);
        assertTrue(result);
    }

    @Test
    void isBlacklistedTest_shouldReturnFalseIfNotBlacklisted() {
        String token = "valid-token";
        when(valueOperations.get(token)).thenReturn("valid");
        boolean result = redisService.isBlacklisted(token);
        assertFalse(result);
    }

    @Test
    void hasTokenTest_shouldReturnTrueIfKeyExists() {
        String token = "token";
        when(redisTemplate.hasKey(token)).thenReturn(true);
        boolean result = redisService.hasToken(token);
        assertTrue(result);
    }

    @Test
    void hasTokenTest_shouldReturnFalseIfKeyDoesNotExist() {
        String token = "token";
        when(redisTemplate.hasKey(token)).thenReturn(false);
        boolean result = redisService.hasToken(token);
        assertFalse(result);
    }
}
