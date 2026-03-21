package com.miniboard.backend.auth.jwt;

import com.miniboard.backend.auth.dto.TokenResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JwtTokenProviderTest {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("access token과 refresh token을 생성할 수 있다")
    void generateTokens() {
        TokenResponseDto tokenResponse = jwtTokenProvider.generateTokens(1L);

        assertThat(tokenResponse.accessToken()).isNotBlank();
        assertThat(tokenResponse.refreshToken()).isNotBlank();
    }

    @Test
    @DisplayName("토큰에서 userId를 추출할 수 있다")
    void getUserId() {
        String accessToken = jwtTokenProvider.generateAccessToken(1L);

        Long userId = jwtTokenProvider.getUserId(accessToken);

        assertThat(userId).isEqualTo(1L);
    }

    @Test
    @DisplayName("access token 타입을 확인할 수 있다")
    void getTokenType() {
        String accessToken = jwtTokenProvider.generateAccessToken(1L);
        String refreshToken = jwtTokenProvider.generateRefreshToken(1L);

        assertThat(jwtTokenProvider.getTokenType(accessToken)).isEqualTo("access");
        assertThat(jwtTokenProvider.getTokenType(refreshToken)).isEqualTo("refresh");
    }

    @Test
    @DisplayName("정상 토큰은 검증에 성공한다")
    void validateToken() {
        String accessToken = jwtTokenProvider.generateAccessToken(1L);

        boolean result = jwtTokenProvider.validateToken(accessToken);

        assertThat(result).isTrue();
    }
}