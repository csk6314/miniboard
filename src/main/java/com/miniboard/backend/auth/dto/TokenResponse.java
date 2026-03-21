package com.miniboard.backend.auth.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
