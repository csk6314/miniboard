package com.miniboard.backend.auth.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory()
                .getConnection()
                .flushDb();
    }

    @Test
    @DisplayName("refresh token을 저장하고 조회할 수 있다")
    void saveAndFind() {
        Long userId = 1L;
        String refreshToken = "test-refresh-token";
        long expirationMillis = 1000 * 60 * 60;

        refreshTokenService.save(userId, refreshToken, expirationMillis);

        String savedToken = refreshTokenService.findByUserId(userId);

        assertThat(savedToken).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("refresh token을 삭제할 수 있다")
    void delete() {
        Long userId = 2L;
        String refreshToken = "test-refresh-token";
        long expirationMillis = 1000 * 60 * 60;

        refreshTokenService.save(userId, refreshToken, expirationMillis);
        refreshTokenService.delete(userId);

        String savedToken = refreshTokenService.findByUserId(userId);

        assertThat(savedToken).isNull();
    }

    @Test
    @DisplayName("저장된 refresh token과 같은지 비교할 수 있다")
    void isSameToken() {
        Long userId = 3L;
        String refreshToken = "test-refresh-token";
        long expirationMillis = 1000 * 60 * 60;

        refreshTokenService.save(userId, refreshToken, expirationMillis);

        assertThat(refreshTokenService.isSameToken(userId, refreshToken)).isTrue();
        assertThat(refreshTokenService.isSameToken(userId, "another-token")).isFalse();
    }
}