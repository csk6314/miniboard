package com.miniboard.backend.auth.jwt;

import com.miniboard.backend.auth.dto.TokenResponseDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Getter
    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public TokenResponseDto generateTokens(Long userId) {
        String accessToken = generateAccessToken(userId);
        String refreshToken = generateRefreshToken(userId);
        return new TokenResponseDto(accessToken, refreshToken);
    }

    public String generateAccessToken(Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", TokenType.ACCESS_TOKEN.getTypeName())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", TokenType.REFRESH_TOKEN.getTypeName())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    public Long getUserId(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    public String getTokenType(String token) {
        Claims claims = parseClaims(token);
        return claims.get("type", String.class);
    }

    public Claims validateTokenOrThrow(String token) {

        if (token == null || token.isBlank()) {
            throw new JwtAuthenticationException(JwtErrorCode.EMPTY_TOKEN);
        }

        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new JwtAuthenticationException(JwtErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new JwtAuthenticationException(JwtErrorCode.UNSUPPORTED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException(JwtErrorCode.INVALID_TOKEN);
        }
    }

    public void validateAccessTokenOrThrow(String token) {
        Claims claims = validateTokenOrThrow(token);
        String type = claims.get("type", String.class);

        if (!TokenType.ACCESS_TOKEN.getTypeName().equals(type)) {
            throw new JwtAuthenticationException(JwtErrorCode.INVALID_TOKEN_TYPE);
        }

    }

    public void validateRefreshTokenOrThrow(String token) {
        Claims claims = validateTokenOrThrow(token);
        String type = claims.get("type", String.class);

        if (!TokenType.REFRESH_TOKEN.getTypeName().equals(type)) {
            throw new JwtAuthenticationException(JwtErrorCode.INVALID_TOKEN_TYPE);
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            validateRefreshTokenOrThrow(token);
            return true;
        } catch (JwtAuthenticationException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}
