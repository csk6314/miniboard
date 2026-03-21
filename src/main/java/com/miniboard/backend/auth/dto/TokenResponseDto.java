package com.miniboard.backend.auth.dto;

public record TokenResponseDto(
        String accessToken,
        String refreshToken
) {
}
