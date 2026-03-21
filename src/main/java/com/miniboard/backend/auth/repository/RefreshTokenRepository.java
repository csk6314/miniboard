package com.miniboard.backend.auth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private static final String PREFIX = "refresh: ";

    private final StringRedisTemplate redisTemplate;

    public void save(Long userId, String refreshToken, long expirationMillis) {
        String key = generateKey(userId);

        redisTemplate.opsForValue().set(
                key,
                refreshToken,
                Duration.ofMillis(expirationMillis)
        );
    }

    public String findByUserId(Long userId) {
        String key = generateKey(userId);
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(Long userId) {
        String key = generateKey(userId);
        redisTemplate.delete(key);
    }

    public boolean isSameToken(Long userId, String refreshToken) {
        String savedToken = findByUserId(userId);
        return refreshToken != null && refreshToken.equals(savedToken);
    }

    private String generateKey(Long userId) {
        return PREFIX + userId;
    }
}
